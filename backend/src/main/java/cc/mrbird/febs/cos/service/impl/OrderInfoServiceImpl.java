package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.common.domain.SystemConstant;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.cos.dao.EvaluationMapper;
import cc.mrbird.febs.cos.dao.ShopCartMapper;
import cc.mrbird.febs.cos.dao.ShopInfoMapper;
import cc.mrbird.febs.cos.entity.*;
import cc.mrbird.febs.cos.dao.OrderInfoMapper;
import cc.mrbird.febs.cos.entity.vo.OrderDetailVo;
import cc.mrbird.febs.cos.entity.vo.OrderInfoVo;
import cc.mrbird.febs.cos.entity.vo.OrderItemVo;
import cc.mrbird.febs.cos.entity.vo.OrderSettlementVo;
import cc.mrbird.febs.cos.service.*;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author FanK
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements IOrderInfoService {

    private final IProductInfoService productInfoService;

    private final IAddressInfoService addressInfoService;

    private final IShopCartService shopCartService;

    private final EvaluationMapper evaluationMapper;

    private final ShopInfoMapper shopInfoMapper;

    private final IOrderItemService orderItemService;

    private final IUserInfoService userInfoService;

    @Override
    public IPage<LinkedHashMap<String, Object>> orderInfoByPage(Page page, OrderInfo orderInfo) {
        return baseMapper.orderInfoByPage(page, orderInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String orderSettlement(String cardListStr, List<Integer> ids) {
        // 修改购买数量
        List<ShopCart> shopCartListStr = JSONUtil.toList(cardListStr, ShopCart.class);
        shopCartService.updateBatchById(shopCartListStr);
        // 添加未支付订单
        List<ShopCart> shopCartList = shopCartService.list(Wrappers.<ShopCart>lambdaQuery().in(ShopCart::getId, ids));
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setDeleteFlag(0);
        // 订单编号
        orderInfo.setCode("ORD-"+new Date().getTime());
        // 订单状态
        orderInfo.setOrderStatus(1);
        // 下单时间
        orderInfo.setPlaceOrderDate(DateUtil.formatDateTime(new Date()));
        // 所属用户
        orderInfo.setUserCode(shopCartList.get(0).getUserCode());
        // 添加地址
        AddressInfo addressInfo = addressInfoService.getOne(Wrappers.<AddressInfo>lambdaQuery().eq(AddressInfo::getUserCode, shopCartList.get(0).getUserCode()).eq(AddressInfo::getDefaultAddress, 1).eq(AddressInfo::getDeleteFlag, 0));
        if (addressInfo != null) {
            orderInfo.setAddressCode(addressInfo.getCode());
        }

        // 添加订单项
        List<OrderItem> orderItems = new ArrayList<>();
        for (ShopCart shopCart : shopCartList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderCode(orderInfo.getCode());
            // 商品编号
            orderItem.setProductCode(shopCart.getProductCode());
            // 下单数量
            orderItem.setAmount(shopCart.getQuantity());
            // 单价
            orderItem.setUnitPrice(productInfoService.getOne(Wrappers.<ProductInfo>lambdaQuery().eq(ProductInfo::getCode, shopCart.getProductCode())).getPrice());
            // 总价
            orderItem.setAllPrice(orderItem.getUnitPrice().multiply(new BigDecimal(orderItem.getAmount())));
            orderItems.add(orderItem);
        }
        // 计算订单价格
        orderInfo.setAllPrice(orderItems.stream().map(fd -> fd.getAllPrice()).reduce(BigDecimal.ZERO, BigDecimal::add));
        this.save(orderInfo);
        orderItemService.saveBatch(orderItems);

        // 删除购物车
        shopCartService.update(Wrappers.<ShopCart>lambdaUpdate().set(ShopCart::getDeleteFlag, 1).in(ShopCart::getId, ids));
        return orderInfo.getCode();
    }

    @Override
    public LinkedHashMap<String, Object> orderDetailByCode(String orderCode) {
        OrderInfo orderInfo = this.getOne(Wrappers.<OrderInfo>lambdaQuery().eq(OrderInfo::getCode, orderCode));
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>() {
            {
                put("orderInfo", orderInfo);
            }
        };
        List<OrderSettlementVo> orderItem = new ArrayList<>();
        List<LinkedHashMap<String, Object>> productList = baseMapper.orderItemByCode(orderCode);
        List<String> productCodes = new ArrayList<>();
        // 获取商家信息
        for (LinkedHashMap<String, Object> item : productList) {
            productCodes.add(item.get("code").toString());
        }
        // 获取商品详情
        List<LinkedHashMap<String, Object>> shopIds = shopInfoMapper.shopListByProduct(productCodes);
        for (LinkedHashMap<String, Object> shop : shopIds) {
            OrderSettlementVo orderSettlementVo = new OrderSettlementVo();
            orderSettlementVo.setCode(shop.get("code").toString());
            orderSettlementVo.setName(shop.get("name").toString());
            orderSettlementVo.setProductList(productList.stream().filter(a -> a.get("shopCode").toString().equals(shop.get("code").toString())).collect(Collectors.toList()));
            orderItem.add(orderSettlementVo);
        }
        // 收货信息
        if (StrUtil.isNotEmpty(orderInfo.getAddressCode())) {
            result.put("address", addressInfoService.getOne(Wrappers.<AddressInfo>lambdaQuery().eq(AddressInfo::getCode, orderInfo.getAddressCode())));
        }
        result.put("orderItem", orderItem);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean orderSave(OrderInfoVo orderInfo) throws Exception{
        // 判断地址是否可用
        if (StrUtil.isNotEmpty(orderInfo.getCode())) {
            orderInfo.setCode("ORD-"+new Date().getTime());
        }
        // 开始计算价格
        List<OrderItem> orderItemList = JSONUtil.toList(orderInfo.getOrderItem(), OrderItem.class);
        // 重新计算价格
        BigDecimal allPriceNew = new BigDecimal(0);
        for (OrderItem item : orderItemList) {
            item.setOrderCode(orderInfo.getCode());
            ProductInfo productInfo = productInfoService.getOne(Wrappers.<ProductInfo>lambdaQuery().eq(ProductInfo::getCode, item.getProductCode()));
            item.setUnitPrice(productInfo.getPrice());
            item.setAllPrice(productInfo.getPrice().multiply(new BigDecimal(item.getAmount())));
            allPriceNew = allPriceNew.add(productInfo.getPrice().multiply(new BigDecimal(item.getAmount())));
        }
        orderInfo.setAllPrice(allPriceNew);
        orderInfo.setOrderStatus(0);
        // 订单详细项
        orderItemService.saveOrUpdateBatch(orderItemList);
        orderInfo.setPlaceOrderDate(DateUtil.formatDateTime(new Date()));
        orderInfo.setDeleteFlag(SystemConstant.DELETE_FLAG);
        return this.saveOrUpdate(orderInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean orderPay(OrderInfoVo orderInfo) throws Exception {
        List<OrderItem> orderItemList = JSONUtil.toList(orderInfo.getOrderItem(), OrderItem.class);
        // 判断单价是否变化
        BigDecimal allPrice = orderItemList.stream().map(a -> a.getUnitPrice().multiply(new BigDecimal(a.getAmount()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        // 重新计算价格
        BigDecimal allPriceNew = new BigDecimal(0);
        for (OrderItem item : orderItemList) {
            item.setOrderCode(orderInfo.getCode());
            ProductInfo productInfo = productInfoService.getOne(Wrappers.<ProductInfo>lambdaQuery().eq(ProductInfo::getCode, item.getProductCode()));
            allPriceNew = allPriceNew.add(productInfo.getPrice().multiply(new BigDecimal(item.getAmount())));
        }
        orderInfo.setPayType(1);
        orderInfo.setOrderStatus(1);
        orderInfo.setAllPrice(allPriceNew);
        return this.updateById(orderInfo);
    }

    @Override
    public List<LinkedHashMap<String, Object>> orderListByUser(String userCode, Integer status) {
        return null;
    }

    @Override
    public LinkedHashMap<String, Object> homeData() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        // 用户数量
        result.put("userNum", userInfoService.count());
        List<OrderInfo> orderInfoList = this.list(Wrappers.<OrderInfo>lambdaQuery().ne(OrderInfo::getOrderStatus, 1));
        // 订单总量
        result.put("orderNum", orderInfoList.size());
        // 交易总金额
        result.put("orderPrice", orderInfoList.stream().map(fd -> fd.getAllPrice()).reduce(BigDecimal.ZERO, BigDecimal::add));
        // 七天内订单量统计
        result.put("orderVolumeStatistics", baseMapper.orderVolumeStatistics());
        // 本月订单量，收益统计
        result.put("orderStatisticsByCurrentMonth", baseMapper.orderStatisticsByCurrentMonth());
        // 本月收益类型占比
        result.put("orderStatisticsByCurrentMonthRate", baseMapper.orderStatisticsByCurrentMonthRate());
        // 代发货订单
        result.put("pendingOrder", baseMapper.pendingOrder());
        // 月统计订单量 收益额
        result.put("orderDataByMonth", baseMapper.orderDataByMonth(DateUtil.formatDate(new Date())));
        return result;
    }

    @Override
    public List<LinkedHashMap<String, Object>> orderDataByMonth(String date) {
        return baseMapper.orderDataByMonth(date);
    }

    @Override
    public List<OrderDetailVo> orderListByUser(String userCode) {
        // 返回数据
        List<OrderDetailVo> result = new ArrayList<>();
        List<OrderItemVo> orderItem = baseMapper.orderListByUser(userCode);
        List<OrderInfo> orderInfoList = this.list(Wrappers.<OrderInfo>lambdaQuery().eq(OrderInfo::getUserCode, userCode).eq(OrderInfo::getDeleteFlag, 0));
        for (OrderInfo orderInfo : orderInfoList) {
            OrderDetailVo orderDetailVo = new OrderDetailVo();
            orderDetailVo.setId(orderInfo.getId());
            // 评价
            List<Evaluation> evaluationList = evaluationMapper.evaluationByOrder(orderInfo.getCode());
            orderDetailVo.setEvaluationStatus(evaluationList.size() != 0 ? 1 : 0);
            if (evaluationList.size() != 0) {
                orderDetailVo.setEvaluationContent(evaluationList.get(0).getContent());
            }
            // 总价格
            orderDetailVo.setAllPrice(orderInfo.getAllPrice());
            // 订单号
            orderDetailVo.setOrderCode(orderInfo.getCode());
            // 订单状态
            orderDetailVo.setOrderStatus(orderInfo.getOrderStatus());
            // 商品项
            orderDetailVo.setOrderItem(orderItem.stream().filter(a -> a.getOrderCode().equals(orderInfo.getCode())).collect(Collectors.toList()));
            orderDetailVo.setShopCode(orderDetailVo.getOrderItem().get(0).getShopCode());
            orderDetailVo.setShopName(orderDetailVo.getOrderItem().get(0).getShopName());
            result.add(orderDetailVo);
        }
        return result;
    }

}

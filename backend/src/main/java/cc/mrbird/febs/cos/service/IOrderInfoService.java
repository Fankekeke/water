package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.OrderInfo;
import cc.mrbird.febs.cos.entity.vo.OrderDetailVo;
import cc.mrbird.febs.cos.entity.vo.OrderInfoVo;
import cc.mrbird.febs.cos.entity.vo.OrderSettlementVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface IOrderInfoService extends IService<OrderInfo> {

    // 分页查询订单信息
    IPage<LinkedHashMap<String, Object>> orderInfoByPage(Page page, OrderInfo orderInfo);

    // 购物车订单结算
    String orderSettlement(String cardListStr, List<Integer> ids);

    // 获取订单详情
    LinkedHashMap<String, Object> orderDetailByCode(String orderCode);

    // 添加用户订单
    Boolean orderSave(OrderInfoVo orderInfoVo) throws Exception;

    // 订单付款
    Boolean orderPay(OrderInfoVo orderInfoVo) throws Exception;

    // 根据用户获取订单信息
    List<LinkedHashMap<String, Object>> orderListByUser(String userCode, Integer status);

    // 获取首页信息
    LinkedHashMap<String, Object> homeData();

    // 月统计订单量 收益额
    List<LinkedHashMap<String, Object>> orderDataByMonth(String date);

    // 获取用户订单
    List<OrderDetailVo> orderListByUser(String userCode);

}

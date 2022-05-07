package cc.mrbird.febs.cos.controller.wechat;

import cc.mrbird.febs.common.domain.SystemConstant;
import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.*;
import cc.mrbird.febs.cos.service.*;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/wechat/api")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebController {

    private final IAddressInfoService addressInfoService;

    private final IOrderInfoService orderInfoService;

    private final IUserInfoService userInfoService;

    private final IShopCartService shopCartService;

    private final IProductInfoService productInfoService;

    private final IBulletinInfoService bulletinInfoService;

    private final IShopInfoService shopInfoService;

    private final IEvaluationService evaluationService;

    /**
     * 用户授权登陆
     * @param user 用户参数信息
     * @return 当前用户对象
     * @throws Exception 异常
     */
    @PostMapping("/userAdd")
    public R userAdd(@RequestBody UserInfo user) throws Exception {
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        url += "?appid=wx76a6577665633a86";//自己的appid
        url += "&secret=78070ccedf3f17b272b84bdeeca28a2e";//自己的appSecret
        url += "&js_code=" + user.getOpenId();
        url += "&grant_type=authorization_code";
        url += "&connect_redirect=1";
        String res = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);    //GET方式
        CloseableHttpResponse response = null;
        // 配置信息
        RequestConfig requestConfig = RequestConfig.custom()          // 设置连接超时时间(单位毫秒)
                .setConnectTimeout(5000)                    // 设置请求超时时间(单位毫秒)
                .setConnectionRequestTimeout(5000)             // socket读写超时时间(单位毫秒)
                .setSocketTimeout(5000)                    // 设置是否允许重定向(默认为true)
                .setRedirectsEnabled(false).build();           // 将上面的配置信息 运用到这个Get请求里
        httpget.setConfig(requestConfig);                         // 由客户端执行(发送)Get请求
        response = httpClient.execute(httpget);                   // 从响应模型中获取响应实体
        HttpEntity responseEntity = response.getEntity();
        System.out.println("响应状态为:" + response.getStatusLine());
        if (responseEntity != null) {
            res = EntityUtils.toString(responseEntity);
            System.out.println("响应内容长度为:" + responseEntity.getContentLength());
            System.out.println("响应内容为:" + res);
        }
        // 释放资源
        httpClient.close();
        response.close();
        String openid = JSON.parseObject(res).get("openid").toString();
        System.out.println("openid" + openid);
        int count = userInfoService.count(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getOpenId, openid));
        if (count > 0) {
            return R.ok(userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getOpenId, openid)));
        } else {
            user.setOpenId(openid);
            user.setCreateDate(DateUtil.formatDateTime(new Date()));
            user.setCode("U-" + new Date().getTime());
            user.setType(1);
            userInfoService.save(user);
            return R.ok(user);
        }
    }

    /**
     * 根据用户获取收货地址
     *
     * @param userCode 用户编号
     * @return 收货地址列表
     */
    @GetMapping("/address/{userCode}")
    public R addressListByUser(@PathVariable("userCode") String userCode) {
        return R.ok(addressInfoService.list(Wrappers.<AddressInfo>lambdaQuery().eq(AddressInfo::getUserCode, userCode).eq(AddressInfo::getDeleteFlag, 0)));
    }

    /**
     * 根据地址编号获取地址详情
     * @param addressId 地址编号
     * @return 地址信息
     */
    @GetMapping("/address/info/{addressId}")
    public R addressByCode(@PathVariable("addressId") Integer addressId) {
        return R.ok(addressInfoService.getOne(Wrappers.<AddressInfo>lambdaQuery().eq(AddressInfo::getId, addressId)));
    }

    /**
     * 新增收货地址
     * @param addressInfo 收货地址
     * @return 结果
     */
    @PostMapping("/address")
    public R addressSave(@RequestBody AddressInfo addressInfo) {
        return R.ok(addressInfoService.addressSave(addressInfo));
    }

    /**
     * 修改收货地址
     * @param addressInfo 收货地址
     * @return 结果
     */
    @PutMapping("/address")
    public R addressEdit(@RequestBody AddressInfo addressInfo) {
        return R.ok(addressInfoService.addressEdit(addressInfo));
    }

    /**
     * 删除收货地址
     * @param addressId 收货地址编号
     * @return 结果
     */
    @DeleteMapping("/address/{addressId}")
    public R addressRemove(@PathVariable("addressId") Integer addressId) {
        return R.ok(addressInfoService.update(Wrappers.<AddressInfo>lambdaUpdate().set(AddressInfo::getDeleteFlag, 1).eq(AddressInfo::getId, addressId)));
    }

    /**
     * 获取公告信息
     * @return 公告信息列表
     */
    @GetMapping("/bulletin/list")
    public R bulletinList() {
        return R.ok(bulletinInfoService.list());
    }

    /**
     * 获取公告详情
     * @param bulletinId 公告ID
     * @return 公告详情
     */
    @GetMapping("/bulletin/{bulletinId}")
    public R bulletinInfo(@PathVariable("bulletinId") Integer bulletinId) {
        return R.ok(bulletinInfoService.getById(bulletinId));
    }

    /**
     * 首页数据
     *
     * @return 首页信息
     */
    @GetMapping("/home")
    public R home() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>() {
            {
                // 商品信息
                put("product", productInfoService.productHot(4));
                // 店铺信息
                put("shop", shopInfoService.shopInfoHot(4));
            }
        };
        return R.ok(result);
    }

    /**
     * 商品详情及评价信息
     * @param productCode 商品编号
     * @return 商品详情信息
     */
    @GetMapping("/product/detail/{productCode}")
    public R productDetail(@PathVariable("productCode") String productCode) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>() {
            {
                // 商品详情
                put("productDetail", productInfoService.productDetail(productCode));
                // 商品评价
                put("productEvaluation", evaluationService.productEvaluation(productCode));
            }
        };
        return R.ok(result);
    }

    /**
     * 获取商家及产品信息
     * @return 商家及产品信息
     */
    @GetMapping("/shop/list")
    public R shopProductList() {
        return R.ok(shopInfoService.shopProductList());
    }

    /**
     * 获取商家详情
     * @param shopCode 商家编号
     * @return 商家详情
     */
    @GetMapping("/shop/detail/{shopCode}")
    public R shopDetail(@PathVariable("shopCode") String shopCode) {
        return R.ok(shopInfoService.getOne(Wrappers.<ShopInfo>lambdaQuery().eq(ShopInfo::getCode, shopCode)));
    }

    /**
     * 商家商品分类查询
     * @param sort 分类类型
     * @param shopCode 商家编号
     * @return 商品信息
     */
    @GetMapping("/product/sort")
    public R productSortList(Integer sort, String shopCode, String key) {
        return R.ok(productInfoService.productSortList(sort, shopCode, key));
    }

    /**
     * 模糊查询商品或商家
     * @param name 名称
     * @return 商品或商家
     */
    @GetMapping("/fuzzy")
    public R fuzzyQuery(String name) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>() {
            {
                put("shop", shopInfoService.list(Wrappers.<ShopInfo>lambdaQuery().like(StrUtil.isNotEmpty(name), ShopInfo::getName, name).eq(ShopInfo::getDeleteFlag, 0)));
                put("product", productInfoService.list(Wrappers.<ProductInfo>lambdaQuery().like(StrUtil.isNotEmpty(name), ProductInfo::getProductName, name).eq(ProductInfo::getDeleteFlag, 0)));
            }
        };
        return R.ok(result);
    }

    /**
     * 根据类型获取商品信息
     * @return 商品信息
     */
    @GetMapping("/product/type")
    public R productListByType() {
        return R.ok(productInfoService.productListByType());
    }

    /**
     * 用户添加购物车
     * @param shopCart 购物车信息
     * @return 添加结果
     */
    @PostMapping("/cart")
    public R shopCartAdd(@RequestBody ShopCart shopCart) {
        return R.ok(shopCartService.showCartAdd(shopCart));
    }

    /**
     * 根据用户获取购物车列表
     * @param userCode 用户编号
     * @return 购物车列表
     */
    @GetMapping("/cart")
    public R shopCartByUser(String userCode) {
        return R.ok(shopCartService.shopCartByUser(userCode));
    }

    /**
     * 删除购物车信息
     * @param ids 购物车ID
     * @return 结果
     */
    @DeleteMapping("/cart/{ids}")
    public R delCartByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(shopCartService.update(Wrappers.<ShopCart>lambdaUpdate().set(ShopCart::getDeleteFlag, 1).in(ShopCart::getId, ids)));
    }

    /**
     * 修改购物车信息
     * @param cardListStr 购物车信息
     * @return 结果
     */
    @GetMapping("/cart/edit")
    public R cartEdit(String cardListStr) {
        return R.ok(shopCartService.cartEdit(cardListStr));
    }

    /**
     * 购物车订单结算
     * @param cardListStr 购物车ID
     * @return 商品信息
     */
    @GetMapping("/order/settlement")
    public R orderSettlement(String cardListStr, String listIds) {
        System.out.println(listIds);
        return R.ok(orderInfoService.orderSettlement(cardListStr, Stream.of(listIds.split(",")).map(Integer::parseInt).collect(Collectors.toList())));
    }

    /**
     * 获取用户订单
     * @param userCode 用户编号
     * @return 用户订单
     */
    @GetMapping("/order/list/{userCode}")
    public R orderListByUser(@PathVariable("userCode") String userCode) {
        return R.ok(orderInfoService.orderListByUser(userCode));
    }

    /**
     * 获取订单详情
     * @param orderCode 订单编号
     * @return 订单信息
     */
    @GetMapping("/order/detail/{code}")
    public R orderDetailByCode(@PathVariable("code") String orderCode) {
        return R.ok(orderInfoService.orderDetailByCode(orderCode));
    }

    /**
     * 删除订单
     * @param orderCode 订单编号
     * @return 结果
     */
    @DeleteMapping("/order/{code}")
    public R deleteOrder(@PathVariable("code") String orderCode) {
        return R.ok(orderInfoService.update(Wrappers.<OrderInfo>lambdaUpdate().set(OrderInfo::getDeleteFlag, 1).eq(OrderInfo::getCode, orderCode)));
    }

    /**
     * 订单确认收货
     * @param orderCode 订单编号
     * @return 结果
     */
    @PutMapping("/order/receipt/{orderCode}")
    public R orderReceipt(@PathVariable("orderCode") String orderCode) {
        return R.ok(orderInfoService.update(Wrappers.<OrderInfo>lambdaUpdate().set(OrderInfo::getOrderStatus, 4).eq(OrderInfo::getCode, orderCode)));
    }

    /**
     * 添加订单评价
     * @param evaluation
     * @return
     */
    @PostMapping("/evaluation")
    public R orderEvaluation(@RequestBody Evaluation evaluation) {
        return R.ok(evaluationService.orderEvaluationAdd(evaluation));
    }

}

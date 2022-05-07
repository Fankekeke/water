package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.domain.SystemConstant;
import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.OrderInfo;
import cc.mrbird.febs.cos.entity.OrderItem;
import cc.mrbird.febs.cos.entity.TagInfo;
import cc.mrbird.febs.cos.entity.vo.OrderInfoVo;
import cc.mrbird.febs.cos.service.IOrderInfoService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/order-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderInfoController {

    private final IOrderInfoService orderInfoService;

    /**
     * 获取首页信息
     * @return
     */
    @GetMapping("/home/data")
    public R home() {
        return R.ok(orderInfoService.homeData());
    }

    /**
     * 分页查询订单信息
     * @param page
     * @param orderInfo
     * @return
     */
    @GetMapping("/page")
    public R page(Page page, OrderInfo orderInfo) {
        return R.ok(orderInfoService.orderInfoByPage(page, orderInfo));
    }

    /**
     * 修改订单快递单号及状态
     * @return
     */
    @PutMapping("/order/trackingNumber")
    public R trackingNumberCheck(OrderInfo orderInfo) {
        return R.ok(orderInfoService.update(Wrappers.<OrderInfo>lambdaUpdate().set(OrderInfo::getMailingNumber, orderInfo.getMailingNumber()).set(OrderInfo::getOrderStatus, orderInfo.getOrderStatus()).eq(OrderInfo::getId, orderInfo.getId())));
    }

    /**
     * 新增订单信息
     * @param orderInfo
     * @return
     */
    @PostMapping
    public R save(OrderInfoVo orderInfo) {
        return R.ok(orderInfoService.save(orderInfo));
    }

    /**
     * 用户订单付款
     * @param orderInfo
     * @return
     */
    @PostMapping("/pay")
    public R orderPay(OrderInfo orderInfo) {
        return R.ok(orderInfoService.update(Wrappers.<OrderInfo>lambdaUpdate().set(OrderInfo::getOrderStatus, 1).eq(OrderInfo::getCode, orderInfo.getCode())));
    }

    /**
     * 删除订单信息
     * @param ids
     * @return
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(orderInfoService.update(Wrappers.<OrderInfo>lambdaUpdate().set(OrderInfo::getDeleteFlag, 1).in(OrderInfo::getId, ids)));
    }

    /**
     * 根据用户编号获取订单信息
     * @param code
     * @param status
     * @return
     */
    @GetMapping("/list/user")
    public R orderListByUserCode(String code, Integer status) {
        return R.ok();
    }

    /**
     * 月统计订单量 收益额
     * @param date
     * @return
     */
    @GetMapping("/orderDataByMonth")
    public R orderDataByMonth(String date) {
        return R.ok(orderInfoService.orderDataByMonth(date));
    }

}

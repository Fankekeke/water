package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.service.IOrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/order-item")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderItemController {

    private final IOrderItemService orderItemService;

    /**
     * 根据订单号获取购买商品详情
     * @param code
     * @return
     */
    @GetMapping("/{code}")
    public R orderItemList(@PathVariable String code) {
        return R.ok(orderItemService.orderItemList(code));
    }

}

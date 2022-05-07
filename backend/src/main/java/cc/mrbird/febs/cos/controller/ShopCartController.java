package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.domain.SystemConstant;
import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.ShopCart;
import cc.mrbird.febs.cos.entity.ShopInfo;
import cc.mrbird.febs.cos.service.IShopCartService;
import cc.mrbird.febs.cos.service.IShopInfoService;
import cn.hutool.core.date.DateUtil;
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
@RequestMapping("/cos/shop-cart")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShopCartController {

    private final IShopCartService shopCartService;

    /**
     * 分页查询购物车信息
     * @param page
     * @param shopCart
     * @return
     */
    @GetMapping("/page")
    public R page(Page page, ShopCart shopCart) {
        return R.ok(shopCartService.shopCartInfoByPage(page, shopCart));
    }

    /**
     * 新增购物车信息
     * @param shopCart
     * @return
     */
    @PostMapping
    public R save(ShopCart shopCart) {
        shopCart.setCreateDate(DateUtil.formatDateTime(new Date()));
        shopCart.setDeleteFlag(SystemConstant.DELETE_FLAG);
        return R.ok(shopCartService.save(shopCart));
    }

    /**
     * 修改购物车信息
     * @param shopCart
     * @return
     */
    @PutMapping
    public R edit(ShopCart shopCart) {
        return R.ok(shopCartService.updateById(shopCart));
    }

    /**
     * 删除购物车信息
     * @param ids
     * @return
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(shopCartService.update(Wrappers.<ShopCart>lambdaUpdate().set(ShopCart::getDeleteFlag, 1).in(ShopCart::getId, ids)));
    }

    /**
     * 根据用户编号获取用户购物车
     * @param code
     * @return
     */
    @GetMapping("/list/{code}")
    public R cartByUserCode(@PathVariable(value = "code") String code) {
        return R.ok();
    }
}

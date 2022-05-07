package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.domain.SystemConstant;
import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.ShopCart;
import cc.mrbird.febs.cos.entity.ShopInfo;
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
@RequestMapping("/cos/shop-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShopInfoController {

    private final IShopInfoService shopInfoService;

    /**
     * 分页查询商铺信息
     * @param page
     * @param shopInfo
     * @return
     */
    @GetMapping("/page")
    public R page(Page page, ShopInfo shopInfo) {
        return R.ok(shopInfoService.shopInfoByPage(page, shopInfo));
    }

    /**
     * 获取店铺商家信息
     * @return
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(shopInfoService.list(Wrappers.<ShopInfo>lambdaQuery().eq(ShopInfo::getDeleteFlag, 0)));
    }

    /**
     * 新增商铺信息
     * @param shopInfo
     * @return
     */
    @PostMapping
    public R save(ShopInfo shopInfo) {
        shopInfo.setCode("SP-"+new Date().getTime());
        shopInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
        shopInfo.setDeleteFlag(SystemConstant.DELETE_FLAG);
        return R.ok(shopInfoService.save(shopInfo));
    }

    /**
     * 修改商铺信息
     * @param shopInfo
     * @return
     */
    @PutMapping
    public R edit(ShopInfo shopInfo) {
        return R.ok(shopInfoService.updateById(shopInfo));
    }

    /**
     * 删除商铺信息
     * @param ids
     * @return
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(shopInfoService.update(Wrappers.<ShopInfo>lambdaUpdate().set(ShopInfo::getDeleteFlag, 1).in(ShopInfo::getId, ids)));
    }

}

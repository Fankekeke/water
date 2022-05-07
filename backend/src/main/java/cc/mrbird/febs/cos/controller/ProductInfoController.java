package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.domain.SystemConstant;
import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.ProductInfo;
import cc.mrbird.febs.cos.service.IProductInfoService;
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
@RequestMapping("/cos/product-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductInfoController {

    private final IProductInfoService productInfoService;

    /**
     * 分页获取商品信息
     * @param page
     * @param productInfo
     * @return
     */
    @GetMapping("/page")
    public R page(Page page, ProductInfo productInfo) {
        return R.ok(productInfoService.productInfoByPage(page, productInfo));
    }

    /**
     * 商品上下架
     * @param id
     * @param flag
     * @return
     */
    @PutMapping("/onPut")
    public R onPut(@RequestParam Integer id, @RequestParam Integer flag) {
        return R.ok(productInfoService.update(Wrappers.<ProductInfo>lambdaUpdate().set(ProductInfo::getDeleteFlag, flag).eq(ProductInfo::getId, id)));
    }

    /**
     * 新增商品信息
     * @param productInfo
     * @return
     */
    @PostMapping
    public R save(ProductInfo productInfo) {
        productInfo.setCode("PRO-" + new Date().getTime());
        productInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
        productInfo.setDeleteFlag(SystemConstant.DELETE_FLAG);
        return R.ok(productInfoService.save(productInfo));
    }

    /**
     * 修改商品信息
     * @param productInfo
     * @return
     */
    @PutMapping
    public R edit(ProductInfo productInfo) {
        return R.ok(productInfoService.updateById(productInfo));
    }

    /**
     * 删除商品信息
     * @param ids
     * @return
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(productInfoService.update(Wrappers.<ProductInfo>lambdaUpdate().set(ProductInfo::getDeleteFlag, 1).in(ProductInfo::getId, ids)));
    }

    /**
     * 根据店铺ID获取商品信息
     * @param code
     * @return
     */
    @GetMapping("/list/{code}")
    public R productListByShopCode(@PathVariable(value = "code") String code) {
        return R.ok();
    }

}

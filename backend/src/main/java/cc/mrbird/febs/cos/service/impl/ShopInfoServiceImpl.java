package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.ProductInfo;
import cc.mrbird.febs.cos.entity.ShopInfo;
import cc.mrbird.febs.cos.dao.ShopInfoMapper;
import cc.mrbird.febs.cos.service.IProductInfoService;
import cc.mrbird.febs.cos.service.IShopInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShopInfoServiceImpl extends ServiceImpl<ShopInfoMapper, ShopInfo> implements IShopInfoService {

    private final IProductInfoService productInfoService;

    @Override
    public IPage<LinkedHashMap<String, Object>> shopInfoByPage(Page page, ShopInfo shopInfo) {
        return baseMapper.shopInfoByPage(page, shopInfo);
    }

    @Override
    public List<LinkedHashMap<String, Object>> shopInfoHot(Integer limit) {
        return baseMapper.shopInfoHot(limit);
    }

    @Override
    public List<LinkedHashMap<String, Object>> shopProductList() {
        // 商家信息
        List<LinkedHashMap<String, Object>> shopList = baseMapper.shopList();
        for (LinkedHashMap<String, Object> stringObjectLinkedHashMap : shopList) {
            stringObjectLinkedHashMap.put("productList", productInfoService.list(Wrappers.<ProductInfo>lambdaQuery().eq(ProductInfo::getShopCode, stringObjectLinkedHashMap.get("code").toString()).eq(ProductInfo::getDeleteFlag, 0).last("limit 3")));
        }
        return shopList;
    }
}

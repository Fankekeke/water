package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.ProductInfo;
import cc.mrbird.febs.cos.dao.ProductInfoMapper;
import cc.mrbird.febs.cos.entity.vo.ProductTypeVo;
import cc.mrbird.febs.cos.service.IProductInfoService;
import cc.mrbird.febs.system.domain.Dict;
import cc.mrbird.febs.system.service.DictService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author FanK
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductInfoServiceImpl extends ServiceImpl<ProductInfoMapper, ProductInfo> implements IProductInfoService {

    private final DictService dictService;

    @Override
    public IPage<LinkedHashMap<String, Object>> productInfoByPage(Page page, ProductInfo productInfo) {
        return baseMapper.productInfoByPage(page, productInfo);
    }

    @Override
    public List<LinkedHashMap<String, Object>> productHot(Integer limit) {
        return baseMapper.productHot(limit);
    }

    @Override
    public LinkedHashMap<String, Object> productDetail(String productCode) {
        return baseMapper.productDetail(productCode);
    }

    @Override
    public List<LinkedHashMap<String, Object>> productSortList(Integer sort, String shopCode, String key) {
        return baseMapper.productSortList(sort, shopCode, key);
    }

    @Override
    public List<ProductTypeVo> productListByType() {
        List<ProductTypeVo> result = new ArrayList<>();
        List<LinkedHashMap<String, Object>> productList = baseMapper.productListByType();
        // 获取所有商品类型
        List<Dict> dictList = dictService.list(Wrappers.<Dict>lambdaQuery().eq(Dict::getTableName, "product_info").eq(Dict::getFieldName, "product_type"));
        for (Dict dict : dictList) {
            ProductTypeVo productTypeVo = new ProductTypeVo(dict.getValuee(), dict.getKeyy(), productList.stream().filter(a -> dict.getKeyy().equals(a.get("productType").toString())).collect(Collectors.toList()));
            result.add(productTypeVo);
        }
        return result;
    }
}

package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.ProductInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface ProductInfoMapper extends BaseMapper<ProductInfo> {

    // 分页查询商品信息
    IPage<LinkedHashMap<String, Object>> productInfoByPage(Page page, @Param("productInfo") ProductInfo productInfo);

    // 热销商品
    List<LinkedHashMap<String, Object>> productHot(@Param("limit") Integer limit);

    // 商品详情
    LinkedHashMap<String, Object> productDetail(String productCode);

    // 商家商品分类查询
    List<LinkedHashMap<String, Object>> productSortList(@Param("sort") Integer sort, @Param("shopCode") String shopCode, @Param("key") String key);

    // 根据类型获取商品信息
    List<LinkedHashMap<String, Object>> productListByType();
}

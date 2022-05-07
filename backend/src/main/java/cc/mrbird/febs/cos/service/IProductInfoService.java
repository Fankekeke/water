package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.ProductInfo;
import cc.mrbird.febs.cos.entity.vo.ProductTypeVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface IProductInfoService extends IService<ProductInfo> {

    // 分页查询商品信息
    IPage<LinkedHashMap<String, Object>> productInfoByPage(Page page, ProductInfo productInfo);

    // 热销商品
    List<LinkedHashMap<String, Object>> productHot(Integer limit);

    // 商品详情
    LinkedHashMap<String, Object> productDetail(String productCode);

    // 商家商品分类查询
    List<LinkedHashMap<String, Object>> productSortList(Integer sort, String shopCode, String key);

    // 根据类型获取商品信息
    List<ProductTypeVo> productListByType();
}

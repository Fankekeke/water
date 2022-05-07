package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.ShopInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface ShopInfoMapper extends BaseMapper<ShopInfo> {

    // 分页查询商铺信息
    IPage<LinkedHashMap<String, Object>> shopInfoByPage(Page page, @Param("shopInfo") ShopInfo shopInfo);

    // 热销商家
    List<LinkedHashMap<String, Object>> shopInfoHot(@Param("limit") Integer limit);

    // 获取商家信息
    List<LinkedHashMap<String, Object>> shopList();

    // 根据商品编号获取商家信息
    List<LinkedHashMap<String, Object>> shopListByProduct(@Param("codes") List<String> codes);
}

package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.ShopCart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface ShopCartMapper extends BaseMapper<ShopCart> {

    // 分页查询购物车信息
    IPage<LinkedHashMap<String, Object>> shopCartInfoByPage(Page page, @Param("shopCart") ShopCart shopCart);

    // 根据用户获取购物车列表
    List<LinkedHashMap<String, Object>> shopCartByUser(@Param("userCode") String userCode);

    // 获取购物车列表详情
    List<LinkedHashMap<String, Object>> shopCartDetail(@Param("ids") List<Integer> ids);
}

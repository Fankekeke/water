package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.ShopCart;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface IShopCartService extends IService<ShopCart> {

    // 分页查询购物车信息
    IPage<LinkedHashMap<String, Object>> shopCartInfoByPage(Page page, ShopCart shopCart);

    // 用户添加购物车
    Boolean showCartAdd(ShopCart shopCart);

    // 根据用户获取购物车列表
    List<LinkedHashMap<String, Object>> shopCartByUser(String userCode);

    // 修改购物车信息
    Boolean cartEdit(String cardListStr);
}

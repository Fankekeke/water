package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.ShopInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface IShopInfoService extends IService<ShopInfo> {

    // 分页查询商铺信息
    IPage<LinkedHashMap<String, Object>> shopInfoByPage(Page page, ShopInfo shopInfo);

    // 热销商家
    List<LinkedHashMap<String, Object>> shopInfoHot(Integer limit);

    // 获取商家及产品信息
    List<LinkedHashMap<String, Object>> shopProductList();
}

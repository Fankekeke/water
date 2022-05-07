package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.OrderItem;
import cc.mrbird.febs.cos.entity.ShopCart;
import cc.mrbird.febs.cos.dao.ShopCartMapper;
import cc.mrbird.febs.cos.service.IShopCartService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
@Service
public class ShopCartServiceImpl extends ServiceImpl<ShopCartMapper, ShopCart> implements IShopCartService {

    @Override
    public IPage<LinkedHashMap<String, Object>> shopCartInfoByPage(Page page, ShopCart shopCart) {
        return baseMapper.shopCartInfoByPage(page, shopCart);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean showCartAdd(ShopCart shopCart) {
        shopCart.setDeleteFlag(0);
        shopCart.setCreateDate(DateUtil.formatDateTime(new Date()));
        // 判断购物车是否存在该商品
        Integer count = this.count(Wrappers.<ShopCart>lambdaQuery().eq(ShopCart::getProductCode, shopCart.getProductCode()).eq(ShopCart::getUserCode, shopCart.getUserCode()).eq(ShopCart::getDeleteFlag, 0));
        if (count > 0) {
            return false;
        } else {
            // 判断新增或修改
            ShopCart exist = this.getOne(Wrappers.<ShopCart>lambdaQuery().eq(ShopCart::getProductCode, shopCart.getProductCode()).eq(ShopCart::getUserCode, shopCart.getUserCode()));
            if (exist != null) {
                shopCart.setId(exist.getId());
            }
            return this.saveOrUpdate(shopCart);
        }
    }

    @Override
    public List<LinkedHashMap<String, Object>> shopCartByUser(String userCode) {
        return baseMapper.shopCartByUser(userCode);
    }

    @Override
    public Boolean cartEdit(String cardListStr) {
        List<ShopCart> shopCartList = JSONUtil.toList(cardListStr, ShopCart.class);
        return this.updateBatchById(shopCartList);
    }
}

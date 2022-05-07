package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.OrderItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    // 根据订单号获取购买商品详情
    List<LinkedHashMap<String, Object>> orderItemList(@Param("code") String code);
}

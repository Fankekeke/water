package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.OrderItem;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface IOrderItemService extends IService<OrderItem> {

    // 根据订单号获取购买商品详情
    List<LinkedHashMap<String, Object>> orderItemList(String code);

}

package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.OrderItem;
import cc.mrbird.febs.cos.dao.OrderItemMapper;
import cc.mrbird.febs.cos.service.IOrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements IOrderItemService {

    @Override
    public List<LinkedHashMap<String, Object>> orderItemList(String code) {
        return baseMapper.orderItemList(code);
    }
}

package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.OrderInfo;
import cc.mrbird.febs.cos.entity.vo.OrderItemVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    // 分页查询订单信息
    IPage<LinkedHashMap<String, Object>> orderInfoByPage(Page page, @Param("orderInfo") OrderInfo orderInfo);

    // 七天内订单量统计
    List<LinkedHashMap<String, Object>> orderVolumeStatistics();

    // 本月订单量，收益统计
    LinkedHashMap<String, Object> orderStatisticsByCurrentMonth();

    // 本月收益类型占比
    List<LinkedHashMap<String, Object>> orderStatisticsByCurrentMonthRate();

    // 获取代发货订单
    List<LinkedHashMap<String, Object>> pendingOrder();

    // 月统计订单量 收益额
    List<LinkedHashMap<String, Object>> orderDataByMonth(String date);

    // 获取用户订单
    List<OrderItemVo> orderListByUser(@Param("userCode") String userCode);

    // 获取订单项
    List<LinkedHashMap<String, Object>> orderItemByCode(@Param("orderCode") String orderCode);

}

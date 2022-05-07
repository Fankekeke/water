package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.Evaluation;
import cc.mrbird.febs.cos.dao.EvaluationMapper;
import cc.mrbird.febs.cos.entity.OrderInfo;
import cc.mrbird.febs.cos.entity.OrderItem;
import cc.mrbird.febs.cos.service.IEvaluationService;
import cc.mrbird.febs.cos.service.IOrderItemService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EvaluationServiceImpl extends ServiceImpl<EvaluationMapper, Evaluation> implements IEvaluationService {

    private final IOrderItemService orderItemService;

    @Override
    public IPage<LinkedHashMap<String, Object>> orderEvaluationByPage(Page page, Evaluation evaluation) {
        return baseMapper.orderEvaluationByPage(page, evaluation);
    }

    @Override
    public List<LinkedHashMap<String, Object>> productEvaluation(String productCode) {
        return baseMapper.productEvaluation(productCode);
    }

    @Override
    public Boolean orderEvaluationAdd(Evaluation evaluation) {
        evaluation.setDeleteFlag(0);
        evaluation.setCode("EVA-"+new Date().getTime());
        evaluation.setCreateDate(DateUtil.formatDateTime(new Date()));
        List<OrderItem> orderItemList = orderItemService.list(Wrappers.<OrderItem>lambdaQuery().eq(OrderItem::getOrderCode, evaluation.getOrderCode()));
        List<Evaluation> evaluations = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            Evaluation evaluationItem = new Evaluation();
            BeanUtil.copyProperties(evaluation, evaluationItem);
            evaluationItem.setOrderItemId(orderItem.getId());
            evaluations.add(evaluationItem);
        }
        return this.saveBatch(evaluations);
    }
}

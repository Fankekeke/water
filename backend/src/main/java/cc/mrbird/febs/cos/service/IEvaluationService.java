package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.Evaluation;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface IEvaluationService extends IService<Evaluation> {

    // 分页查询评价信息
    IPage<LinkedHashMap<String, Object>> orderEvaluationByPage(Page page, Evaluation evaluation);

    // 商品评价
    List<LinkedHashMap<String, Object>> productEvaluation(String productCode);

    // 添加订单评价
    Boolean orderEvaluationAdd(Evaluation evaluation);
}

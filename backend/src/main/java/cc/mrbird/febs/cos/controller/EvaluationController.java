package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.domain.SystemConstant;
import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.Evaluation;
import cc.mrbird.febs.cos.service.IEvaluationService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/evaluation")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EvaluationController {

    private final IEvaluationService evaluationService;

    /**
     * 分页查询评价信息
     * @param page
     * @param evaluation
     * @return
     */
    @GetMapping("/page")
    public R page(Page page, Evaluation evaluation) {
        return R.ok(evaluationService.orderEvaluationByPage(page, evaluation));
    }

    /**
     * 新增评价信息
     * @param evaluation
     * @return
     */
    @PostMapping
    public R save(Evaluation evaluation) {
        evaluation.setCreateDate(DateUtil.formatDateTime(new Date()));
        evaluation.setDeleteFlag(SystemConstant.DELETE_FLAG);
        return R.ok(evaluationService.save(evaluation));
    }

    /**
     * 修改评价信息
     * @param evaluation
     * @return
     */
    @PutMapping
    public R edit(Evaluation evaluation) {
        return R.ok(evaluationService.updateById(evaluation));
    }

    /**
     * 删除评价信息
     * @param ids
     * @return
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(evaluationService.update(Wrappers.<Evaluation>lambdaUpdate().set(Evaluation::getDeleteFlag, 1).in(Evaluation::getId, ids)));
    }

    /**
     * 根据产品编号获取评价信息
     * @param code
     * @return
     */
    @GetMapping("/list/{code}")
    public R evaluationListByProductCode(@PathVariable(value = "code") String code) {
        return R.ok();
    }

}

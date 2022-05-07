package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.ReplyInfo;
import cc.mrbird.febs.cos.service.IReplyInfoService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Wrapper;
import java.util.Date;
import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/reply-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReplyInfoController {

    private final IReplyInfoService replyInfoService;

    /**
     * 分页查询回复消息
     * @param page
     * @param replyInfo
     * @return
     */
    @GetMapping("/page")
    public R page(Page page, ReplyInfo replyInfo) {
        return R.ok(replyInfoService.replyInfoByPage(page, replyInfo));
    }

    /**
     * 新增回复消息
     * @param replyInfo
     * @return
     */
    @PostMapping
    public R save(ReplyInfo replyInfo) {
        replyInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
        return R.ok(replyInfoService.save(replyInfo));
    }

    /**
     * 修改回复消息
     * @param replyInfo
     * @return
     */
    @PutMapping
    public R edit(ReplyInfo replyInfo) {
        return R.ok(replyInfoService.updateById(replyInfo));
    }

    /**
     * 删除回复消息
     * @param ids
     * @return
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(replyInfoService.update(Wrappers.<ReplyInfo>lambdaUpdate().set(ReplyInfo::getDeleteFlag, 1).in(ReplyInfo::getId, ids)));
    }

    /**
     * 根据编号获取回复消息
     * @param code
     * @return
     */
    @GetMapping("/list/{code}")
    public R replyListByCode(@PathVariable(value = "code") String code) {
        return R.ok(replyInfoService.replyListByCode(code));
    }

}

package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.domain.SystemConstant;
import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.TagInfo;
import cc.mrbird.febs.cos.service.ITagInfoService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
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
@RequestMapping("/cos/tag-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TagInfoController {

    private final ITagInfoService tagInfoService;

    /**
     * 分页查询标签信息
     * @param page
     * @param tagInfo
     * @return
     */
    @GetMapping("/page")
    public R page(Page page, TagInfo tagInfo) {
        return R.ok();
    }

    /**
     * 修改标签内容
     * @param tags
     * @return
     */
    @PostMapping("/batch")
    public R saveBatch(@RequestParam String tags) {
        tagInfoService.remove(Wrappers.<TagInfo>lambdaQuery());
        List<TagInfo> tagInfos = JSONUtil.toList(tags, TagInfo.class);
        return R.ok(tagInfoService.saveBatch(tagInfos));
    }

    /**
     * 查询标签信息
     * @return
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(tagInfoService.list());
    }

    /**
     * 新增标签信息
     * @param tagInfo
     * @return
     */
    @PostMapping
    public R save(TagInfo tagInfo) {
        tagInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
        return R.ok(tagInfoService.save(tagInfo));
    }

    /**
     * 修改标签信息
     * @param tagInfo
     * @return
     */
    @PutMapping
    public R edit(TagInfo tagInfo) {
        return R.ok(tagInfoService.updateById(tagInfo));
    }

    /**
     * 删除商铺信息
     * @param ids
     * @return
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(tagInfoService.removeByIds(ids));
    }

}

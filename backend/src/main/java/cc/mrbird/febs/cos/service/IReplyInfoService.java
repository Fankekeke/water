package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.ReplyInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface IReplyInfoService extends IService<ReplyInfo> {

    // 分页查询回复消息
    IPage<LinkedHashMap<String, Object>> replyInfoByPage(Page page, ReplyInfo replyInfo);

    // 根据编号获取回复消息
    List<LinkedHashMap<String, Object>> replyListByCode(String code);

}

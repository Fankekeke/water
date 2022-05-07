package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.TagInfo;
import cc.mrbird.febs.cos.dao.TagInfoMapper;
import cc.mrbird.febs.cos.service.ITagInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author FanK
 */
@Service
public class TagInfoServiceImpl extends ServiceImpl<TagInfoMapper, TagInfo> implements ITagInfoService {

}

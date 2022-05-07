package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.AddressInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface AddressInfoMapper extends BaseMapper<AddressInfo> {

    // 分页获取收货地址信息
    IPage<LinkedHashMap<String, Object>> getAddressInfoByPage(Page page, @Param("addressInfo") AddressInfo addressInfo);
}

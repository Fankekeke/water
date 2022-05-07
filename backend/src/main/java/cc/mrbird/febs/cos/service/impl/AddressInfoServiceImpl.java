package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.common.domain.SystemConstant;
import cc.mrbird.febs.cos.entity.AddressInfo;
import cc.mrbird.febs.cos.dao.AddressInfoMapper;
import cc.mrbird.febs.cos.service.IAddressInfoService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.LinkedHashMap;

/**
 * @author FanK
 */
@Service
public class AddressInfoServiceImpl extends ServiceImpl<AddressInfoMapper, AddressInfo> implements IAddressInfoService {

    @Override
    public IPage<LinkedHashMap<String, Object>> getAddressInfoByPage(Page page, AddressInfo addressInfo) {
        return baseMapper.getAddressInfoByPage(page, addressInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addressSave(AddressInfo addressInfo) {
        addressInfo.setDeleteFlag(SystemConstant.DELETE_FLAG);
        addressInfo.setCode("ARS-" + new Date().getTime());
        addressInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
        // 判断是否为默认地址
        if (addressInfo.getDefaultAddress() == 1) {
            this.update(Wrappers.<AddressInfo>lambdaUpdate().set(AddressInfo::getDefaultAddress, 0).eq(AddressInfo::getUserCode, addressInfo.getUserCode()));
        }
        return this.save(addressInfo);
    }

    @Override
    public Boolean addressEdit(AddressInfo addressInfo) {
        // 判断是否为默认地址
        if (addressInfo.getDefaultAddress() == 1) {
            this.update(Wrappers.<AddressInfo>lambdaUpdate().set(AddressInfo::getDefaultAddress, 0).eq(AddressInfo::getUserCode, addressInfo.getUserCode()));
        }
        return this.updateById(addressInfo);
    }
}

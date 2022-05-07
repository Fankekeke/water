package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.AddressInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface IAddressInfoService extends IService<AddressInfo> {

    // 分页获取收货地址信息
    IPage<LinkedHashMap<String, Object>> getAddressInfoByPage(Page page, AddressInfo addressInfo);

    // 小程序用户添加收货地址
    Boolean addressSave(AddressInfo addressInfo);

    // 小程序用户修改收货地址
    Boolean addressEdit(AddressInfo addressInfo);
}

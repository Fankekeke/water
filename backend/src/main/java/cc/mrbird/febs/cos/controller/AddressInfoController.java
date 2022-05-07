package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.domain.SystemConstant;
import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.AddressInfo;
import cc.mrbird.febs.cos.service.IAddressInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/address-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AddressInfoController {

    private final IAddressInfoService addressInfoService;

    /**
     * 分页获取收货地址信息
     * @param page
     * @param addressInfo
     * @return
     */
    @GetMapping("/page")
    public R page(Page page, AddressInfo addressInfo) {
        return R.ok(addressInfoService.getAddressInfoByPage(page, addressInfo));
    }

    /**
     * 根据编号获取地址信息
     * @param code
     * @return
     */
    @GetMapping("/info/{code}")
    public R addressDataByCode(@PathVariable String code) {
        return R.ok(addressInfoService.getOne(Wrappers.<AddressInfo>lambdaQuery().eq(AddressInfo::getCode, code)));
    }

    /**
     * 新增收货地址
     * @param addressInfo
     * @return
     */
    @PostMapping
    public R save(AddressInfo addressInfo) {
        addressInfo.setDeleteFlag(SystemConstant.DELETE_FLAG);
        return R.ok(addressInfoService.save(addressInfo));
    }

    /**
     * 修改收货地址
     * @param addressInfo
     * @return
     */
    @PutMapping
    public R edit(AddressInfo addressInfo) {
        return R.ok(addressInfoService.updateById(addressInfo));
    }

    /**
     * 删除收货地址
     * @param ids
     * @return
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(addressInfoService.update(Wrappers.<AddressInfo>lambdaUpdate().set(AddressInfo::getDeleteFlag, 1).in(AddressInfo::getId, ids)));
    }

    /**
     *  根据用户获取收获地址
     * @param userCode
     * @return
     */
    @GetMapping("/list/{code}")
    public R getAddressByUser(@PathVariable("code") String userCode) {
        return R.ok(addressInfoService.list(Wrappers.<AddressInfo>lambdaQuery().eq(AddressInfo::getUserCode, userCode).eq(AddressInfo::getDeleteFlag, 0)));
    }

    /**
     * 获取用户地址
     * @param userCode
     * @return
     */
    @GetMapping("/default/{code}")
    public R addressDefaultByUser(@PathVariable(value = "code") String userCode) {
        List<AddressInfo> addressInfoList = addressInfoService.list(Wrappers.<AddressInfo>lambdaQuery().eq(AddressInfo::getUserCode, userCode).eq(AddressInfo::getDeleteFlag, 0));
        List<AddressInfo> addressDefault = addressInfoList.stream().filter(a -> a.getDefaultAddress() == 1).collect(Collectors.toList());
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>(){
            {
                put("defaultFlag", addressDefault.size() > 0 ? 1 : 0);
                put("defaultCode", addressDefault.size() > 0 ? addressDefault.get(0).getCode() : null);
                put("data", addressInfoList);
            }
        };
        return R.ok(result);
    }
}

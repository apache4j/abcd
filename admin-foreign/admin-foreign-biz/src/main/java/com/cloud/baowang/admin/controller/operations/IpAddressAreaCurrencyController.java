package com.cloud.baowang.admin.controller.operations;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.operations.IpAddressAreaCurrencyApi;
import com.cloud.baowang.system.api.enums.operations.CountryCodeEnum;
import com.cloud.baowang.system.api.vo.operations.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IP归属地方案表
 *
 * @author system
 * @since 2025-06-03 10:22:26
 */
@RestController
@RequestMapping("/ipAddressAreaCurrency/api")
@Tag(name = "IP归属地方案表控制器")
public class IpAddressAreaCurrencyController {

    @Autowired
    private IpAddressAreaCurrencyApi ipAddressAreaCurrencyApi;

    /**
     * 分页查询
     */
    @Operation(summary = "分页查询")
    @PostMapping("/findPage")
    public ResponseVO<Page<IpAddressAreaCurrencyResVO>> findPage(@RequestBody @Validated IpAddressAreaCurrencyQueryReqVO reqVO) {
        Page<IpAddressAreaCurrencyResVO> result = ipAddressAreaCurrencyApi.findPage(reqVO);
        return ResponseVO.success(result);
    }

    /**
     * 列表查询
     */
    @Operation(summary = "列表查询")
    @PostMapping("/findList")
    public ResponseVO<List<IpAddressAreaCurrencyResVO>> findList(@RequestBody @Validated IpAddressAreaCurrencyReqVO reqVO) {
        return ipAddressAreaCurrencyApi.findList(reqVO);
    }

    /**
     * 查询
     */
    @Operation(summary = "查询详情")
    @PostMapping("/findById")
    public ResponseVO<IpAddressAreaCurrencyResVO> findById(@RequestBody @Validated IpAddressAreaCurrencyIdReqVO reqVO) {
        return ipAddressAreaCurrencyApi.findById(reqVO);
    }

    /**
     * 新增
     */
    @Operation(summary = "新增数据")
    @PostMapping("add")
    public ResponseVO<Boolean> insert(@RequestBody @Validated IpAddressAreaCurrencyAddReqVO reqVO) {
        reqVO.setCreator(CurrReqUtils.getAccount());
        reqVO.setUpdater(CurrReqUtils.getAccount());
        return ipAddressAreaCurrencyApi.insert(reqVO);
    }

    /**
     * 修改
     */
    @Operation(summary = "修改数据")
    @PostMapping("update")
    public ResponseVO<Boolean> update(@RequestBody @Validated IpAddressAreaCurrencyUpdateReqVO reqVO) {
        reqVO.setCreator(null);
        reqVO.setUpdater(CurrReqUtils.getAccount());
        return ipAddressAreaCurrencyApi.update(reqVO);
    }

    /**
     * 删除
     */
    @Operation(summary = "删除数据")
    @PostMapping("/delete")
    public ResponseVO<Boolean> delete(@RequestBody @Validated IpAddressAreaCurrencyIdReqVO reqVO) {
        return ipAddressAreaCurrencyApi.delete(reqVO);
    }

    /**
     * 删除
     */
    @Operation(summary = "禁用启用")
    @PostMapping("/enableOrDisable")
    public ResponseVO<Boolean> enableOrDisable(@RequestBody @Validated IpAddressAreaCurrencyStatusReqVO reqVO) {
        return ipAddressAreaCurrencyApi.enableOrDisable(reqVO);
    }

    /**
     * 删除
     */
    @Operation(summary = "获取国家码和名称")
    @PostMapping("/areaList")
    public ResponseVO<List<Map<String, String>>> areaList() {

        CountryCodeEnum[] values = CountryCodeEnum.values();
        List<Map<String, String>> backList = new ArrayList<>();

        for (CountryCodeEnum value : values) {
            HashMap<String, String> tempMap = new HashMap<>();
            tempMap.put("code", value.getCode());
            tempMap.put("name", value.getName());
            backList.add(tempMap);
        }

        return ResponseVO.success(backList);
    }

}
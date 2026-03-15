package com.cloud.baowang.system.api.api.operations;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.operations.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteIpAddressAreaCurrencyApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - ipAddressAreaCurrencyApi")
public interface IpAddressAreaCurrencyApi {

    String PREFIX = ApiConstants.PREFIX + "/ipAddressAreaCurrency/api";

    @Operation(summary = "查询列表分页")
    @PostMapping(PREFIX + "/findPage")
    public Page<IpAddressAreaCurrencyResVO> findPage(@RequestBody IpAddressAreaCurrencyQueryReqVO vo);

    @Operation(summary = "查询列表")
    @PostMapping(PREFIX + "/findList")
    public ResponseVO<List<IpAddressAreaCurrencyResVO>> findList(@RequestBody IpAddressAreaCurrencyReqVO reqVO);

    @Operation(summary = "查询详情")
    @PostMapping(PREFIX + "/findById")
    public ResponseVO<IpAddressAreaCurrencyResVO> findById(@RequestBody IpAddressAreaCurrencyIdReqVO reqVO);

    @Operation(summary = "新增")
    @PostMapping(PREFIX + "/insert")
    public ResponseVO<Boolean> insert(@RequestBody IpAddressAreaCurrencyAddReqVO reqVO);

    @Operation(summary = "修改")
    @PostMapping(PREFIX + "/update")
    public ResponseVO<Boolean> update(@RequestBody IpAddressAreaCurrencyUpdateReqVO reqVO);

    @Operation(summary = "删除")
    @PostMapping(PREFIX + "/delete")
    public ResponseVO<Boolean> delete(@RequestBody IpAddressAreaCurrencyIdReqVO reqVO);

    @Operation(summary = "获取ip根据归属地方案用户未登陆默认币种")
    @PostMapping(PREFIX + "/queryWebCurrey")
    public ResponseVO<IpAdsWebResVO> queryWebCurrey(@RequestBody IpAdsWebReqVO reqVO);

    @Operation(summary = "禁用启用")
    @PostMapping(PREFIX + "/enableOrDisable")
    public ResponseVO<Boolean> enableOrDisable(@RequestBody IpAddressAreaCurrencyStatusReqVO reqVO) ;


}
package com.cloud.baowang.wallet.api.api;


import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.uservirtualcurrency.EditVirtualCurrencyAddressVO;
import com.cloud.baowang.wallet.api.vo.uservirtualcurrency.RiskEditVirtualCurrencyAddressVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteVirtualCurrencyManageApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 虚拟币管理")
public interface VirtualCurrencyManageApi {

    String PREFIX = ApiConstants.PREFIX + "/virtualCurrencyManage/api/";

    @Operation(summary = "根据虚拟币地址 查询绑定信息")
    @PostMapping(value = PREFIX + "getRiskEditVirtualCurrencyInfoGetByAddr")
    ResponseVO<RiskEditVirtualCurrencyAddressVO> getRiskEditVirtualCurrencyInfoGetByAddr(@RequestParam("addr") String addr);

    @Operation(summary = "根据虚拟币地址 查询绑定信息")
    @GetMapping(value = PREFIX + "getRiskEditVirtualCurrencyInfoGetByAddrAndSiteCode")
    ResponseVO<RiskEditVirtualCurrencyAddressVO> getRiskEditVirtualCurrencyInfoGetByAddrAndSiteCode(@RequestParam("addr") String addr,
                                                                                                    @RequestParam("siteCode") String siteCode);

    @Operation(summary = "根据id更新虚拟币信息")
    @PostMapping(value = PREFIX + "updateBankInfoById")
    ResponseVO<Boolean> updateVirtualCurrencyById(@RequestBody EditVirtualCurrencyAddressVO editVirtualCurrencyAddressVO);


}

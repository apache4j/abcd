package com.cloud.baowang.wallet.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeAuthorizeReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeAuthorizeResVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeBatchReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteSiteRechargeApi",value = ApiConstants.NAME)
@Tag(name = "RPC服务-站点充值api")
public interface SiteRechargeApi {

    String PREFIX = ApiConstants.PREFIX + "/site/recharge/";

    @PostMapping(value = PREFIX + "batchSave")
    @Operation(summary = "站点充值批量新增")
    ResponseVO<Boolean> batchSave(@RequestParam("creator")String creator,@RequestParam("siteCode")String siteCode,
                                  @RequestBody List<SiteRechargeBatchReqVO> siteRechargeBatchReqVO,@RequestParam("siteName") String siteName,
                                  @RequestParam(value = "handicapMode",required = false,defaultValue = "0") Integer handicapMode);

    @PostMapping(value = PREFIX + "queryDepositAuthorize")
    @Operation(summary = "站点存款授权查询")
    ResponseVO<SiteRechargeAuthorizeResVO> queryDepositAuthorize(@RequestBody RechargeAuthorizeReqVO reqVO);
}

package com.cloud.baowang.wallet.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawAuthorizeResVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawBatchRequsetVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawAuthorizeReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteSiteWithdrawApi", value = ApiConstants.NAME)
@Tag(name = "RPC服务-站点提现api")
public interface SiteWithdrawApi {

    String PREFIX = ApiConstants.PREFIX + "/withdraw/api/";

    @PostMapping(value = PREFIX + "batchSave")
    @Operation(summary = "站点提现批量新增")
    ResponseVO<Boolean> batchSave(@RequestParam("creator") String creator, @RequestParam("siteCode") String siteCode, @RequestBody List<SiteWithdrawBatchRequsetVO> siteWithdrawBatchRequsetVOS,@RequestParam("siteName") String siteName);

    @PostMapping(value = PREFIX + "queryWithdrawAuthorize")
    @Operation(summary = "新增站点提款授权查询")
    ResponseVO<SiteWithdrawAuthorizeResVO> queryWithdrawAuthorize(@RequestBody WithdrawAuthorizeReqVO reqVO);
}

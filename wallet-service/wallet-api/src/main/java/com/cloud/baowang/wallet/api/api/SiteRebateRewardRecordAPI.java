package com.cloud.baowang.wallet.api.api;


import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "siteRebateRewardRecordAPIApi", value = ApiConstants.NAME)
@Tag(name = "RPC 福利中心反水 服务")
public interface SiteRebateRewardRecordAPI {

    String PREFIX = ApiConstants.PREFIX + "/site/siteRebateRewardRecord/";

//    @PostMapping(value = PREFIX + "saveSiteRebateRewardRecord")
//    @Operation(summary = "按照站点查询平台币")
//    ResponseVO saveSiteRebateRewardRecord(@RequestParam("siteCode") String siteCode);


    @PostMapping(value = PREFIX + "rebateReward")
    @Operation(summary = "反水领奖")
    ResponseVO rebateReward(@RequestParam("id") String id);


    @PostMapping(value = PREFIX + "rebateUserReward")
    @Operation(summary = "一键领取反水领奖")
    ResponseVO<Boolean> rebateUserReward(@RequestParam("userId") String userId);

}

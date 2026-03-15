package com.cloud.baowang.wallet.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserAwardRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "remoteUserVIPAwardRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员VIP奖励记录 服务")
public interface VIPAwardRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/userVIPAwardRecord/api/";

    @Operation(summary = "记录VIP奖励信息")
    @PostMapping(value = PREFIX + "recordVIPAward")
    ResponseVO<Boolean> recordVIPAward(@RequestBody List<UserAwardRecordVO> userAwardRecordVOList);

    @Operation(summary = "一键领取会员VIP奖励")
    @PostMapping(value = PREFIX + "receiveUserAward")
    ResponseVO<Boolean> receiveUserAward(@RequestParam("userId") String userId);

    @Operation(summary = "领取某个活动的奖励")
    @PostMapping(value = PREFIX + "receiveActiveAward")
    ResponseVO<Boolean> receiveActiveAward(@RequestParam("orderId") String orderId);

    @Operation(summary = "VIP活动过期")
    @PostMapping(value = PREFIX + "vipExpired")
    void vipExpired();
}

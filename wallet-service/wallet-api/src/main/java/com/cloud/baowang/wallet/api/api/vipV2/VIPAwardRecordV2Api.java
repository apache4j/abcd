package com.cloud.baowang.wallet.api.api.vipV2;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.activityV2.UserAwardRecordV2ReqVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserAwardRecordVO;
import com.cloud.baowang.wallet.api.vo.vipV2.UserAwardRecordV2VO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteUserVIPAwardRecordV2Api", value = ApiConstants.NAME)
@Tag(name = "RPC 会员VIP奖励记录 服务")
public interface VIPAwardRecordV2Api {

    String PREFIX = ApiConstants.PREFIX + "/userVIPAwardRecordV2/api/";

    @Operation(summary = "记录VIP奖励信息")
    @PostMapping(value = PREFIX + "recordVIPAward")
    ResponseVO<Boolean> recordVIPAward(@RequestBody List<UserAwardRecordV2VO> userAwardRecordVOList);

    @Operation(summary = "一键领取会员VIP奖励")
    @PostMapping(value = PREFIX + "receiveUserAward")
    ResponseVO<Boolean> receiveUserAward(@RequestParam("userId") String userId);

    @Operation(summary = "领取某个活动的奖励")
    @PostMapping(value = PREFIX + "receiveActiveAward")
    ResponseVO<Boolean> receiveActiveAward(@RequestParam("id") String id);

    @Operation(summary = "VIP活动过期")
    @PostMapping(value = PREFIX + "vipExpired")
    void vipExpired();

    @Operation(summary = "VIP奖励查询量否已领取")
    @PostMapping(value = PREFIX + "awardHasReceive")
    ResponseVO<Boolean> awardHasReceive(@RequestBody UserAwardRecordV2ReqVO vo);


    @Operation(summary = "VIP奖励查询")
    @PostMapping(value = PREFIX + "awardRecordByUserId")
    ResponseVO<UserAwardRecordV2VO> awardRecordByUserId(@RequestBody UserAwardRecordV2ReqVO vo);

    @Operation(summary = "VIP奖励查询")
    @PostMapping(value = PREFIX + "awardRecordByVipGrade")
    List<UserAwardRecordV2VO> awardRecordByVipGrade(@RequestBody UserAwardRecordV2ReqVO vo);
}

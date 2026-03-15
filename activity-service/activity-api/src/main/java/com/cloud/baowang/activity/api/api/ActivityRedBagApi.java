package com.cloud.baowang.activity.api.api;

import com.cloud.baowang.activity.api.ApiConstants;
import com.cloud.baowang.activity.api.vo.ToActivityVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagParticipateReqVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagRainClientInfoVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagRealTimeInfo;
import com.cloud.baowang.activity.api.vo.redbag.RedBagSendReqVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagSendRespVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagSettlementReqVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagSettlementVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(contextId = "activityRedBagRainApi", value = ApiConstants.NAME)
@Tag(name = "红包雨活动配置-接口")
public interface ActivityRedBagApi {


    String PREFIX = ApiConstants.PREFIX +"/"+ApiConstants.PATH+ "/redBagRainApi/api/";

    @Operation(summary = "红包雨活动实时信息")
    @PostMapping(PREFIX + "realTimeInfo")
    ResponseVO<RedBagRealTimeInfo> realTimeInfo(@RequestParam("siteCode") String siteCode);

    @Operation(summary = "红包雨活动客户端详情")
    @PostMapping(PREFIX + "clientInfo")
    ResponseVO<RedBagRainClientInfoVO> clientInfo(@RequestParam("siteCode") String siteCode,@RequestParam("timezone")String timezone);

    @Operation(summary = "红包雨活动参与校验")
    @PostMapping(PREFIX + "participate")
    ResponseVO<ToActivityVO> participate(@RequestBody RedBagParticipateReqVO vo);

    @Operation(summary = "红包雨活动发红包")
    @PostMapping(PREFIX + "send")
    ResponseVO<RedBagSendRespVO> send(@RequestBody RedBagSendReqVO vo);

    @Operation(summary = "红包雨活动结算")
    @PostMapping(PREFIX + "settlement")
    ResponseVO<RedBagSettlementVO> settlement(@RequestBody RedBagSettlementReqVO vo);

    @Operation(summary = "红包雨开始时间推送")
    @PostMapping(PREFIX + "startPush")
    ResponseVO<Void> activityRedBagStartPush(@RequestParam("siteCode") String siteCode, @RequestParam("timeStr") String timeStr);

    @Operation(summary = "红包雨结束时间推送")
    @PostMapping(PREFIX + "endPush")
    ResponseVO<Void> activityRedBagEndPush(@RequestParam("siteCode") String siteCode, @RequestParam("timeStr") String timeStr);


}

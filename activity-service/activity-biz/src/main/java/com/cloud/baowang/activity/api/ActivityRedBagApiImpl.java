package com.cloud.baowang.activity.api;

import com.cloud.baowang.activity.api.api.ActivityRedBagApi;
import com.cloud.baowang.activity.api.vo.ToActivityVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagParticipateReqVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagRainClientInfoVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagRealTimeInfo;
import com.cloud.baowang.activity.api.vo.redbag.RedBagSendReqVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagSendRespVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagSettlementReqVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagSettlementVO;
import com.cloud.baowang.activity.service.redbag.SiteActivityRedBagService;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@AllArgsConstructor
public class ActivityRedBagApiImpl implements ActivityRedBagApi {
    private final SiteActivityRedBagService redBagService;

    @Override
    public ResponseVO<RedBagRealTimeInfo> realTimeInfo(String siteCode) {
        return redBagService.realTimeInfo(siteCode);
    }

    @Override
    public ResponseVO<RedBagRainClientInfoVO> clientInfo(String siteCode,String timezone) {
        return ResponseVO.success(redBagService.clientInfo(siteCode,timezone));
    }

    @Override
    public ResponseVO<ToActivityVO> participate(RedBagParticipateReqVO vo) {
        ToActivityVO activityVO = redBagService.participate(vo);
        return ResponseVO.success(activityVO);
    }

    @Override
    public ResponseVO<RedBagSendRespVO> send(RedBagSendReqVO vo) {
        return ResponseVO.success(redBagService.send(vo));
    }

    @Override
    public ResponseVO<RedBagSettlementVO> settlement(RedBagSettlementReqVO reqVO) {
        return ResponseVO.success(redBagService.settlement(reqVO));
    }

    @Override
    public ResponseVO<Void> activityRedBagStartPush(String siteCode, String timeStr) {
        redBagService.activityRedBagStartPush(siteCode, timeStr);
        return ResponseVO.success();

    }

    @Override
    public ResponseVO<Void> activityRedBagEndPush(String siteCode, String timeStr) {
        redBagService.activityRedBagEndPush(siteCode, timeStr, true);
        return ResponseVO.success();
    }
}

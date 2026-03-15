package com.cloud.baowang.user.controller.activity.redbag;

import com.cloud.baowang.activity.api.api.ActivityRedBagApi;
import com.cloud.baowang.activity.api.vo.ToActivityVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagParticipateReqVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagRainClientInfoVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagSettlementReqVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagSettlementVO;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "优惠活动-红包雨")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/activityRedBag/api")
public class ActivityRedBagController {

    private final ActivityRedBagApi activityRedBagApi;

    @Operation(summary = "红包雨活动客户端详情")
    @PostMapping("clientInfo")
    public ResponseVO<RedBagRainClientInfoVO> clientInfo() {
        String siteCode= CurrReqUtils.getSiteCode();
        String timeZone= CurrReqUtils.getTimezone();
        return activityRedBagApi.clientInfo(siteCode,timeZone);
    }

    @Operation(summary = "红包雨活动参与校验")
    @PostMapping("participate")
    public ResponseVO<ToActivityVO> participate(@RequestBody RedBagParticipateReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setUserAccount(CurrReqUtils.getAccount());
        vo.setUserId(CurrReqUtils.getOneId());
        vo.setTimeZone(CurrReqUtils.getTimezone());
        vo.setReqDeviceType(CurrReqUtils.getReqDeviceType());
        return activityRedBagApi.participate(vo);
    }
}

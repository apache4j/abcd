package com.cloud.baowang.play.task.pulltask.ace.task;

import com.alibaba.fastjson.JSON;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.ace.impl.ACEGameServiceImpl;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import com.cloud.baowang.play.task.pulltask.ace.params.ACEPullBetParams;
import com.cloud.baowang.play.task.pulltask.fc.params.FCPullBetParams;
import com.cloud.baowang.play.task.pulltask.omg.constant.OmgConstant;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component(ThirdGamePullBetTaskTypeConstant.ACE_GAME_PULL_BET_TASK)
public class ACEPullBetTask extends BasePullBetTask {


    @Autowired
    private ACEGameServiceImpl aceGameService;

    @Override
    protected VenuePullBetParams pullBetRecord(VenueInfoVO venueInfoVO, String pullParamJson) {
        log.info("ACE 拉单服务开始 pullBetRecord, 入参 : {}", pullParamJson);
        ACEPullBetParams fcPullBetParams = JSON.parseObject(pullParamJson, ACEPullBetParams.class);
        VenuePullParamVO venuePullParamVO = new VenuePullParamVO();
        venuePullParamVO.setStartTime(fcPullBetParams.getStartTime());
        venuePullParamVO.setEndTime(fcPullBetParams.getEndTime());
        if (aceGameService.getBetRecordList(venueInfoVO, venuePullParamVO).isOk()) {
            log.info("ACE 拉单服务结束 pullBetRecord.");
            return genNextPullParams(fcPullBetParams);
        }
        return null;
    }

    @Override
    protected String getVenuePlatform() {
        return VenuePlatformConstants.ACE;
    }

    @Override
    protected VenuePullBetParams initPullParams() {
        long l = System.currentTimeMillis() - 1000 * 60 * 10L;
        return initPullParams(String.valueOf(l));
    }

    @Override
    protected VenuePullBetParams genNextPullParams(VenuePullBetParams currPullBetParams) {
        ACEPullBetParams params = (ACEPullBetParams) currPullBetParams;
        ACEPullBetParams newParams = new ACEPullBetParams();
        Long endTime = params.getEndTime();
        newParams.setStartTime(endTime);
        newParams.setEndTime(newParams.getStartTime() + OmgConstant.DEFAULT_STEP.longValue());
        if (newParams.getEndTime() > System.currentTimeMillis()) {
            newParams.setStartTime(System.currentTimeMillis() - OmgConstant.DEFAULT_STEP.longValue());
            newParams.setEndTime(System.currentTimeMillis());
        }
        return newParams;
    }

    @Override
    protected VenuePullBetParams initPullParams(String startTime) {
        FCPullBetParams params = new FCPullBetParams();
        params.setStartTime(Long.parseLong(startTime));
        params.setEndTime(params.getStartTime() + OmgConstant.DEFAULT_STEP.longValue());
        return params;
    }
}


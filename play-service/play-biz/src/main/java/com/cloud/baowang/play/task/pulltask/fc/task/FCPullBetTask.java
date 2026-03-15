package com.cloud.baowang.play.task.pulltask.fc.task;


import com.alibaba.fastjson.JSON;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.fc.impl.FCGamServiceImpl;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import com.cloud.baowang.play.task.pulltask.fc.params.FCPullBetParams;
import com.cloud.baowang.play.task.pulltask.omg.constant.OmgConstant;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component(ThirdGamePullBetTaskTypeConstant.FC_GAME_PULL_BET_TASK)
public class FCPullBetTask extends BasePullBetTask {


    @Autowired

    private FCGamServiceImpl fcGamService;

    @Override
    protected VenuePullBetParams pullBetRecord(VenueInfoVO venueInfoVO, String pullParamJson) {
        log.info("FC拉单服务开始pullBetRecord, 场馆信息: {} 入参 : {}", venueInfoVO, pullParamJson);
        FCPullBetParams fcPullBetParams = JSON.parseObject(pullParamJson, FCPullBetParams.class);
        VenuePullParamVO venuePullParamVO = new VenuePullParamVO();
        venuePullParamVO.setStartTime(fcPullBetParams.getStartTime());
        venuePullParamVO.setEndTime(fcPullBetParams.getEndTime());
        ResponseVO<FCPullBetParams> betRecordList = fcGamService.getBetRecordList(venueInfoVO, venuePullParamVO);
        if (betRecordList.isOk()) {
            log.info("FC拉单服务结束pullBetRecord, 返回信息: {}", betRecordList);
            return genNextPullParams(fcPullBetParams);
        }
        return null;
    }

    @Override
    protected String getVenuePlatform() {
        return VenuePlatformConstants.FC;
    }

    @Override
    protected VenuePullBetParams initPullParams() {
        long l = System.currentTimeMillis() - 1000 * 60 * 10L;
        return initPullParams(String.valueOf(l));
    }

    @Override
    protected VenuePullBetParams genNextPullParams(VenuePullBetParams currPullBetParams) {
        FCPullBetParams params = (FCPullBetParams) currPullBetParams;
        FCPullBetParams newParams = new FCPullBetParams();
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

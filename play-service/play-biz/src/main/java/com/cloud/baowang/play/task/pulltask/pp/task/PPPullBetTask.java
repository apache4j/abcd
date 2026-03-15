package com.cloud.baowang.play.task.pulltask.pp.task;


import com.alibaba.fastjson.JSON;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.pp.impl.PPGameServiceImpl;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import com.cloud.baowang.play.task.pulltask.omg.constant.OmgConstant;
import com.cloud.baowang.play.task.pulltask.pp.params.PPPullBetParams;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component(ThirdGamePullBetTaskTypeConstant.PP_GAME_PULL_BET_TASK)
public class PPPullBetTask extends BasePullBetTask {


    @Autowired
    private PPGameServiceImpl ppGameService;

    @Override
    protected VenuePullBetParams pullBetRecord(VenueInfoVO venueInfoVO, String pullParamJson) {
        log.info("PP拉单服务开始pullBetRecord, 场馆信息: {} 入参 : {}", venueInfoVO, pullParamJson);
        PPPullBetParams venuePullBetParams = JSON.parseObject(pullParamJson, PPPullBetParams.class);
        VenuePullParamVO venuePullParamVO = new VenuePullParamVO();
        venuePullParamVO.setStartTime(venuePullBetParams.getStartTime());
        venuePullParamVO.setEndTime(venuePullBetParams.getEndTime());
        ResponseVO<PPPullBetParams> betRecordList = ppGameService.getBetRecordList(venueInfoVO, venuePullParamVO);
        if (betRecordList.isOk()) {
            log.info("PP拉单服务结束pullBetRecord, 返回信息: {}", betRecordList);
            return genNextPullParams(venuePullBetParams);
        }
        return null;
    }

    @Override
    protected String getVenuePlatform() {
        return VenueEnum.PP.getVenueCode();
    }

    @Override
    protected VenuePullBetParams initPullParams() {
        long l = System.currentTimeMillis() - 1000 * 60 * 10L;
        return initPullParams(String.valueOf(l));
    }

    @Override
    protected VenuePullBetParams genNextPullParams(VenuePullBetParams currPullBetParams) {
        PPPullBetParams params = (PPPullBetParams) currPullBetParams;
        PPPullBetParams newParams = new PPPullBetParams();
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
        PPPullBetParams params = new PPPullBetParams();
        params.setStartTime(Long.parseLong(startTime));
        params.setEndTime(params.getStartTime() + OmgConstant.DEFAULT_STEP.longValue());
        return params;
    }
}

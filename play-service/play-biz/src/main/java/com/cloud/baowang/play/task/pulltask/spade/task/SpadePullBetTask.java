package com.cloud.baowang.play.task.pulltask.spade.task;


import com.alibaba.fastjson.JSON;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.spade.impl.SpadeGamServiceImpl;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import com.cloud.baowang.play.task.pulltask.spade.params.SpadePullBetParams;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component(ThirdGamePullBetTaskTypeConstant.SPADE_GAME_PULL_BET_TASK)
public class SpadePullBetTask extends BasePullBetTask {

    @Autowired
    private SpadeGamServiceImpl SpadeGamServiceImpl;

    public static final Integer DEFAULT_STEP = 10 * 60 * 1000;

    @Override
    protected VenuePullBetParams pullBetRecord(VenueInfoVO venueInfoVO, String pullParamJson) {
        log.info("SPADE拉单服务开始pullBetRecord, 场馆信息: {} 入参 : {}", venueInfoVO, pullParamJson);
        SpadePullBetParams venuePullBetParams = JSON.parseObject(pullParamJson, SpadePullBetParams.class);
        VenuePullParamVO venuePullParamVO = new VenuePullParamVO();
        venuePullParamVO.setStartTime(venuePullBetParams.getStartTime());
        venuePullParamVO.setEndTime(venuePullBetParams.getEndTime());
        ResponseVO<SpadePullBetParams> betRecordList = SpadeGamServiceImpl.getBetRecordList(venueInfoVO, venuePullParamVO);
        if (betRecordList.isOk()) {
            log.info("SPADE拉单服务结束pullBetRecord, 返回信息: {}", betRecordList);
            return genNextPullParams(venuePullBetParams);
        }
        return null;
    }

    @Override
    protected String getVenuePlatform() {
        return VenuePlatformConstants.SPADE;
    }

    @Override
    protected VenuePullBetParams initPullParams() {
        long l = System.currentTimeMillis() - DEFAULT_STEP;
        return initPullParams(String.valueOf(l));
    }

    @Override
    protected VenuePullBetParams genNextPullParams(VenuePullBetParams currPullBetParams) {
        SpadePullBetParams params = (SpadePullBetParams) currPullBetParams;
        SpadePullBetParams newParams = new SpadePullBetParams();
        Long endTime = params.getEndTime();
        newParams.setStartTime(endTime);
        newParams.setEndTime(newParams.getStartTime() + newParams.getStep());
        if (newParams.getEndTime() > System.currentTimeMillis()) {
            newParams.setStartTime(System.currentTimeMillis() - newParams.getStep());
            newParams.setEndTime(System.currentTimeMillis());
        }
        return newParams;
    }

    @Override
    protected VenuePullBetParams initPullParams(String startTime) {
        SpadePullBetParams params = new SpadePullBetParams();
        params.setStartTime(Long.parseLong(startTime));
        params.setEndTime(params.getStartTime() + params.getStep());
        return params;
    }
}

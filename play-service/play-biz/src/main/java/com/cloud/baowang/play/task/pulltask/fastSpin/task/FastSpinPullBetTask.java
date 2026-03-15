package com.cloud.baowang.play.task.pulltask.fastSpin.task;


import com.alibaba.fastjson.JSON;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.fastSpin.impl.FastSpinGamServiceImpl;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import com.cloud.baowang.play.task.pulltask.fastSpin.params.FastSpinPullBetParams;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component(ThirdGamePullBetTaskTypeConstant.FAST_SPIN_GAME_PULL_BET_TASK)
public class FastSpinPullBetTask extends BasePullBetTask {


    @Autowired
    private FastSpinGamServiceImpl fastSpinGamService;

    public static final Integer DEFAULT_STEP = 10 * 60 * 1000;

    @Override
    protected VenuePullBetParams pullBetRecord(VenueInfoVO venueInfoVO, String pullParamJson) {
        log.info("FastSpin拉单服务开始pullBetRecord, 场馆信息: {} 入参 : {}", venueInfoVO, pullParamJson);
        FastSpinPullBetParams venuePullBetParams = JSON.parseObject(pullParamJson, FastSpinPullBetParams.class);
        VenuePullParamVO venuePullParamVO = new VenuePullParamVO();
        venuePullParamVO.setStartTime(venuePullBetParams.getStartTime());
        venuePullParamVO.setEndTime(venuePullBetParams.getEndTime());
        ResponseVO<FastSpinPullBetParams> betRecordList = fastSpinGamService.getBetRecordList(venueInfoVO, venuePullParamVO);
        if (betRecordList.isOk()) {
            log.info("FastSpin拉单服务结束pullBetRecord, 返回信息: {}", betRecordList);
            return genNextPullParams(venuePullBetParams);
        }
        return null;
    }

    @Override
    protected String getVenuePlatform() {
        return VenuePlatformConstants.FASTSPIN;
    }

    @Override
    protected VenuePullBetParams initPullParams() {
        long l = System.currentTimeMillis() - DEFAULT_STEP;
        return initPullParams(String.valueOf(l));
    }

    @Override
    protected VenuePullBetParams genNextPullParams(VenuePullBetParams currPullBetParams) {
        FastSpinPullBetParams params = (FastSpinPullBetParams) currPullBetParams;
        FastSpinPullBetParams newParams = new FastSpinPullBetParams();
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
        FastSpinPullBetParams params = new FastSpinPullBetParams();
        params.setStartTime(Long.parseLong(startTime));
        params.setEndTime(params.getStartTime() + params.getStep());
        return params;
    }
}

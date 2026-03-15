/**
 * @(#)ShGamePullBetTask.java, 9月 18, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.play.task.pulltask.v8.task;

import com.alibaba.fastjson.JSON;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.v8.impl.V8GameServiceImpl;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import com.cloud.baowang.play.task.pulltask.v8.param.V8VenuePullBetParams;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Date;

@Log4j2
@Component(ThirdGamePullBetTaskTypeConstant.V8_GAME_PULL_BET_TASK)
@AllArgsConstructor
public class V8GamePullBetTask extends BasePullBetTask {

    private final V8GameServiceImpl v8GameService;

    /**
     * 间隔时间，默认：30分钟
     */
    public static final Integer DEFAULT_STEP = 10 * 60 * 1000;

    @Override
    protected VenuePullBetParams pullBetRecord(VenueInfoVO venueDetailVO, String pullParamJson) {
                V8VenuePullBetParams venuePullBetParams = JSON.parseObject(pullParamJson, V8VenuePullBetParams.class);
        log.info("V8棋牌开始拉取时间段 时间start：{}，end：{}",
                DateUtils.convertDateToString(new Date(venuePullBetParams.getStartTime())),
                DateUtils.convertDateToString(new Date(venuePullBetParams.getEndTime())));
        VenuePullParamVO venuePullParamVO = new VenuePullParamVO();
        venuePullParamVO.setStartTime(venuePullBetParams.getStartTime());
        venuePullParamVO.setEndTime(venuePullBetParams.getEndTime());
        ResponseVO<?> responseVO = v8GameService.getBetRecordList(venueDetailVO, venuePullParamVO);;
        if(responseVO.isOk()) {
            log.info("V8棋牌更新拉取时间段 时间start：{}，end：{}",DateUtils.convertDateToString(new Date(venuePullBetParams.getStartTime()))
                    ,DateUtils.convertDateToString(new Date(venuePullBetParams.getEndTime())));
            return genNextPullParams(venuePullBetParams);
        }
        return null;
    }



    @Override
    protected String getVenuePlatform() {
        return VenuePlatformConstants.V8;
    }

    @Override
    protected VenuePullBetParams initPullParams() {
        V8VenuePullBetParams venuePullBetParams = new V8VenuePullBetParams();
        venuePullBetParams.setStartTime(System.currentTimeMillis());
        venuePullBetParams.setStep(DEFAULT_STEP.longValue());
        venuePullBetParams.setEndTime(venuePullBetParams.getStartTime()+venuePullBetParams.getStep());
        return venuePullBetParams;
    }

    @Override
    protected VenuePullBetParams genNextPullParams(VenuePullBetParams currPullBetParams) {
        V8VenuePullBetParams params = (V8VenuePullBetParams) currPullBetParams;
        V8VenuePullBetParams newPullBetParams = new V8VenuePullBetParams();
        newPullBetParams.setStartTime(params.getStartTime());
        newPullBetParams.setEndTime(params.getEndTime());

        long timeInterval = params.getStep() == null ? DEFAULT_STEP : params.getStep();
        newPullBetParams.setStep(timeInterval);

        newPullBetParams.setStartTime(newPullBetParams.getEndTime() - newPullBetParams.getStep());

        newPullBetParams.setEndTime(newPullBetParams.getEndTime() + newPullBetParams.getStep()*2);
        if (newPullBetParams.getEndTime() > System.currentTimeMillis()) {
            newPullBetParams.setStartTime(System.currentTimeMillis() - newPullBetParams.getStep());
            newPullBetParams.setEndTime(System.currentTimeMillis());
        }

        log.info("{}下一次拉单参数: {} | {}", VenueEnum.V8.getVenueName(), newPullBetParams.getStartTime(),
                newPullBetParams.getEndTime() );
        return newPullBetParams;
    }

    @Override
    protected VenuePullBetParams initPullParams(String startTime) {
        // 时间转换
        V8VenuePullBetParams venuePullBetParams = new V8VenuePullBetParams();
        Long startTimeLong = Long.valueOf(startTime);
        venuePullBetParams.setStartTime(startTimeLong);
        venuePullBetParams.setStep(DEFAULT_STEP.longValue());
        venuePullBetParams.setEndTime(startTimeLong);
        // 获取下次拉单参数
        V8VenuePullBetParams TfVenuePullBetParams = (V8VenuePullBetParams)genNextPullParams(venuePullBetParams);
        // 本次结束时间
        TfVenuePullBetParams.setManualCurrentPullEndTime(TfVenuePullBetParams.getEndTime().toString());
        return TfVenuePullBetParams;
    }
}

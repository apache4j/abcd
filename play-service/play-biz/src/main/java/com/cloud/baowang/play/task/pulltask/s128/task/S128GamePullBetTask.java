/**
 * @(#)ShGamePullBetTask.java, 9月 18, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.play.task.pulltask.s128.task;

import com.alibaba.fastjson.JSON;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.s128.impl.S128GameServiceImpl;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import com.cloud.baowang.play.task.pulltask.s128.params.S128VenuePullBetParams;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * <h2></h2>
 * @author wayne
 * date 2023/9/18
 */
@Log4j2
@Component(ThirdGamePullBetTaskTypeConstant.S128_GAME_PULL_BET_TASK)
@AllArgsConstructor
public class S128GamePullBetTask extends BasePullBetTask {
    /**
     * 拉单最大间隔 30 min
     */
    private final static Long DEFAULT_STEP = 10 * 60 * 1000L;


    private final S128GameServiceImpl s128GameService;

    @Override
    protected VenuePullBetParams pullBetRecord(VenueInfoVO venueDetailVO, String pullParamJson) {
        S128VenuePullBetParams venuePullBetParams = JSON.parseObject(pullParamJson, S128VenuePullBetParams.class);
        log.info("神话游戏开始拉取时间段 时间start：{}，end：{}",
                DateUtils.convertDateToString(new Date(venuePullBetParams.getStartTime())),
                DateUtils.convertDateToString(new Date(venuePullBetParams.getEndTime())));
        VenuePullParamVO venuePullParamVO = new VenuePullParamVO();
        venuePullParamVO.setStartTime(venuePullBetParams.getStartTime());
        venuePullParamVO.setEndTime(venuePullBetParams.getEndTime());
        ResponseVO<?> responseVO = s128GameService.getBetRecordList(venueDetailVO, venuePullParamVO);;
        if(responseVO.isOk()) {
            log.info("神话游戏更新拉取时间段 时间start：{}，end：{}",DateUtils.convertDateToString(new Date(venuePullBetParams.getStartTime()))
                    ,DateUtils.convertDateToString(new Date(venuePullBetParams.getEndTime())));
            return genNextPullParams(venuePullBetParams);
        }
        return null;
    }



    @Override
    protected String getVenuePlatform() {
        return VenuePlatformConstants.S128;
    }

    @Override
    protected VenuePullBetParams initPullParams() {
        S128VenuePullBetParams venuePullBetParams = new S128VenuePullBetParams();
        venuePullBetParams.setStartTime(System.currentTimeMillis());
        venuePullBetParams.setStep(DEFAULT_STEP);
        venuePullBetParams.setEndTime(venuePullBetParams.getStartTime()+venuePullBetParams.getStep());
        return venuePullBetParams;
    }

    @Override
    protected VenuePullBetParams genNextPullParams(VenuePullBetParams currPullBetParams) {
        S128VenuePullBetParams params = (S128VenuePullBetParams) currPullBetParams;
        S128VenuePullBetParams newPullBetParams = new S128VenuePullBetParams();
        newPullBetParams.setStartTime(params.getStartTime());
        newPullBetParams.setEndTime(params.getEndTime());

        long timeInterval = params.getStep() == null ? DEFAULT_STEP : params.getStep();
        newPullBetParams.setStep(timeInterval);

        newPullBetParams.setStartTime(newPullBetParams.getEndTime() - newPullBetParams.getStep());

        newPullBetParams.setEndTime(newPullBetParams.getEndTime() + newPullBetParams.getStep());
        if (newPullBetParams.getEndTime() > System.currentTimeMillis()) {
            newPullBetParams.setStartTime(System.currentTimeMillis() - newPullBetParams.getStep());
            newPullBetParams.setEndTime(System.currentTimeMillis());
        }

        log.info("{}下一次拉单参数: {} | {}", VenueEnum.S128.getVenueName(), newPullBetParams.getStartTime(),
                newPullBetParams.getEndTime() );
        return newPullBetParams;
    }

    @Override
    protected VenuePullBetParams initPullParams(String startTime) {
        // 时间转换
        S128VenuePullBetParams venuePullBetParams = new S128VenuePullBetParams();
        Long startTimeLong = Long.valueOf(startTime);
        venuePullBetParams.setStartTime(startTimeLong);
        venuePullBetParams.setEndTime(startTimeLong + DEFAULT_STEP);
//        // 获取下次拉单参数
//        S128VenuePullBetParams S128VenuePullBetParams = (S128VenuePullBetParams)genNextPullParams(venuePullBetParams);
//        // 本次结束时间
//        S128VenuePullBetParams.setManualCurrentPullEndTime(S128VenuePullBetParams.getEndTime().toString());
//        return S128VenuePullBetParams;
        return venuePullBetParams;
    }
}

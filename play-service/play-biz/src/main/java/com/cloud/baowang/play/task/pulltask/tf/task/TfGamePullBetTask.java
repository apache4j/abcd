/**
 * @(#)ShGamePullBetTask.java, 9月 18, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.play.task.pulltask.tf.task;

import com.alibaba.fastjson.JSON;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.tf.constant.TfConstant;
import com.cloud.baowang.play.game.tf.impl.TFGameServiceImpl;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import com.cloud.baowang.play.task.pulltask.tf.param.TfVenuePullBetParams;
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
@Component(ThirdGamePullBetTaskTypeConstant.TF_GAME_PULL_BET_TASK)
@AllArgsConstructor
public class TfGamePullBetTask  extends BasePullBetTask {

    private final TFGameServiceImpl tfGameService;

    @Override
    protected VenuePullBetParams pullBetRecord(VenueInfoVO venueDetailVO, String pullParamJson) {
                TfVenuePullBetParams venuePullBetParams = JSON.parseObject(pullParamJson, TfVenuePullBetParams.class);
        log.info("TF电竞开始拉取时间段 时间start：{}，end：{}",
                DateUtils.convertDateToString(new Date(venuePullBetParams.getStartTime())),
                DateUtils.convertDateToString(new Date(venuePullBetParams.getEndTime())));
        VenuePullParamVO venuePullParamVO = new VenuePullParamVO();
        venuePullParamVO.setStartTime(venuePullBetParams.getStartTime());
        venuePullParamVO.setEndTime(venuePullBetParams.getEndTime());
        ResponseVO<?> responseVO = tfGameService.getBetRecordList(venueDetailVO, venuePullParamVO);;
        if(responseVO.isOk()) {
            log.info("TF电竞更新拉取时间段 时间start：{}，end：{}",DateUtils.convertDateToString(new Date(venuePullBetParams.getStartTime()))
                    ,DateUtils.convertDateToString(new Date(venuePullBetParams.getEndTime())));
            return genNextPullParams(venuePullBetParams);
        }
        return null;
    }



    @Override
    protected String getVenuePlatform() {
        return VenuePlatformConstants.TF;
    }

    @Override
    protected VenuePullBetParams initPullParams() {
        TfVenuePullBetParams venuePullBetParams = new TfVenuePullBetParams();
        venuePullBetParams.setStartTime(System.currentTimeMillis());
        venuePullBetParams.setStep(TfConstant.DEFAULT_STEP.longValue());
        venuePullBetParams.setEndTime(venuePullBetParams.getStartTime()+venuePullBetParams.getStep());
        return venuePullBetParams;
    }

    @Override
    protected VenuePullBetParams genNextPullParams(VenuePullBetParams currPullBetParams) {
        TfVenuePullBetParams params = (TfVenuePullBetParams) currPullBetParams;
        TfVenuePullBetParams newPullBetParams = new TfVenuePullBetParams();
        newPullBetParams.setStartTime(params.getStartTime());
        newPullBetParams.setEndTime(params.getEndTime());

        long timeInterval = params.getStep() == null ? TfConstant.DEFAULT_STEP : params.getStep();
        newPullBetParams.setStep(timeInterval);

        newPullBetParams.setStartTime(newPullBetParams.getEndTime() - newPullBetParams.getStep());

        newPullBetParams.setEndTime(newPullBetParams.getEndTime() + newPullBetParams.getStep());
        if (newPullBetParams.getEndTime() > System.currentTimeMillis()) {
            newPullBetParams.setStartTime(System.currentTimeMillis() - newPullBetParams.getStep());
            newPullBetParams.setEndTime(System.currentTimeMillis());
        }

        log.info("{}下一次拉单参数: {} | {}", VenueEnum.TF.getVenueName(), newPullBetParams.getStartTime(),
                newPullBetParams.getEndTime() );
        return newPullBetParams;
    }

    @Override
    protected VenuePullBetParams initPullParams(String startTime) {
        // 时间转换
        TfVenuePullBetParams venuePullBetParams = new TfVenuePullBetParams();
        Long startTimeLong = Long.valueOf(startTime);
        venuePullBetParams.setStartTime(startTimeLong);
        venuePullBetParams.setEndTime(startTimeLong + TfConstant.DEFAULT_STEP);

        // 获取下次拉单参数
//        TfVenuePullBetParams TfVenuePullBetParams = (TfVenuePullBetParams)genNextPullParams(venuePullBetParams);
//        // 本次结束时间
//        TfVenuePullBetParams.setManualCurrentPullEndTime(TfVenuePullBetParams.getEndTime().toString());
//        return TfVenuePullBetParams;
        return venuePullBetParams;
    }
}

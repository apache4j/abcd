/**
 * @(#)ShGamePullBetTask.java, 9月 18, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.play.task.pulltask.lgd.task;

import com.alibaba.fastjson.JSON;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.lgd.impl.LgdServiceImpl;
import com.cloud.baowang.play.game.tf.constant.TfConstant;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import com.cloud.baowang.play.task.pulltask.lgd.param.LGDVenuePullBetParams;
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
@Component(ThirdGamePullBetTaskTypeConstant.LGD_GAME_PULL_BET_TASK)
@AllArgsConstructor
public class LgdGamePullBetTask extends BasePullBetTask {

    private final LgdServiceImpl lgdService;

    /**
     * 间隔时间，默认：10分钟
     */
    public static final Integer DEFAULT_STEP =10 * 60 * 1000;

    @Override
    protected VenuePullBetParams pullBetRecord(VenueInfoVO venueDetailVO, String pullParamJson) {
        LGDVenuePullBetParams venuePullBetParams = JSON.parseObject(pullParamJson, LGDVenuePullBetParams.class);
        log.info("LGD电子开始拉取时间段 时间start：{}，end：{}",
                DateUtils.convertDateToString(new Date(venuePullBetParams.getStartTime())),
                DateUtils.convertDateToString(new Date(venuePullBetParams.getEndTime())));
        VenuePullParamVO venuePullParamVO = new VenuePullParamVO();
        venuePullParamVO.setStartTime(venuePullBetParams.getStartTime());
        venuePullParamVO.setEndTime(venuePullBetParams.getEndTime());
        ResponseVO<?> responseVO = lgdService.getBetRecordList(venueDetailVO, venuePullParamVO);;
        if(responseVO.isOk()) {
            log.info("LGD电子更新拉取时间段 时间start：{}，end：{}",DateUtils.convertDateToString(new Date(venuePullBetParams.getStartTime()))
                    ,DateUtils.convertDateToString(new Date(venuePullBetParams.getEndTime())));
            return genNextPullParams(venuePullBetParams);
        }
        return null;
    }



    @Override
    protected String getVenuePlatform() {
        return VenuePlatformConstants.LGD;
    }

    @Override
    protected VenuePullBetParams initPullParams() {
        LGDVenuePullBetParams venuePullBetParams = new LGDVenuePullBetParams();
        venuePullBetParams.setStartTime(System.currentTimeMillis());
        venuePullBetParams.setStep(TfConstant.DEFAULT_STEP.longValue());
        venuePullBetParams.setEndTime(venuePullBetParams.getStartTime()+venuePullBetParams.getStep());
        return venuePullBetParams;
    }

    @Override
    protected VenuePullBetParams genNextPullParams(VenuePullBetParams currPullBetParams) {
        LGDVenuePullBetParams params = (LGDVenuePullBetParams) currPullBetParams;
        LGDVenuePullBetParams newPullBetParams = new LGDVenuePullBetParams();
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

        log.info("{}下一次拉单参数: {} | {}", VenueEnum.LGD.getVenueName(), newPullBetParams.getStartTime(),
                newPullBetParams.getEndTime() );
        return newPullBetParams;
    }

    @Override
    protected VenuePullBetParams initPullParams(String startTime) {
        // 时间转换
        LGDVenuePullBetParams venuePullBetParams = new LGDVenuePullBetParams();
        long startTimeLong = Long.parseLong(startTime);
        venuePullBetParams.setStartTime(startTimeLong);
        venuePullBetParams.setEndTime(startTimeLong + DEFAULT_STEP.longValue()*2);
//        // 获取下次拉单参数
//        LGDVenuePullBetParams lgdVenuePullBetParams = (LGDVenuePullBetParams)genNextPullParams(venuePullBetParams);
//        // 本次结束时间
//        lgdVenuePullBetParams.setManualCurrentPullEndTime(lgdVenuePullBetParams.getEndTime().toString());
//        return lgdVenuePullBetParams;
        return venuePullBetParams;
    }
}

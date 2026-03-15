/**
 * @(#)ShGamePullBetTask.java, 9月 18, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.play.task.pulltask.sh.task;

import com.alibaba.fastjson.JSON;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.sh.constant.SHConstantApi;
import com.cloud.baowang.play.game.sh.impl.ShGameServiceImpl;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import com.cloud.baowang.play.task.pulltask.sh.params.ShVenuePullBetParams;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * <h2></h2>
 *
 * @author wayne
 * date 2023/9/18
 */
@Log4j2
@Component(ThirdGamePullBetTaskTypeConstant.SH_GAME_PULL_BET_TASK)
@AllArgsConstructor
public class ShGamePullBetTask extends BasePullBetTask {

    private final ShGameServiceImpl shGameService;

    @Override
    protected VenuePullBetParams pullBetRecord(VenueInfoVO venueDetailVO, String pullParamJson) {
        ShVenuePullBetParams venuePullBetParams = JSON.parseObject(pullParamJson, ShVenuePullBetParams.class);
        int i = 0;
        boolean pullType = true;
        while (pullType) {

            //如果这个字段有值并且是 false = 手动拉单,这么说只需要执行一次
            if(venuePullBetParams.getPullType() != null && !venuePullBetParams.getPullType()){
                pullType = false;
            }

            i++;
            log.info("视讯拉单次数:{},开始时间start：{}，end：{}", i, venuePullBetParams.getStartTime(), venuePullBetParams.getEndTime());
            VenuePullParamVO venuePullParamVO = new VenuePullParamVO();
            venuePullParamVO.setStartTime(venuePullBetParams.getStartTime());
            venuePullParamVO.setEndTime(venuePullBetParams.getEndTime());
            ResponseVO<?> responseVO = shGameService.getBetRecordList(venueDetailVO, venuePullParamVO);
            log.info("视讯拉单次数:{},结束", i);

            if(venuePullBetParams.getPullType() != null && !venuePullBetParams.getPullType()){
                return null;
            }

            if(!responseVO.isOk()){
                return venuePullBetParams;
            }
            venuePullBetParams = (ShVenuePullBetParams) genNextPullParams(venuePullBetParams);
            if (venuePullBetParams.getEndTime() >= System.currentTimeMillis()) {
                return venuePullBetParams;
            }

            try {
                Thread.sleep(1000);
            }catch (Exception e){
                log.error(e);
            }
        }
        return null;
    }


    @Override
    protected String getVenuePlatform() {
        return VenuePlatformConstants.SH;
    }

    @Override
    protected VenuePullBetParams initPullParams() {
        ShVenuePullBetParams venuePullBetParams = new ShVenuePullBetParams();
        venuePullBetParams.setStartTime(System.currentTimeMillis());
        venuePullBetParams.setStep(SHConstantApi.DEFAULT_STEP.longValue());
        venuePullBetParams.setEndTime(venuePullBetParams.getStartTime() + venuePullBetParams.getStep());
        return venuePullBetParams;
    }

    @Override
    protected VenuePullBetParams genNextPullParams(VenuePullBetParams currPullBetParams) {
        ShVenuePullBetParams params = (ShVenuePullBetParams) currPullBetParams;
        ShVenuePullBetParams newPullBetParams = new ShVenuePullBetParams();
        newPullBetParams.setStartTime(params.getStartTime());
        newPullBetParams.setEndTime(params.getEndTime());

        long timeInterval = params.getStep() == null ? SHConstantApi.DEFAULT_STEP : params.getStep();
        newPullBetParams.setStep(timeInterval);

        newPullBetParams.setStartTime(newPullBetParams.getEndTime() - newPullBetParams.getStep());

        newPullBetParams.setEndTime(newPullBetParams.getEndTime() + newPullBetParams.getStep());
        if (newPullBetParams.getEndTime() > System.currentTimeMillis()) {
            newPullBetParams.setStartTime(System.currentTimeMillis() - newPullBetParams.getStep());
            newPullBetParams.setEndTime(System.currentTimeMillis());
        }
        return newPullBetParams;
    }

    @Override
    protected VenuePullBetParams initPullParams(String startTime) {
        // 时间转换
        ShVenuePullBetParams venuePullBetParams = new ShVenuePullBetParams();
        Long startTimeLong = Long.valueOf(startTime);
        venuePullBetParams.setStartTime(startTimeLong);
        venuePullBetParams.setEndTime(startTimeLong + SHConstantApi.DEFAULT_STEP.longValue());
        // 获取下次拉单参数
//        ShVenuePullBetParams shVenuePullBetParams = (ShVenuePullBetParams) genNextPullParams(venuePullBetParams);
        // 本次结束时间
//        shVenuePullBetParams.setManualCurrentPullEndTime(shVenuePullBetParams.getEndTime().toString());
        return venuePullBetParams;
    }
}

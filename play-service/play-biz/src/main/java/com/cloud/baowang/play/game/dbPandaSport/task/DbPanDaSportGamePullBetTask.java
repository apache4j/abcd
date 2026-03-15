/**
 * @(#)ShGamePullBetTask.java, 9月 18, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.play.game.dbPandaSport.task;

import com.alibaba.fastjson.JSON;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.dbDj.DbDJGameServiceImpl;
import com.cloud.baowang.play.game.dbDj.constant.DbDJConstant;
import com.cloud.baowang.play.game.dbDj.param.DbDJVenuePullBetParams;
import com.cloud.baowang.play.game.dbPandaSport.DbPanDaSportServiceImpl;
import com.cloud.baowang.play.game.dbPandaSport.constant.DbPanDaSportConstant;
import com.cloud.baowang.play.game.sh.constant.SHConstantApi;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import com.cloud.baowang.play.task.pulltask.sh.params.ShVenuePullBetParams;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component(ThirdGamePullBetTaskTypeConstant.DB_PANDA_SPORT_GAME_PULL_BET_TASK)
@AllArgsConstructor
public class DbPanDaSportGamePullBetTask extends BasePullBetTask {

    private final DbPanDaSportServiceImpl dbPanDaSportService;

    public static final Integer DEFAULT_STEP = 20 * 60 * 1000;


    @Override
    protected VenuePullBetParams pullBetRecord(VenueInfoVO venueDetailVO, String pullParamJson) {
        DbDJVenuePullBetParams venuePullBetParams = JSON.parseObject(pullParamJson, DbDJVenuePullBetParams.class);
        int i = 0;
        boolean pullType = true;
        while (pullType) {

            //如果这个字段有值并且是 false = 手动拉单,这么说只需要执行一次
            if (venuePullBetParams.getPullType() != null && !venuePullBetParams.getPullType()) {
                pullType = false;
            }

            i++;
            log.info("DB熊猫拉单次数:{},开始时间start：{}，end：{}", i, venuePullBetParams.getStartTime(), venuePullBetParams.getEndTime());
            VenuePullParamVO venuePullParamVO = new VenuePullParamVO();
            venuePullParamVO.setStartTime(venuePullBetParams.getStartTime());
            venuePullParamVO.setEndTime(venuePullBetParams.getEndTime());
            ResponseVO<?> responseVO = dbPanDaSportService.getBetRecordList(venueDetailVO, venuePullParamVO);
            log.info("DB熊猫拉单次数:{},结束", i);

            if (venuePullBetParams.getPullType() != null && !venuePullBetParams.getPullType()) {
                return null;
            }

            if (!responseVO.isOk()) {
                return venuePullBetParams;
            }
            venuePullBetParams = (DbDJVenuePullBetParams) genNextPullParams(venuePullBetParams);
            if (venuePullBetParams.getEndTime() >= System.currentTimeMillis()) {
                return venuePullBetParams;
            }

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                log.error(e);
            }
        }
        return null;
    }


    @Override
    protected String getVenuePlatform() {
        return VenuePlatformConstants.DB_PANDA_SPORT;
    }

    @Override
    protected VenuePullBetParams initPullParams() {
        long nowStart = System.currentTimeMillis();
        long startTime = nowStart - (DEFAULT_STEP + 5 * 60); // 当前系统时间 - 30分钟 + 5分钟 = 开始时间

        //开始时间 + 29分钟后为结束时间
        long endTime = startTime + DEFAULT_STEP;

        DbDJVenuePullBetParams venuePullBetParams = new DbDJVenuePullBetParams();
        venuePullBetParams.setStartTime(startTime);
        venuePullBetParams.setEndTime(endTime);
        return venuePullBetParams;
    }

    @Override
    protected VenuePullBetParams genNextPullParams(VenuePullBetParams currPullBetParams) {
        DbDJVenuePullBetParams params = (DbDJVenuePullBetParams) currPullBetParams;
        DbDJVenuePullBetParams newPullBetParams = new DbDJVenuePullBetParams();

        //这次的结束时间等于下一次的开始时间
        long startTime = params.getEndTime();

        //这次的开始时间 + 30分钟等于结束时间
        long endTime = startTime + DEFAULT_STEP;

        long now = System.currentTimeMillis();

        //结束时间如果 > 当前时间 - 5分钟 那么结束时间就按照 当前时间 - 5分钟
        if(endTime > now){
            endTime = now;
        }
        newPullBetParams.setStartTime(startTime);
        newPullBetParams.setEndTime(endTime);
        return newPullBetParams;
    }

    @Override
    protected VenuePullBetParams initPullParams(String startTime) {
        // 时间转换
        ShVenuePullBetParams venuePullBetParams = new ShVenuePullBetParams();
        Long startTimeLong = Long.valueOf(startTime);
        venuePullBetParams.setStartTime(startTimeLong);
        venuePullBetParams.setEndTime(startTimeLong + DEFAULT_STEP.longValue());
        // 获取下次拉单参数
//        ShVenuePullBetParams shVenuePullBetParams = (ShVenuePullBetParams) genNextPullParams(venuePullBetParams);
        // 本次结束时间
//        shVenuePullBetParams.setManualCurrentPullEndTime(shVenuePullBetParams.getEndTime().toString());
        return venuePullBetParams;
    }
}

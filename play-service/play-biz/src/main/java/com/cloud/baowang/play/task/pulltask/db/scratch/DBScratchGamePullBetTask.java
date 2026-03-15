/**
 * @(#)ShGamePullBetTask.java, 9月 18, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.play.task.pulltask.db.scratch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.db.fishing.DBFishingServiceImpl;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import com.cloud.baowang.play.task.pulltask.db.vo.DBGameVenuePullBetParams;
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
@Component(ThirdGamePullBetTaskTypeConstant.DB_SCRATCH_LOTTERY_GAME_PULL_BET_TASK)
@AllArgsConstructor
public class DBScratchGamePullBetTask extends BasePullBetTask {

    private final DBFishingServiceImpl fishingService;

    /**
     * 最多支持5分钟
     */
    public static final Long DEFAULT_STEP = 3* 60 * 1000L;

    /**
     * 自动拉单最多1分钟
     */
    public static final Long AUTO_STEP = 3*60 * 1000L;


    @Override
    protected VenuePullBetParams pullBetRecord(VenueInfoVO venueDetailVO, String pullParamJson) {
        JSONObject jsonObject = JSONObject.parseObject(pullParamJson);
        log.info("400003:{}" , jsonObject);
        VenuePullParamVO venuePullParamVO = new VenuePullParamVO();
        venuePullParamVO.setStartTime(jsonObject.getLong("startTime"));
        venuePullParamVO.setEndTime(jsonObject.getLong("endTime"));
        venuePullParamVO.setPullType(jsonObject.getBoolean("pullType"));
        fishingService.getBetRecordList(venueDetailVO, venuePullParamVO);
        try {
            Thread.sleep(1000);
        }catch (Exception e){
            log.error(e);
        }
        return genNextPullParams(JSON.parseObject(pullParamJson, DBGameVenuePullBetParams.class));

    }


    @Override
    protected String getVenuePlatform() {
        return VenuePlatformConstants.DBSCRATCH;
    }

    @Override
    protected VenuePullBetParams initPullParams() {
        DBGameVenuePullBetParams venuePullBetParams = new DBGameVenuePullBetParams();
        long startTime = System.currentTimeMillis()-2*DEFAULT_STEP;
        venuePullBetParams.setStartTime(startTime);
        venuePullBetParams.setStep(AUTO_STEP);
        venuePullBetParams.setEndTime(venuePullBetParams.getStartTime()+DEFAULT_STEP);
        venuePullBetParams.setPullType(true);
        log.info("400007-auto"+venuePullBetParams);
        return venuePullBetParams;
    }

    @Override
    protected VenuePullBetParams genNextPullParams(VenuePullBetParams currPullBetParams) {
        DBGameVenuePullBetParams params = (DBGameVenuePullBetParams) currPullBetParams;
        DBGameVenuePullBetParams newPullBetParams = new DBGameVenuePullBetParams();
        Long startTime = params.getEndTime();
        newPullBetParams.setStartTime(startTime);
        newPullBetParams.setEndTime(newPullBetParams.getStartTime()+AUTO_STEP);
        newPullBetParams.setPullType(params.getPullType());
        newPullBetParams.setStep(AUTO_STEP);
        log.info("{}下一次拉单参数: {} | {}", VenueEnum.DBSCRATCH.getVenueName(), newPullBetParams.getStartTime(),
                newPullBetParams.getEndTime());
        return newPullBetParams;
    }

    @Override
    protected VenuePullBetParams initPullParams(String startTime) {
        // 时间转换

        DBGameVenuePullBetParams venuePullBetParams = new DBGameVenuePullBetParams();
        long startTimeLong = Long.parseLong(startTime);
        venuePullBetParams.setStartTime(startTimeLong);
        venuePullBetParams.setEndTime(startTimeLong + AUTO_STEP);
        venuePullBetParams.setPullType(true);
        venuePullBetParams.setManualCurrentPullEndTime(String.valueOf(venuePullBetParams.getEndTime()));
        log.info("400007-hand"+venuePullBetParams);
        return venuePullBetParams;
    }
}

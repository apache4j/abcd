/**
 * @(#)ShGamePullBetTask.java, 9月 18, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.play.task.pulltask.winto;

import com.alibaba.fastjson.JSON;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.winto.WintoEvgServiceImpl;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import com.cloud.baowang.play.task.pulltask.db.vo.DBGameVenuePullBetParams;
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
@Component(ThirdGamePullBetTaskTypeConstant.WINTO_EVG_GAME_PULL_BET_TASK)
@AllArgsConstructor
public class WintoEvgGamePullBetTask extends BasePullBetTask {

    private final WintoEvgServiceImpl wintoEVGService;

    /**
     * 最多支持5分钟
     */
    public static final Long DEFAULT_STEP = 5 * 60 * 1000L;



    protected VenuePullBetParams pullBetRecord(VenueInfoVO venueDetailVO, String pullParamJson) {
        DBGameVenuePullBetParams venuePullBetParams = JSON.parseObject(pullParamJson, DBGameVenuePullBetParams.class);
        int i = 0;
        boolean pullType = true;
        log.info("winto_电子拉单:{}",pullParamJson);
        while (pullType) {

            //如果这个字段有值并且是 false = 手动拉单,这么说只需要执行一次
            if (venuePullBetParams.getPullType() != null && !venuePullBetParams.getPullType()) {
                pullType = false;
            }

            i++;
            log.info("{}:{},开始时间start：{}，end：{}", venueDetailVO.getVenueCode(),i, venuePullBetParams.getStartTime(), venuePullBetParams.getEndTime());
            VenuePullParamVO venuePullParamVO = new VenuePullParamVO();
            venuePullParamVO.setStartTime(venuePullBetParams.getStartTime());
            venuePullParamVO.setEndTime(venuePullBetParams.getEndTime());
            ResponseVO<?> responseVO = wintoEVGService.getBetRecordList(venueDetailVO, venuePullParamVO);
            log.info("{}:{},结束End",venueDetailVO.getVenueCode(), i);

            if (venuePullBetParams.getPullType() != null && !venuePullBetParams.getPullType()) {
                return null;
            }

            if (!responseVO.isOk()) {
                return venuePullBetParams;
            }
            venuePullBetParams = (DBGameVenuePullBetParams) genNextPullParams(venuePullBetParams);
            if (venuePullBetParams.getEndTime() >= System.currentTimeMillis()) {
                log.info("{}:{},这次拉单结束",venueDetailVO.getVenueCode(), i);
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
        return VenuePlatformConstants.WINTO_EVG;
    }


    protected VenuePullBetParams initPullParams() {
        long nowStart = System.currentTimeMillis();
        long startTime = nowStart - (20 * 60 * 1000); // 当前系统时间 - 20分钟 = 开始时间

        DBGameVenuePullBetParams venuePullBetParams = new DBGameVenuePullBetParams();
        venuePullBetParams.setStartTime(startTime);
        venuePullBetParams.setEndTime(nowStart);
        return venuePullBetParams;
    }



    protected VenuePullBetParams genNextPullParams(VenuePullBetParams currPullBetParams) {
        DBGameVenuePullBetParams params = (DBGameVenuePullBetParams) currPullBetParams;
        DBGameVenuePullBetParams newPullBetParams = new DBGameVenuePullBetParams();

        long startTime = params.getEndTime();

        long endTime = startTime + (20 * 60 * 1000);

        long now = System.currentTimeMillis();

        if(endTime > now){
            endTime = now;
        }
        newPullBetParams.setStartTime(startTime);
        newPullBetParams.setEndTime(endTime);
        log.info("Winto电子下一次拉单参数 : "+newPullBetParams);
        return newPullBetParams;
    }

    protected VenuePullBetParams initPullParams(String startTime) {
        // 时间转换
        ShVenuePullBetParams venuePullBetParams = new ShVenuePullBetParams();
        long startTimeLong = Long.parseLong(startTime);
        venuePullBetParams.setStartTime(startTimeLong);
        venuePullBetParams.setEndTime(startTimeLong + (20 * 60 * 1000));
        return venuePullBetParams;
    }
}

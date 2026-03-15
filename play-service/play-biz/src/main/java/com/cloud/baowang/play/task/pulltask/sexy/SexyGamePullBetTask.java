/**
 * @(#)ShGamePullBetTask.java, 9月 18, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.play.task.pulltask.sexy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.sexy.SexyServiceImpl;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import com.cloud.baowang.play.task.pulltask.jdb.vo.JdbVenuePullBetParams;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;


@Log4j2
@Component(ThirdGamePullBetTaskTypeConstant.SEXY_GAME_PULL_BET_TASK)
@AllArgsConstructor
public class SexyGamePullBetTask extends BasePullBetTask {

    private final SexyServiceImpl sexyService;

    public static final Long DEFAULT_STEP = 2 * 60 * 1000L;



    @Override
    protected VenuePullBetParams pullBetRecord(VenueInfoVO venueDetailVO, String pullParamJson) {
        JdbVenuePullBetParams venuePullBetParams = JSON.parseObject(pullParamJson, JdbVenuePullBetParams.class);
        boolean pullType = venuePullBetParams.getPullType();
        log.info("sexy 拉单开始: {}, 类型: {}", pullParamJson, pullType ? "自动" : "手动");

        int maxLoopCount = 50;
        int loopCount = 0;

        while (true) {
            loopCount++;
            if (loopCount > maxLoopCount) {
                log.warn("{}: 超过最大拉单次数({})，强制结束", venueDetailVO.getVenueCode(), maxLoopCount);
                break;
            }

            // 构建请求参数
            VenuePullParamVO venuePullParamVO = new VenuePullParamVO();
            venuePullParamVO.setStartTime(venuePullBetParams.getStartTime());
            venuePullParamVO.setEndTime(venuePullBetParams.getEndTime());
            venuePullParamVO.setPullType(pullType);

            // 调用拉单接口
            ResponseVO<?> responseVO = sexyService.getBetRecordList(venueDetailVO, venuePullParamVO);
            if (!responseVO.isOk()) {
                log.warn("{}: 第{}次拉单失败，结束。响应: {}", venueDetailVO.getVenueCode(), loopCount, responseVO);
                break;
            }
            // 生成下一次拉单参数
            venuePullBetParams = pullType
                    ? (JdbVenuePullBetParams) genNextPullParams(venuePullBetParams)
                    : (JdbVenuePullBetParams) initPullParams(String.valueOf(venuePullParamVO.getEndTime()));

            long now = System.currentTimeMillis()-DEFAULT_STEP;
            if (venuePullBetParams.getEndTime() >= now) {
                log.info("{}: 第{}次拉单结束，已追平当前时间。", venueDetailVO.getVenueCode(), loopCount);
                break;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("sexy 拉单-线程中断: ", e);
                break;
            }
        }
        return venuePullBetParams;
    }




    @Override
    protected String getVenuePlatform() {
        return VenuePlatformConstants.SEXY;
    }

    @Override
    protected VenuePullBetParams initPullParams() {
        long nowStart = System.currentTimeMillis()-DEFAULT_STEP;
        long startTime = nowStart - (20 * 60 * 1000);

        JdbVenuePullBetParams venuePullBetParams = new JdbVenuePullBetParams();
        venuePullBetParams.setStartTime(startTime);
        venuePullBetParams.setEndTime(nowStart);
        venuePullBetParams.setPullType(true);
        log.info("sexy-auto : "+venuePullBetParams);
        return venuePullBetParams;
    }

    @Override
    protected VenuePullBetParams genNextPullParams(VenuePullBetParams currPullBetParams) {
        JdbVenuePullBetParams params = (JdbVenuePullBetParams) currPullBetParams;
        JdbVenuePullBetParams newPullBetParams = new JdbVenuePullBetParams();

        long startTime = params.getEndTime();
        long now = System.currentTimeMillis();
        long latestAllowTime = now - DEFAULT_STEP;

        long endTime = startTime + (10 * 60 * 1000);

        // endTime 不能超过 当前时间 - DEFAULT_STEP
        if (endTime > latestAllowTime) {
            endTime = latestAllowTime;
        }

        // 如果由于延迟等原因导致 endTime <= startTime，则强制推进1分钟
        if (endTime - startTime < DEFAULT_STEP) {
            endTime = startTime + DEFAULT_STEP;
        }

        newPullBetParams.setStartTime(startTime);
        newPullBetParams.setEndTime(endTime);
        newPullBetParams.setPullType(params.getPullType());
        log.info("sexy-下一次拉单参数 : "+newPullBetParams);
        return newPullBetParams;
    }


    @Override
    protected VenuePullBetParams initPullParams(String startTime) {

        JdbVenuePullBetParams venuePullBetParams = new JdbVenuePullBetParams();
        long startTimeLong = Long.parseLong(startTime);
        venuePullBetParams.setStartTime(startTimeLong);
        venuePullBetParams.setEndTime(startTimeLong + (20 * 60 * 1000));
        venuePullBetParams.setPullType(false);
        return venuePullBetParams;
    }
}

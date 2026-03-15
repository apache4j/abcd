/**
 * @(#)ShGamePullBetTask.java, 9月 18, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.play.task.pulltask.jdb;

import com.alibaba.fastjson.JSON;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.jdb.JDBGameApiImplExtend;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import com.cloud.baowang.play.task.pulltask.jdb.vo.JdbVenuePullBetParams;
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
@Component(ThirdGamePullBetTaskTypeConstant.JDB_GAME_PULL_BET_TASK)
@AllArgsConstructor
public class JdbGamePullBetTask extends BasePullBetTask {

    private final JDBGameApiImplExtend jdbService;

    /**
     * 仅提供 3 分钟前 ~ 2 小时内的资料
     */
    public static final long DEFAULT_STEP = 3 * 60 * 1000L;

    public static  final long MIN_STEP = 60 * 1000;


    @Override
    protected VenuePullBetParams pullBetRecord(VenueInfoVO venueDetailVO, String pullParamJson) {
        JdbVenuePullBetParams venuePullBetParams = JSON.parseObject(pullParamJson, JdbVenuePullBetParams.class);
        boolean pullType = venuePullBetParams.getPullType();
        log.info("jdb电子拉单开始: {}, 类型: {}", pullParamJson, pullType ? "自动" : "手动");

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
            ResponseVO<?> responseVO = jdbService.getBetRecordList(venueDetailVO, venuePullParamVO);
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
                log.error("JDB拉单-线程中断: ", e);
                break;
            }
        }
        return venuePullBetParams;
    }


    @Override
    protected String getVenuePlatform() {
        return VenuePlatformConstants.JDB;
    }

    @Override
    protected VenuePullBetParams initPullParams() {
        long nowStart = System.currentTimeMillis()-DEFAULT_STEP;
        long startTime = nowStart - (10 * 60 * 1000);

        JdbVenuePullBetParams venuePullBetParams = new JdbVenuePullBetParams();
        venuePullBetParams.setStartTime(startTime);
        venuePullBetParams.setEndTime(nowStart);
        venuePullBetParams.setPullType(true);
        log.info("jdb拉单-auto : "+venuePullBetParams);
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
        if (endTime - startTime < MIN_STEP) {
            endTime = startTime + MIN_STEP;
        }

        newPullBetParams.setStartTime(startTime);
        newPullBetParams.setEndTime(endTime);
        newPullBetParams.setPullType(params.getPullType());
        return newPullBetParams;
    }


    @Override
    protected VenuePullBetParams initPullParams(String startTime) {

        JdbVenuePullBetParams venuePullBetParams = new JdbVenuePullBetParams();
        long startTimeLong = Long.parseLong(startTime);
        venuePullBetParams.setStartTime(startTimeLong);
        venuePullBetParams.setEndTime(startTimeLong + (5 * 60 * 1000));
        venuePullBetParams.setPullType(false);
        return venuePullBetParams;
    }

}

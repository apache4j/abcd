package com.cloud.baowang.play.task.pulltask.nextSpin.task;

import com.alibaba.fastjson.JSON;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.cq9.constant.CQ9Constant;
import com.cloud.baowang.play.game.nextSpin.constant.NextSpinConstant;
import com.cloud.baowang.play.game.nextSpin.impl.NextSpinServiceImpl;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import com.cloud.baowang.play.task.pulltask.nextSpin.params.NextSpinPullBetParams;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;


/**
 * NEXTSPIN拉取注单任务
 *
 * @author: mufan
 * @creat: 2025/7/8 17:43
 */
@Slf4j
@Component(ThirdGamePullBetTaskTypeConstant.NEXTSPIN_GAME_PULL_BET_TASK)
public class NextSpinPullBetTask extends BasePullBetTask {
    public static final ZoneId gmt8ZoneId = ZoneId.of("GMT+8");
    public static final TimeZone gmt8TimeZone = TimeZone.getTimeZone("GMT+8");
    /**
     * 时区：GMT+8 (东8区)
     */
    public static final TimeZone shanghaiTimeZone = TimeZone.getTimeZone("Asia/Shanghai");

    @Autowired
    private NextSpinServiceImpl nextSpinServiceImpl;


    /**
     * 执行拉单记录
     *
     * @param venueInfoVO
     * @param pullParamJson
     * @return
     */
    @Override
    protected VenuePullBetParams pullBetRecord(VenueInfoVO venueInfoVO, String pullParamJson) {
        log.info("NextSpin拉单服务开始pullBetRecord, 场馆信息: {} 入参 : {}", venueInfoVO, pullParamJson);
        NextSpinPullBetParams venuePullBetParams = JSON.parseObject(pullParamJson, NextSpinPullBetParams.class);
        ResponseVO<NextSpinPullBetParams> responseVO = nextSpinServiceImpl.getBetRecordListByParams(venueInfoVO, venuePullBetParams);
        if (responseVO.isOk()) {
            log.info("NextSpin拉单服务结束pullBetRecord, 返回信息: {}", responseVO);
            return responseVO.getData();
        }
        return null;
    }

    @Override
    protected String getVenuePlatform() {
        return VenuePlatformConstants.NEXTSPIN;
    }

    @Override
    protected VenuePullBetParams initPullParams() {
        NextSpinPullBetParams params = new NextSpinPullBetParams();
        Date canadaCurrentDate = TimeZoneUtils.getCurrentDate4ZoneId(gmt8ZoneId);
        params.setStarttime(TimeZoneUtils.formatDate4TimeZone(canadaCurrentDate, gmt8TimeZone, DateUtils.yyyyMMddTHHmmss));
        params.setTimeInterval(CQ9Constant.DEFAULT_TIME_INTERVAL);
        // 按时间间隔计算结束时间
        //查詢日期開始時間，格式為RFC3339 如 20170601T000000
        //※時區請用UTC+8
        Date startAt = TimeZoneUtils.parseDate4TimeZoneCQ9(params.getStarttime(), gmt8TimeZone, DateUtils.yyyyMMddTHHmmss);
        //查詢日期結束時間，格式為RFC3339 如 20170602T000000
        //※時區請用UTC+8
        Date endAt = new DateTime(startAt).plusMillis(params.getTimeInterval()).toDate();
        params.setEndtime(TimeZoneUtils.formatDate4TimeZone(endAt, gmt8TimeZone, DateUtils.yyyyMMddTHHmmss));
        return params;
    }

    /**
     * 生成下次拉单参数
     *
     * @param currPullBetParams 当前拉单参数
     * @return
     */
    @Override
    protected VenuePullBetParams genNextPullParams(VenuePullBetParams currPullBetParams) {
        return null;
    }

    /**
     * 传入的是时间戳，转化为 utc+8的时间格式
     * 查詢日期開始時間，格式為RFC3339
     * 第一次使用开始时间，后面使用结束时间
     * 返回 就是这次开始与结束是的拉单参数，时间格式：格式為RFC3339
     *
     * @param startTime 开始时间 时间戳
     * @return
     */
    @Override
    protected VenuePullBetParams initPullParams(String startTime) {
        // 时间转换
        NextSpinPullBetParams params = new NextSpinPullBetParams();
        String tarTime = TimeZoneUtils.formatLocalDateTime(TimeZoneUtils.timeByTimeZone(Long.parseLong(startTime),"UTC+8"), DateUtils.yyyyMMddTHHmmss);
        // 设置开启与截止时间
        params.setStarttime(tarTime);
        params.setEndtime(tarTime);
        params.setTimeInterval(NextSpinConstant.DEFAULT_TIME_INTERVAL);

        // 获取下次拉单参数
        NextSpinPullBetParams nextSpinPullBetParams = nextSpinServiceImpl.nextPullBetParams(params);
        return nextSpinPullBetParams;
    }
}

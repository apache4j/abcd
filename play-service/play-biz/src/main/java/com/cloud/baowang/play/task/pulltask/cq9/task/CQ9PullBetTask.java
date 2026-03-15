package com.cloud.baowang.play.task.pulltask.cq9.task;

import com.alibaba.fastjson.JSON;

import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.cq9.constant.CQ9Constant;
import com.cloud.baowang.play.game.cq9.impl.CQ9ApiServiceImpl;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import com.cloud.baowang.play.task.pulltask.cq9.params.CQ9PullBetParams;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

import com.cloud.baowang.play.api.enums.venue.VenueEnum;


/**
 * CQ9拉取注单任务
 *
 * @author: lavine
 * @creat: 2023/9/13 17:43
 */
@Component(ThirdGamePullBetTaskTypeConstant.CQ9_GAME_PULL_BET_TASK)
public class CQ9PullBetTask extends BasePullBetTask {

    // 时区：GMT-4
//    public static final ZoneId canadaZoneId = ZoneId.of("Canada/Eastern");
    public static final ZoneId gmt4ZoneId = ZoneId.of("UTC-4");

    public static final TimeZone gmt4TimeZone = TimeZone.getTimeZone("UTC-4");

    public static final String patten_yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";

    // 时区：GMT+8 (东8区)
    //public static final TimeZone shanghaiTimeZone = TimeZone.getTimeZone("Asia/Shanghai");


    @Autowired
    private CQ9ApiServiceImpl apiService;


    /**
     * 执行拉单记录
     *
     * @param venueInfoVO
     * @param pullParamJson
     * @return
     */
    @Override
    protected VenuePullBetParams pullBetRecord(VenueInfoVO venueInfoVO, String pullParamJson) {
        CQ9PullBetParams venuePullBetParams = JSON.parseObject(pullParamJson, CQ9PullBetParams.class);

        ResponseVO<CQ9PullBetParams> responseVO = apiService.getBetRecordListByParams(venueInfoVO, venuePullBetParams);
        if (responseVO.isOk()) {
            return responseVO.getData();
        }
        return null;
    }

    @Override
    protected String getVenuePlatform() {
        return VenuePlatformConstants.CQ9;
    }

    @Override
    protected VenuePullBetParams initPullParams() {
        CQ9PullBetParams params = new CQ9PullBetParams();

        Date canadaCurrentDate = TimeZoneUtils.getCurrentDate4ZoneId(gmt4ZoneId);

        params.setStarttime(TimeZoneUtils.formatDate4TimeZone(canadaCurrentDate, gmt4TimeZone, DateUtils.DATE_FORMAT_1) + "T00:00:00.000-04:00");
        params.setPage(1);
        params.setPagesize(CQ9Constant.MAX_PAGE_SIZE);
        params.setTimeInterval(CQ9Constant.DEFAULT_TIME_INTERVAL);

        // 按时间间隔计算结束时间
        //查詢日期開始時間，格式為RFC3339 如 2017-06-01T00:00:00-04:00
        //※時區請用UTC-4
        Date startAt = TimeZoneUtils.parseDate4TimeZoneCQ9(params.getStarttime(), gmt4TimeZone, DateUtils.PATTEN_EASTERN_TIME);
        //查詢日期結束時間，格式為RFC3339 如 2017-06-02T00:00:00-04:00
        //※時區請用UTC-4
        Date endAt = new DateTime(startAt).plusMillis(params.getTimeInterval()).toDate();
        params.setEndtime(TimeZoneUtils.formatDate4TimeZone(endAt, gmt4TimeZone, DateUtils.PATTEN_EASTERN_TIME));

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
     * 传入的是时间戳，转化为 utc-4的时间格式
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
        CQ9PullBetParams params = new CQ9PullBetParams();
        String tarTime = TimeZoneUtils.formatLocalDateTime(TimeZoneUtils.timeByTimeZone(Long.parseLong(startTime), TimeZoneUtils.cq9TimeZone), DateUtils.PATTEN_EASTERN_TIME);
        // 设置开启与截止时间
        params.setStarttime(tarTime);
        params.setEndtime(tarTime);
        //params.setEndtime(TimeZoneUtils.formatDate4TimeZone(gmt4Date, gmt4TimeZone, DateUtils.PATTEN_EASTERN_TIME));
        params.setPage(1);
        params.setPagesize(CQ9Constant.MAX_PAGE_SIZE);
        params.setTimeInterval(CQ9Constant.DEFAULT_TIME_INTERVAL);

        // 获取下次拉单参数
        CQ9PullBetParams cq9PullBetParams = apiService.nextPullBetParams(params);
        // 本次结束时间
        String currentPullEndTime = TimeZoneUtils.convertTimeZone2Str(cq9PullBetParams.getEndtime(), DateUtils.PATTEN_EASTERN_TIME,
                gmt4TimeZone, gmt4TimeZone);
        Date date = TimeZoneUtils.parseDate4TimeZone(currentPullEndTime, DateUtils.PATTEN_EASTERN_TIME, gmt4TimeZone);

        SimpleDateFormat timeDateFormat = new SimpleDateFormat(patten_yyyyMMddHHmmss);
        timeDateFormat.setTimeZone(gmt4TimeZone);
        String newDateStr = timeDateFormat.format(date);
        // 转化为时间戳
        cq9PullBetParams.setManualCurrentPullEndTime(newDateStr);

        return cq9PullBetParams;
    }
}

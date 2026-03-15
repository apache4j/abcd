package com.cloud.baowang.play.task.pulltask.im.task;

import com.alibaba.fastjson.JSON;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.im.impl.marbles.MarblesGameService;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import com.cloud.baowang.play.task.pulltask.im.param.MarblesVenuePullBetParams;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Log4j2
@AllArgsConstructor
@Component(ThirdGamePullBetTaskTypeConstant.MARBLES_GAME_PULL_BET_TASK)
public class MarblesPlusBasePullBetTask extends BasePullBetTask {


    private final MarblesGameService marblesGameService;

    /**
     * 拉单最大间隔 30 min
     */
    private final static Long DEFAULT_STEP = 10 * 60 * 1000L;

    @Override
    protected VenuePullBetParams pullBetRecord(VenueInfoVO venueInfoVO, String pullParamJson) {
        MarblesVenuePullBetParams pullBetParams = JSON.parseObject(pullParamJson, MarblesVenuePullBetParams.class);
        log.info("神话游戏开始拉取时间段 时间start：{}，end：{}",
                DateUtils.convertDateToString(new Date(pullBetParams.getStartTime())),
                DateUtils.convertDateToString(new Date(pullBetParams.getEndTime())));

        log.info("im弹珠场馆信息：{}", JSON.toJSONString(venueInfoVO));
        VenuePullParamVO venuePullParamVO = new VenuePullParamVO();
        venuePullParamVO.setStartTime(pullBetParams.getStartTime());
        venuePullParamVO.setEndTime(pullBetParams.getEndTime());
        List<String> currency= venueInfoVO.getPullCurrencyCodeList();
        ResponseVO<?> responseVO =null;
        currency.forEach(c -> {
            venueInfoVO.setCurrencyCodeList(Arrays.asList(c));
            venueInfoVO.setPullCurrencyCodeList(Arrays.asList(c));
            marblesGameService.getBetRecordList(venueInfoVO, venuePullParamVO);
        });
        log.info("神话游戏更新拉取时间段 时间start：{}，end：{}",DateUtils.convertDateToString(new Date(pullBetParams.getStartTime()))
                ,DateUtils.convertDateToString(new Date(pullBetParams.getEndTime())));
        return genNextPullParams(pullBetParams);
    }

    @Override
    protected String getVenuePlatform() {
        return VenuePlatformConstants.MARBLES;
    }

    @Override
    protected VenuePullBetParams initPullParams() {
        MarblesVenuePullBetParams venuePullBetParams = new MarblesVenuePullBetParams();
        venuePullBetParams.setStartTime(System.currentTimeMillis());
        venuePullBetParams.setStep(DEFAULT_STEP);
        venuePullBetParams.setEndTime(venuePullBetParams.getStartTime()+venuePullBetParams.getStep());
        return venuePullBetParams;
    }


    @Override
    protected VenuePullBetParams genNextPullParams(VenuePullBetParams currPullBetParams) {
        MarblesVenuePullBetParams params = (MarblesVenuePullBetParams) currPullBetParams;
        MarblesVenuePullBetParams newPullBetParams = new MarblesVenuePullBetParams();
        newPullBetParams.setStartTime(params.getStartTime());
        newPullBetParams.setEndTime(params.getEndTime());

        long timeInterval = params.getStep() == null ? DEFAULT_STEP : params.getStep();
        newPullBetParams.setStep(timeInterval);

        newPullBetParams.setStartTime(newPullBetParams.getEndTime());

        newPullBetParams.setEndTime(newPullBetParams.getEndTime() + newPullBetParams.getStep());

        if (newPullBetParams.getEndTime() > System.currentTimeMillis()) {
            long pullTime = System.currentTimeMillis();
            newPullBetParams.setStartTime(pullTime - newPullBetParams.getStep());
            newPullBetParams.setEndTime(pullTime);
        }

        log.info("{}下一次拉单参数: {} | {}", VenueEnum.MARBLES.getVenueName(), newPullBetParams.getStartTime(),
                newPullBetParams.getEndTime() );
        return newPullBetParams;
    }

    @Override
    protected VenuePullBetParams initPullParams(String startTime) {
        // 时间转换
        MarblesVenuePullBetParams venuePullBetParams = new MarblesVenuePullBetParams();
        Long startTimeLong = Long.valueOf(startTime);
        venuePullBetParams.setStartTime(startTimeLong);
        venuePullBetParams.setStep(DEFAULT_STEP);
        venuePullBetParams.setEndTime(startTimeLong);
        // 获取下次拉单参数
        MarblesVenuePullBetParams marblesVenuePullBetParams = (MarblesVenuePullBetParams)genNextPullParams(venuePullBetParams);
        // 本次结束时间
        marblesVenuePullBetParams.setManualCurrentPullEndTime(marblesVenuePullBetParams.getEndTime().toString());
        return marblesVenuePullBetParams;
    }
}

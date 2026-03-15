package com.cloud.baowang.play.task.pulltask.omg.task;

import com.alibaba.fastjson.JSON;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.omg.OmgGameService;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import com.cloud.baowang.play.task.pulltask.omg.constant.OmgConstant;
import com.cloud.baowang.play.task.pulltask.omg.param.OmgVenuePullBetParams;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;


@Log4j2
@Component
public class OmgBasePullBetTask extends BasePullBetTask {
    @Autowired
    protected OmgGameService omgGameService;
    @Override
    protected VenuePullBetParams pullBetRecord(VenueInfoVO venueDetailVO, String pullParamJson) {
        OmgVenuePullBetParams venuePullBetParams = JSON.parseObject(pullParamJson, OmgVenuePullBetParams.class);
        log.info("{}开始拉取时间段 时间start：{}，end：{}",
                venueDetailVO.getVenueCode(),
                DateUtils.convertDateToString(new Date(venuePullBetParams.getStartTime())),
                DateUtils.convertDateToString(new Date(venuePullBetParams.getEndTime())));
        VenuePullParamVO venuePullParamVO = new VenuePullParamVO();
        venuePullParamVO.setStartTime(venuePullBetParams.getStartTime());
        venuePullParamVO.setEndTime(venuePullBetParams.getEndTime());
        ResponseVO<?> responseVO = omgGameService.getBetRecordList(venueDetailVO, venuePullParamVO);;
        if(responseVO.isOk()) {
            log.info("{}更新拉取时间段 时间start：{}，end：{}",venueDetailVO.getVenueCode() ,DateUtils.convertDateToString(new Date(venuePullBetParams.getStartTime()))
                    ,DateUtils.convertDateToString(new Date(venuePullBetParams.getEndTime())));
            return genNextPullParams(venuePullBetParams);
        }
        return null;
    }


    protected String getVenuePlatform() {
        return null;
    }


    @Override
    protected VenuePullBetParams initPullParams() {
        OmgVenuePullBetParams venuePullBetParams = new OmgVenuePullBetParams();
        venuePullBetParams.setStartTime(System.currentTimeMillis());
        venuePullBetParams.setStep(OmgConstant.DEFAULT_STEP.longValue());
        venuePullBetParams.setEndTime(venuePullBetParams.getStartTime()+venuePullBetParams.getStep());
        return venuePullBetParams;
    }

    @Override
    protected VenuePullBetParams genNextPullParams(VenuePullBetParams currPullBetParams) {
        OmgVenuePullBetParams params = (OmgVenuePullBetParams) currPullBetParams;
        OmgVenuePullBetParams newPullBetParams = new OmgVenuePullBetParams();
        newPullBetParams.setStartTime(params.getStartTime());
        newPullBetParams.setEndTime(params.getEndTime());

        long timeInterval = params.getStep() == null ? OmgConstant.DEFAULT_STEP : params.getStep();
        newPullBetParams.setStep(timeInterval);

        newPullBetParams.setStartTime(newPullBetParams.getEndTime() - newPullBetParams.getStep());

        newPullBetParams.setEndTime(newPullBetParams.getEndTime() + newPullBetParams.getStep());
        if (newPullBetParams.getEndTime() > System.currentTimeMillis()) {
            newPullBetParams.setStartTime(System.currentTimeMillis() - newPullBetParams.getStep());
            newPullBetParams.setEndTime(System.currentTimeMillis());
        }

        log.info("{},下一次拉单参数: {} | {}", getVenuePlatform() ,newPullBetParams.getStartTime(),
                newPullBetParams.getEndTime() );
        return newPullBetParams;
    }

    @Override
    protected VenuePullBetParams initPullParams(String startTime) {
        // 时间转换
        OmgVenuePullBetParams venuePullBetParams = new OmgVenuePullBetParams();
        long startTimeLong = Long.parseLong(startTime);
        venuePullBetParams.setStartTime(startTimeLong);
        venuePullBetParams.setEndTime(startTimeLong + OmgConstant.DEFAULT_STEP.longValue());
        // 获取下次拉单参数
//        OmgVenuePullBetParams omgVenuePullBetParams = (OmgVenuePullBetParams)genNextPullParams(venuePullBetParams);
//        // 本次结束时间
//        omgVenuePullBetParams.setManualCurrentPullEndTime(omgVenuePullBetParams.getEndTime().toString());
//        return omgVenuePullBetParams;
        return venuePullBetParams;
    }
}

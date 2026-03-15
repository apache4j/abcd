/**
 * @(#)ShGamePullBetTask.java, 9月 18, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.play.task.pulltask.sb;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.sh.constant.SHConstantApi;
import com.cloud.baowang.play.game.shaba.SBAGameServiceImpl;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import com.cloud.baowang.play.task.pulltask.sh.params.ShVenuePullBetParams;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * <h2></h2>
 *
 * @author sheldon
 * date 2023/9/18
 */
@Log4j2
@Component(ThirdGamePullBetTaskTypeConstant.SBA_GAME_PULL_BET_TASK)
@AllArgsConstructor
public class SBGamePullBetTask extends BasePullBetTask {

    private final SBAGameServiceImpl shGameService;

    @Override
    protected VenuePullBetParams pullBetRecord(VenueInfoVO venueDetailVO, String pullParamJson) {
        log.info("{},开始拉取,偏移量:", venueDetailVO.getVenueCode());
        ShVenuePullBetParams shVenuePullBetParams = JSON.parseObject(pullParamJson, ShVenuePullBetParams.class);
        if (ObjectUtil.isNotEmpty(shVenuePullBetParams.getStartTime())
                && System.currentTimeMillis() < shVenuePullBetParams.getStartTime()) {
            log.info("{},开始拉取,还未到下次开始时间:不进行拉单,当前时间:{},下次时间:{}", venueDetailVO.getVenueCode(),
                    DateUtils.convertDateToString(new Date(System.currentTimeMillis()), DateUtils.FULL_FORMAT_1)
                    , DateUtils.convertDateToString(new Date(shVenuePullBetParams.getStartTime()), DateUtils.FULL_FORMAT_1));
            return shVenuePullBetParams;
        }

        VenuePullParamVO venuePullParamVO = JSON.parseObject(pullParamJson, VenuePullParamVO.class);
        ResponseVO<ShVenuePullBetParams> responseVO = shGameService.getBetRecordList(venueDetailVO, venuePullParamVO);
        if (responseVO.isOk()) {
            return responseVO.getData();
        }
        return shVenuePullBetParams;
    }


    @Override
    protected String getVenuePlatform() {
        return VenuePlatformConstants.SBA;
    }

    @Override
    protected VenuePullBetParams initPullParams() {
        ShVenuePullBetParams venuePullBetParams = new ShVenuePullBetParams();
        venuePullBetParams.setStartTime(System.currentTimeMillis());
        venuePullBetParams.setStep(SHConstantApi.DEFAULT_STEP.longValue());
        venuePullBetParams.setEndTime(venuePullBetParams.getStartTime() + venuePullBetParams.getStep());
        venuePullBetParams.setVersionKey("0");
        return venuePullBetParams;
    }

    @Override
    protected VenuePullBetParams genNextPullParams(VenuePullBetParams currPullBetParams) {
        return currPullBetParams;
    }

    @Override
    protected VenuePullBetParams initPullParams(String startTime) {
        // 时间转换
        ShVenuePullBetParams venuePullBetParams = new ShVenuePullBetParams();
        Long startTimeLong = Long.valueOf(startTime);
        venuePullBetParams.setStartTime(startTimeLong);
        venuePullBetParams.setStep(SHConstantApi.DEFAULT_STEP.longValue());
        venuePullBetParams.setEndTime(startTimeLong);
        // 获取下次拉单参数
//        ShVenuePullBetParams shVenuePullBetParams = (ShVenuePullBetParams) genNextPullParams(venuePullBetParams);
//        // 本次结束时间
//        shVenuePullBetParams.setManualCurrentPullEndTime(shVenuePullBetParams.getEndTime().toString());
//        return shVenuePullBetParams;
        return venuePullBetParams;
    }

}

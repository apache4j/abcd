package com.cloud.baowang.play.task.pulltask.evo.task;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.evo.impl.EvoServiceImpl;
import com.cloud.baowang.play.game.evo.utils.EVOUtils;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import com.cloud.baowang.play.task.pulltask.evo.params.EvoVenuePullBetParams;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component(ThirdGamePullBetTaskTypeConstant.EVO_GAME_PULL_BET_TASK)
public class EvoPullBetTask extends BasePullBetTask {

    @Autowired
    private EvoServiceImpl evoService;

    @Override
    protected VenuePullBetParams pullBetRecord(VenueInfoVO venueDetailVO, String pullParamJson) {
        EvoVenuePullBetParams pullBetParams = JSON.parseObject(pullParamJson, EvoVenuePullBetParams.class);

        if (pullBetParams.getPullType()) {
            if (pullBetParams.getVersionKey() == null) {
                pullBetParams.setVersionKey(EVOUtils.timestampToUtcZero(System.currentTimeMillis()));
            }

            VenuePullParamVO venuePullParamVO = new VenuePullParamVO();
            venuePullParamVO.setVersionKey(pullBetParams.getVersionKey());
            venuePullParamVO.setPullType(pullBetParams.getPullType());
            ResponseVO<String> responseVO = evoService.getBetRecordList(venueDetailVO, venuePullParamVO);
            if (responseVO.isOk()) {
                pullBetParams.setVersionKey(responseVO.getData());
                return pullBetParams;
            }
        } else {
            // 手动拉单
            if (ObjectUtil.isNull(pullBetParams.getStartTime()) || ObjectUtil.isNull(pullBetParams.getEndTime())) {
                return null;
            }
            // String versionKey = EVOUtils.timestampToUtcZero(Long.valueOf(pullBetParams.getStartTime()));
            Long startTimeTemp = Long.valueOf(pullBetParams.getStartTime());
            Long endTimeTemp = Long.valueOf(pullBetParams.getEndTime());
            while (startTimeTemp <= endTimeTemp) {
                VenuePullParamVO venuePullParamVO = new VenuePullParamVO();
                String versionStart = EVOUtils.timestampToUtcZero(startTimeTemp);
                venuePullParamVO.setVersionKey(versionStart);
                venuePullParamVO.setStartTime(startTimeTemp);
                venuePullParamVO.setEndTime(endTimeTemp);
                venuePullParamVO.setPullType(pullBetParams.getPullType());
                ResponseVO<String> responseVO = evoService.getBetRecordList(venueDetailVO, venuePullParamVO);
                if (responseVO.isOk()) {
                    startTimeTemp = EVOUtils.utcToTimestamp(responseVO.getData());
                    pullBetParams.setVersionKey(responseVO.getData());
                } else {
                    return null;
                }
            }
            return pullBetParams;

        }

        return null;
    }

    @Override
    protected String getVenuePlatform() {
        return VenuePlatformConstants.EVO;
    }

    @Override
    protected VenuePullBetParams initPullParams() {
        EvoVenuePullBetParams pullBetParams = new EvoVenuePullBetParams();
        // 上线当前时间戳
        String versionKey = EVOUtils.timestampToUtcZero(System.currentTimeMillis());
        pullBetParams.setVersionKey(versionKey);
        return pullBetParams;
    }

    @Override
    protected VenuePullBetParams initPullParams(String startTime) {
        //向后十分钟
        EvoVenuePullBetParams pullBetParams = new EvoVenuePullBetParams();
        pullBetParams.setStartTime(startTime);
        // 向后推十分钟
        Long endTime = Long.valueOf(startTime) + 10 * 60 * 1000;
        pullBetParams.setEndTime(String.valueOf(endTime));
        return pullBetParams;
    }

    @Override
    protected VenuePullBetParams genNextPullParams(VenuePullBetParams currPullBetParams) {
        return null;
    }
}

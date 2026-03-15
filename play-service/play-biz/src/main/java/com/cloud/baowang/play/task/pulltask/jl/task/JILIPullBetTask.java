package com.cloud.baowang.play.task.pulltask.jl.task;


import com.alibaba.fastjson.JSON;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.zf.jili.impl.JILIGameThreeServiceImpl;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import com.cloud.baowang.play.task.pulltask.jl.params.JILIPullBetParams;
import com.cloud.baowang.play.task.pulltask.omg.constant.OmgConstant;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component(ThirdGamePullBetTaskTypeConstant.JILI_03_GAME_PULL_BET_TASK)
public class JILIPullBetTask extends BasePullBetTask {


    @Autowired
    private JILIGameThreeServiceImpl service;

    @Override
    protected VenuePullBetParams pullBetRecord(VenueInfoVO venueInfoVO, String pullParamJson) {
        log.info("拉单服务开始pullBetRecord, 场馆信息: {} 入参 : {}", venueInfoVO, pullParamJson);
        JILIPullBetParams venuePullBetParams = JSON.parseObject(pullParamJson, JILIPullBetParams.class);
        VenuePullParamVO venuePullParamVO = new VenuePullParamVO();
        venuePullParamVO.setStartTime(venuePullBetParams.getStartTime());
        venuePullParamVO.setEndTime(venuePullBetParams.getEndTime());
        ResponseVO<?> betRecordList = service.getBetRecordList(venueInfoVO, venuePullParamVO);
        if (betRecordList.isOk()){
            log.info("JILI拉单服务结束pullBetRecord, 返回信息: {}", betRecordList);
            return genNextPullParams(venuePullBetParams);
        }
        return null;
    }

    @Override
    protected String getVenuePlatform() {
        return VenuePlatformConstants.NEW_JILI;
    }

    @Override
    protected VenuePullBetParams initPullParams() {
        long l = System.currentTimeMillis() - 1000 * 60 * 10L;
        return initPullParams(String.valueOf(l));
    }

    @Override
    protected VenuePullBetParams genNextPullParams(VenuePullBetParams currPullBetParams) {
        JILIPullBetParams params = (JILIPullBetParams)currPullBetParams;
        JILIPullBetParams newParams = new JILIPullBetParams();
        Long endTime = params.getEndTime();
        newParams.setStartTime(endTime);
        newParams.setEndTime(newParams.getStartTime() + OmgConstant.DEFAULT_STEP.longValue());
        if (newParams.getEndTime() > System.currentTimeMillis()) {
            newParams.setStartTime(System.currentTimeMillis() -  OmgConstant.DEFAULT_STEP.longValue());
            newParams.setEndTime(System.currentTimeMillis());
        }
        return newParams;
    }

    @Override
    protected VenuePullBetParams initPullParams(String startTime) {
        JILIPullBetParams params = new JILIPullBetParams();
        params.setStartTime(Long.parseLong(startTime));
        params.setEndTime(params.getStartTime() + OmgConstant.DEFAULT_STEP.longValue());
        return params;
    }
}

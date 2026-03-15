package com.cloud.baowang.play.task.pulltask.pg.task;

import com.alibaba.fastjson.JSON;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.pg.impl.PgServiceImpl;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import com.cloud.baowang.play.task.pulltask.pg.params.PgVenuePullBetParams;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component(ThirdGamePullBetTaskTypeConstant.PG_GAME_PULL_BET_TASK)
public class PgPullBetTask extends BasePullBetTask {

    @Autowired
    private PgServiceImpl pgGameService;
    private final String INIT_VERSION_KEY = "1";

    @Override
    protected VenuePullBetParams pullBetRecord(VenueInfoVO venueDetailVO, String pullParamJson) {
        PgVenuePullBetParams pullBetParams = JSON.parseObject(pullParamJson, PgVenuePullBetParams.class);
        log.info("PGPullBetTask versionKey：{}", pullBetParams.getVersionKey().equals(INIT_VERSION_KEY) ? INIT_VERSION_KEY : DateUtils.convertDateToString(new Date(Long.valueOf(pullBetParams.getVersionKey()))));
        VenuePullParamVO venuePullParamVO = new VenuePullParamVO();
        venuePullParamVO.setVersionKey(pullBetParams.getVersionKey());
        ResponseVO<String> responseVO = pgGameService.getBetRecordList(venueDetailVO, venuePullParamVO);
        if(responseVO.isOk()) {
            pullBetParams.setVersionKey(responseVO.getData());
            return pullBetParams;
        }
        return null;
    }

    @Override
    protected String getVenuePlatform() {
        return VenueEnum.PG.getVenueCode();
    }

    @Override
    protected VenuePullBetParams initPullParams() {
        PgVenuePullBetParams pullBetParams = new PgVenuePullBetParams();
        pullBetParams.setVersionKey(INIT_VERSION_KEY);
        return pullBetParams;
    }

    @Override
    protected VenuePullBetParams initPullParams(String startTime) {
        return null;
    }

    @Override
    protected VenuePullBetParams genNextPullParams(VenuePullBetParams currPullBetParams) {
        return null;
    }
}

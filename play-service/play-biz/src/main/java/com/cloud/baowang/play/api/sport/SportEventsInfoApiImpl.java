package com.cloud.baowang.play.api.sport;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.api.venue.SportEventsInfoApi;
import com.cloud.baowang.play.api.vo.venue.SportEventsInfoRequestVO;
import com.cloud.baowang.play.api.vo.venue.SportEventsInfoSortRequestVO;
import com.cloud.baowang.play.api.vo.venue.SportEventsInfoVO;
import com.cloud.baowang.play.game.shaba.SBAGameServiceImpl;
import com.cloud.baowang.play.service.SportEventsInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@AllArgsConstructor
@Service
@Slf4j
public class SportEventsInfoApiImpl implements SportEventsInfoApi {

    private final SportEventsInfoService sportEventsInfoService;

    private final SBAGameServiceImpl sbaGameService;

    @Override
    public ResponseVO<Page<SportEventsInfoVO>> getSportEventsInfoPage(SportEventsInfoRequestVO requestVO) {
        return ResponseVO.success(sportEventsInfoService.getSportEventsInfoPage(requestVO));
    }

    @Override
    public ResponseVO<List<SportEventsInfoVO>> getSportEventsInfoSortList(SportEventsInfoRequestVO sportEventsInfoRequestVO) {
        return ResponseVO.success(sportEventsInfoService.getSportEventsInfoSortList(sportEventsInfoRequestVO));
    }

    @Override
    public ResponseVO<Boolean> setSportEventsPinEvents(String id) {
        return ResponseVO.success(sportEventsInfoService.setSportEventsPinEvents(id));
    }

    @Override
    public ResponseVO<Boolean> cancelSportEventsPinEvents(String id) {
        return ResponseVO.success(sportEventsInfoService.cancelSportEventsPinEvents(id));
    }

    @Override
    public ResponseVO<Boolean> setSortEvents(SportEventsInfoSortRequestVO requestVO) {
        return ResponseVO.success(sportEventsInfoService.setSortEvents(requestVO));
    }

    @Override
    public ResponseVO<Boolean> sysEventsInfo() {
        Boolean sysEventsInfo = RedisUtil.getValue(RedisConstants.SYS_EVENTS_INFO);
        if (sysEventsInfo != null) {
            throw new BaowangDefaultException(ResultCode.PLEASE_TRY_AGAIN_LATER);
        }
        sbaGameService.sbaPullEventInfo();
        RedisUtil.setValue(RedisConstants.SYS_EVENTS_INFO, Boolean.TRUE, 1L, TimeUnit.MINUTES);
        return ResponseVO.success(Boolean.TRUE);
    }

}

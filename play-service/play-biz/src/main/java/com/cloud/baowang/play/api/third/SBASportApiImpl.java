package com.cloud.baowang.play.api.third;

import com.cloud.baowang.play.api.api.third.SBASportApi;
import com.cloud.baowang.play.game.shaba.SBAGameServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class SBASportApiImpl implements SBASportApi {


    @Autowired
    private SBAGameServiceImpl sbaGameService;

    @Override
    public void sbaPullStatus(Integer orderStatus,String order) {
        sbaGameService.sbaPullStatus(orderStatus, order);
    }

    @Override
    public void sbaPullReachLimitTrans(String date) {
        sbaGameService.sbaPullReachLimitTrans(date);
    }


    @Override
    public void sbaPullGameEventsTask() {
        sbaGameService.sbaPullGameEventsTask();
    }

    @Override
    public void sbaPullEventInfo() {
        sbaGameService.sbaPullEventInfo();
    }
}

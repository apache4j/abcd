package com.cloud.baowang.play.api.third;

import com.cloud.baowang.play.api.api.third.DBFishingGameApi;
import com.cloud.baowang.play.api.vo.db.evg.vo.DBEVGBasicInfo;
import com.cloud.baowang.play.api.vo.db.rsp.evg.DBEVGBaseRsp;
import com.cloud.baowang.play.game.db.fishing.DBFishingServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
public class DBFishingGameApiImpl implements DBFishingGameApi {

    private final DBFishingServiceImpl dbFishingService;


    @Override
    public DBEVGBaseRsp queryBalance(DBEVGBasicInfo evgBasicInfo, String req) {
        return dbFishingService.queryBalance(evgBasicInfo, req);
    }

    @Override
    public DBEVGBaseRsp balanceChange(DBEVGBasicInfo evgBasicInfo, String req) {
        return dbFishingService.balanceChange(evgBasicInfo, req);
    }

    @Override
    public DBEVGBaseRsp queryOrderStatus(DBEVGBasicInfo evgBasicInfo, String req) {
        return dbFishingService.queryOrderStatus(evgBasicInfo, req);
    }
}

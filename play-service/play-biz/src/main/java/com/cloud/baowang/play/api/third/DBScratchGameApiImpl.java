package com.cloud.baowang.play.api.third;

import com.cloud.baowang.play.api.api.third.DBScratchGameApi;
import com.cloud.baowang.play.api.vo.db.evg.vo.DBEVGBasicInfo;
import com.cloud.baowang.play.api.vo.db.rsp.evg.DBEVGBaseRsp;
import com.cloud.baowang.play.game.db.evg.DBEVGServiceImpl;
import com.cloud.baowang.play.game.db.scratch.DBScratchLotteryServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
public class DBScratchGameApiImpl implements DBScratchGameApi {

    private final DBScratchLotteryServiceImpl dbScratchService;


    @Override
    public DBEVGBaseRsp queryBalance(DBEVGBasicInfo evgBasicInfo, String req) {
        return dbScratchService.queryBalance(evgBasicInfo, req);
    }

    @Override
    public DBEVGBaseRsp balanceChange(DBEVGBasicInfo evgBasicInfo, String req) {
        return dbScratchService.balanceChange(evgBasicInfo, req);
    }

    @Override
    public DBEVGBaseRsp queryOrderStatus(DBEVGBasicInfo evgBasicInfo, String req) {
        return dbScratchService.queryOrderStatus(evgBasicInfo, req);
    }
}

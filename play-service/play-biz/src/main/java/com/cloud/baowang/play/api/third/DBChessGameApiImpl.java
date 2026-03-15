package com.cloud.baowang.play.api.third;

import com.cloud.baowang.play.api.api.third.DBChessGameApi;
import com.cloud.baowang.play.api.api.third.DBEVGGameApi;
import com.cloud.baowang.play.api.vo.db.evg.vo.DBEVGBasicInfo;
import com.cloud.baowang.play.api.vo.db.rsp.evg.DBEVGBaseRsp;
import com.cloud.baowang.play.game.db.chess.DBChessServiceImpl;
import com.cloud.baowang.play.game.db.evg.DBEVGServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
public class DBChessGameApiImpl implements DBChessGameApi {

    private final DBChessServiceImpl dbChessService;


    @Override
    public DBEVGBaseRsp queryBalance(DBEVGBasicInfo evgBasicInfo, String req) {
        return dbChessService.queryBalance(evgBasicInfo, req);
    }

    @Override
    public DBEVGBaseRsp balanceChange(DBEVGBasicInfo evgBasicInfo, String req) {
        return dbChessService.balanceChange(evgBasicInfo, req);
    }

    @Override
    public DBEVGBaseRsp queryOrderStatus(DBEVGBasicInfo evgBasicInfo, String req) {
        return dbChessService.queryOrderStatus(evgBasicInfo, req);
    }
}

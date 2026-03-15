package com.cloud.baowang.play.api.third;

import com.cloud.baowang.play.api.api.third.DBChessGameApi;
import com.cloud.baowang.play.api.api.third.DBSHGameApi;
import com.cloud.baowang.play.api.vo.db.evg.vo.DBEVGBasicInfo;
import com.cloud.baowang.play.api.vo.db.rsp.evg.DBEVGBaseRsp;
import com.cloud.baowang.play.api.vo.db.rsp.sh.DBSHBaseRsp;
import com.cloud.baowang.play.api.vo.db.sh.vo.*;
import com.cloud.baowang.play.game.db.chess.DBChessServiceImpl;
import com.cloud.baowang.play.game.db.sh.DBSHServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@Slf4j
@RestController
public class DBSHGameApiImpl implements DBSHGameApi {

    private final DBSHServiceImpl dbshService;


    @Override
    public DBSHBaseRsp<SHOrderRspData> getBalance(SHBalanceQueryVO reqVO) {
        return dbshService.getBalance(reqVO);
    }

    @Override
    public DBSHBaseRsp<List<SHOrderRspData>> getBatchBalance(SHBalanceQueryBatchVO reqVO) {
        return dbshService.getBatchBalance(reqVO);
    }

    @Override
    public DBSHBaseRsp<BetRspParams> betConfirm(BetRequestVO reqVO) {
        return dbshService.betConfirm(reqVO);
    }

    @Override
    public DBSHBaseRsp<BetRspParams> betCancel(DBSHRequestVO reqVO) {
        return dbshService.betCancel(reqVO);
    }

    @Override
    public DBSHBaseRsp<BetRspParams> gamePayout(DBSHRequestVO reqVO) {
        return dbshService.gamePayout(reqVO);
    }

    @Override
    public DBSHBaseRsp<BetRspParams> activityPayout(DBSHRequestVO reqVO) {
        return dbshService.activityPayout(reqVO);
    }

    @Override
    public DBSHBaseRsp<BetRspParams> playerbetting(DBSHRequestVO reqVO) {
        return dbshService.playerbetting(reqVO);
    }

    @Override
    public DBSHBaseRsp<BetRspParams> activityRebate(DBSHRequestVO reqVO) {
        return dbshService.activityRebate(reqVO);
    }
}

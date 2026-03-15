package com.cloud.baowang.play.api.third;

import com.cloud.baowang.play.api.api.third.DBAceltGameApi;
import com.cloud.baowang.play.api.api.third.PgGameApi;
import com.cloud.baowang.play.api.vo.db.acelt.vo.BalanceQueryVO;
import com.cloud.baowang.play.api.vo.db.acelt.vo.TransferCheckVO;
import com.cloud.baowang.play.api.vo.db.acelt.vo.TransferRequestVO;
import com.cloud.baowang.play.api.vo.db.acelt.vo.TransferRspData;
import com.cloud.baowang.play.api.vo.db.rsp.acelt.DBAceltBaseRsp;
import com.cloud.baowang.play.api.vo.pg.req.PgAdjustmentReq;
import com.cloud.baowang.play.api.vo.pg.req.PgBaseReq;
import com.cloud.baowang.play.api.vo.pg.req.PgBetReq;
import com.cloud.baowang.play.api.vo.pg.req.VerifySessionReq;
import com.cloud.baowang.play.api.vo.pg.rsp.*;
import com.cloud.baowang.play.game.db.acelt.DBAceltServiceImpl;
import com.cloud.baowang.play.game.pg.impl.PgServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
public class PgGameApiImpl implements PgGameApi {

    private final PgServiceImpl pgService;


    @Override
    public PGBaseRes<VerifySessionRes> sendVerification(VerifySessionReq request) {
        return pgService.sendVerification(request);
    }

    @Override
    public PGBaseRes<PgBalanceRes> queryBalance(PgBaseReq request) {
        return pgService.queryBalance(request);
    }

    @Override
    public PGBaseRes<PgAmountBetRes> processBet(PgBetReq request) {
        return pgService.processBet(request);
    }

    @Override
    public PGBaseRes<PgAdjustAmountRes> processAdjustBet(PgAdjustmentReq request) {
        return pgService.processAdjustBet(request);
    }
}

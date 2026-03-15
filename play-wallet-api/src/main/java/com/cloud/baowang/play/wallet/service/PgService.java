package com.cloud.baowang.play.wallet.service;

import com.cloud.baowang.play.wallet.vo.req.pg.*;
import com.cloud.baowang.play.wallet.vo.res.pg.*;

public interface PgService {
    PGBaseRes<VerifySessionRes> sendVerification(VerifySessionReq request);

    PGBaseRes<PgBalanceRes> queryBalance(PgBaseReq request);

    PGBaseRes<PgAmountBetRes> processBet(PgBetReq request);

    PGBaseRes<PgAdjustAmountRes> processAdjustBet(PgAdjustmentReq request);
}

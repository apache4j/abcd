package com.cloud.baowang.play.api.api.third;

import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.pg.req.PgAdjustmentReq;
import com.cloud.baowang.play.api.vo.pg.req.PgBaseReq;
import com.cloud.baowang.play.api.vo.pg.req.PgBetReq;
import com.cloud.baowang.play.api.vo.pg.req.VerifySessionReq;
import com.cloud.baowang.play.api.vo.pg.rsp.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(contextId = "pg-api", value = ApiConstants.NAME)
@Tag(name = "pg电子")
public interface PgGameApi {

    String PREFIX = ApiConstants.PREFIX + "pg/api/";

    @Operation(summary = "获取余额")
    @PostMapping(PREFIX + "sendVerification")
    PGBaseRes<VerifySessionRes> sendVerification(VerifySessionReq request);


    @Operation(summary = "获取余额")
    @PostMapping(PREFIX + "queryBalance")
    PGBaseRes<PgBalanceRes> queryBalance(PgBaseReq request);


    @Operation(summary = "获取余额")
    @PostMapping(PREFIX + "processBet")
    PGBaseRes<PgAmountBetRes> processBet(PgBetReq request);


    @Operation(summary = "获取余额")
    @PostMapping(PREFIX + "processAdjustBet")
    PGBaseRes<PgAdjustAmountRes> processAdjustBet(PgAdjustmentReq request);
}

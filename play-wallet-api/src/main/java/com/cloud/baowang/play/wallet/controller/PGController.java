package com.cloud.baowang.play.wallet.controller;

import com.alibaba.fastjson.JSON;
import com.cloud.baowang.play.api.api.third.PgGameApi;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.vo.pg.req.PgAdjustmentReq;
import com.cloud.baowang.play.api.vo.pg.req.PgBaseReq;
import com.cloud.baowang.play.api.vo.pg.req.PgBetReq;
import com.cloud.baowang.play.api.vo.pg.req.VerifySessionReq;
import com.cloud.baowang.play.api.vo.pg.rsp.*;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@LogExecution
@Slf4j
@RestController
@RequestMapping("callback/pg")
@Tag(name = "PG单一钱包")
public class PGController {

    @Resource
    private PgGameApi pgGameApi;

    @Operation(summary = "游戏登入后令牌验证")
    @PostMapping("/VerifySession")
    public PGBaseRes<VerifySessionRes> sendVerification(VerifySessionReq request) {
        log.info("收到{}-游戏登入后令牌验证 消息:{}", VenueEnum.PG.getVenueName(), JSON.toJSONString(request));
        PGBaseRes<VerifySessionRes> res = pgGameApi.sendVerification(request);
        log.info("收到PG-用户查询余额 ,返回 消息:{}",   JSON.toJSONString(res));
        return res;
    }

    @Operation(summary = "用户查询余额")
    @PostMapping("/Cash/Get")
    public PGBaseRes<PgBalanceRes> queryBalance(@RequestParam("trace_id") String traceId, PgBaseReq request) {
        log.info("收到{}-用户查询余额 消息:{}", VenueEnum.PG.getVenueName(), JSON.toJSONString(request));
        PGBaseRes<PgBalanceRes> res = pgGameApi.queryBalance(request);
        log.info("收到PG-用户查询余额 ,返回 消息:{}",   JSON.toJSONString(res));
        return res;
    }

    @Operation(summary = "余额加扣款(10秒内作出响应，否则API将超时)")
    @PostMapping("/Cash/TransferInOut")
    public PGBaseRes<PgAmountBetRes> processBet(@RequestParam("trace_id") String traceId, PgBetReq request) {
        request.setTrace_id(traceId);
        log.info("收到PG-余额加扣款 消息:{}", JSON.toJSONString(request));
        PGBaseRes<PgAmountBetRes> res = pgGameApi.processBet(request);
        log.info("收到PG-余额加扣款 ,返回 消息:{}",   JSON.toJSONString(res));
        return res;
    }

    @Operation(summary = "余额调整-特定活动使用")
    @PostMapping("/Cash/Adjustment")
    public PGBaseRes<PgAdjustAmountRes> processAdjustment(PgAdjustmentReq request) {
        log.info("收到{}-余额调整-特定活动使用 消息:{}", VenueEnum.PG.getVenueName(), JSON.toJSONString(request));
        PGBaseRes<PgAdjustAmountRes> res = pgGameApi.processAdjustBet(request);
        log.info("收到PG-余额调整-特定活动使用 ,返回 消息:{}",   JSON.toJSONString(res));
        return res;
    }

}

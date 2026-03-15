package com.cloud.baowang.play.wallet.controller;

import com.alibaba.fastjson.JSON;
import com.cloud.baowang.play.api.api.third.EVOGameApi;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.vo.evo.*;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import com.cloud.baowang.play.wallet.vo.req.pg.PgAdjustmentReq;
import com.cloud.baowang.play.wallet.vo.res.pg.PGBaseRes;
import com.cloud.baowang.play.wallet.vo.res.pg.PgAdjustAmountRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@LogExecution
@Slf4j
@RestController
@RequestMapping("callback/evo")
@Tag(name = "Evo单一钱包")
public class EvolutionController {

//    @Resource
//    private EvoService evoService;

    @Resource
    private EVOGameApi evoGameApi;

    @Operation(summary = "校验用户信息")
    @PostMapping("/check")
    public CheckUserResponse check(@RequestParam("authToken") String authToken, @RequestBody CheckUserRequest request) {
        request.setAuthToken(authToken);
        log.info("收到evo-校验用户信息 消息:{}", JSON.toJSONString(request));
        CheckUserResponse res = evoGameApi.check(request);
        log.info("收到Evo-校验用户信息 ,返回 消息:{}", JSON.toJSONString(res));
        return res;
    }

    @Operation(summary = "生成用户的 sid")
    @PostMapping("/sid")
    public CheckUserResponse sid(@RequestParam("authToken") String authToken, @RequestBody CheckUserRequest request) {
        request.setAuthToken(authToken);
        log.info("生成用户的 sid 消息:{}", JSON.toJSONString(request));
        CheckUserResponse res = evoGameApi.sid(request);
        log.info("收到Evo-生成用户的 sid 返回消息,:{}", JSON.toJSONString(res));
        return res;
    }

    @Operation(summary = "用户查询余额")
    @PostMapping("/balance")
    public BalanceResponse balance(@RequestParam("authToken") String authToken, @RequestBody BalanceRequest request) {
        request.setAuthToken(authToken);
        log.info("收到{}-用户查询余额 消息:{}", VenueEnum.EVO.getVenueName(), JSON.toJSONString(request));
        BalanceResponse res = evoGameApi.balance(request);
        log.info("收到Evo-用户查询余额 ,返回 消息:{}", JSON.toJSONString(res));
        return res;
    }

    @Operation(summary = "投注")
    @PostMapping("/debit")
    public BalanceResponse debit(@RequestParam("authToken") String authToken, @RequestBody DebitRequest request) {
        request.setAuthToken(authToken);
        log.info("收到Evo-余额投注 消息:{}", JSON.toJSONString(request));
        BalanceResponse res = evoGameApi.debit(request);
        log.info("收到Evo-余额投注 ,返回 消息:{}", JSON.toJSONString(res));
        return res;
    }

    @Operation(summary = "派彩")
    @PostMapping("/credit")
    public BalanceResponse credit(@RequestParam("authToken") String authToken, @RequestBody CreditRequest request) {
        request.setAuthToken(authToken);
        log.info("收到EVO-派彩 消息:{}", JSON.toJSONString(request));
        BalanceResponse res = evoGameApi.credit(request);
        log.info("收到Evo-派彩 ,返回 消息:{}", JSON.toJSONString(res));
        return res;
    }

    @Operation(summary = "取消投注")
    @PostMapping("/cancel")
    public BalanceResponse cancel(@RequestParam("authToken") String authToken, @RequestBody CancelRequest request) {
        request.setAuthToken(authToken);
        log.info("收到EVO-取消投注 消息:{}", JSON.toJSONString(request));
        BalanceResponse res = evoGameApi.cancel(request);
        log.info("收到Evo-取消投注使用 ,返回 消息:{}", JSON.toJSONString(res));
        return res;
    }
    @Operation(summary = "发放优惠")
    @PostMapping("/promo_payout")
    public BalanceResponse promo_payout(@RequestParam("authToken") String authToken, @RequestBody PromoPayoutRequest request) {
        request.setAuthToken(authToken);
        log.info("收到EVO-发放优惠 消息:{}", JSON.toJSONString(request));
        BalanceResponse res = evoGameApi.promoPayout(request);
        log.info("收到Evo-发放优惠使用 ,返回 消息:{}", JSON.toJSONString(res));
        return res;
    }

    @Operation(summary = "余额调整-特定活动使用")
    @PostMapping("/openSession")
    public PGBaseRes<PgAdjustAmountRes> openSession(@RequestBody PgAdjustmentReq request) {
        log.info("收到{}-余额调整-特定活动使用 消息:{}", VenueEnum.PG.getVenueName(), JSON.toJSONString(request));
        //PGBaseRes<PgAdjustAmountRes> res = evoService.processAdjustBet(request);
        //log.info("收到Evo-余额调整-特定活动使用 ,返回 消息:{}",   JSON.toJSONString(res));
        return null;
    }

    @Operation(summary = "余额调整-特定活动使用")
    @PostMapping("/close")
    public PGBaseRes<PgAdjustAmountRes> closeSession(@RequestBody PgAdjustmentReq request) {
        log.info("收到{}-余额调整-特定活动使用 消息:{}", VenueEnum.PG.getVenueName(), JSON.toJSONString(request));
        //PGBaseRes<PgAdjustAmountRes> res = evoService.processAdjustBet(request);
        //log.info("收到Evo-余额调整-特定活动使用 ,返回 消息:{}",   JSON.toJSONString(res));
        return null;
    }


}

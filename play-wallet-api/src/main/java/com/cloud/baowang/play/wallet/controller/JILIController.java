package com.cloud.baowang.play.wallet.controller;

import com.cloud.baowang.play.api.api.third.JILIGameApi;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.jili.req.*;
import com.cloud.baowang.play.api.vo.zf.*;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@LogExecution
@Slf4j
@RestController
@RequestMapping("callback/jili")
@Tag(name = "ZF单一钱包")
@AllArgsConstructor
public class JILIController {

//    private ZfService zfService;
    private JILIGameApi jiliGameApi;

    @Operation(summary = "游戏登入后令牌验证")
    @PostMapping("/auth")
    public ZfResp auth(@RequestBody ZfAuthReq req) {
        req.setPlatformCode(VenuePlatformConstants.TADA);
        return jiliGameApi.zfAuth(req);
    }

    @Operation(summary = "玩家注单(下注与结算同时发送)")
    @PostMapping("/bet")
    public ZfBetResp bet(@RequestBody ZfBetReq req) {
        req.setPlatformCode(VenuePlatformConstants.TADA);
        return jiliGameApi.zfBet(req);
    }

    @Operation(summary = "取消注单 (回滚下注及结算)")
    @PostMapping("/cancelBet")
    public ZfCancelBetResp cancelBet(@RequestBody ZfCancelBetReq req) {
        req.setPlatformCode(VenuePlatformConstants.TADA);
        return jiliGameApi.zfCancelBet(req);
    }

    @Operation(summary = "牌局型注单 (下注和结算分别发送)")
    @PostMapping("/sessionBet")
    public ZfBetResp sessionBet(@RequestBody ZfSessionBetReq req) {
        req.setPlatformCode(VenuePlatformConstants.TADA);
        return jiliGameApi.zfSessionBet(req);
    }

    @Operation(summary = "牌局型注单 (下注和结算分别发送)")
    @PostMapping("/cancelSessionBet")
    public ZfCancelBetResp cancelSessionBet(@RequestBody ZfCancelSessionBetReq req) {
        req.setPlatformCode(VenuePlatformConstants.TADA);
        return jiliGameApi.zfCancelSessionBet(req);
    }

    /**
     *  Retrieve user's latest balance.
     */
    @Operation(summary = "检索用户的最新余额")
    @PostMapping("/wallet/balance")
    public Object balance(@RequestBody JILIBalanceReq req, @RequestHeader("X-Signature") String signature) {
        req.setSignature(signature);
        return jiliGameApi.balance(req);
    }

    @Operation(summary = "投注")
    @PostMapping("/wallet/bet")
    public Object bet(@RequestBody JILIBetReq req, @RequestHeader("X-Signature") String signature){
        req.setSignature(signature);
        return jiliGameApi.bet(req);
    }


    @Operation(summary = "查詢投注结果")
    @PostMapping("/wallet/bet_result")
    public Object betResult(@RequestBody JILIBetResultReq req, @RequestHeader("X-Signature") String signature){
        req.setSignature(signature);
        return jiliGameApi.betResult(req);
    }

    @Operation(summary = "回滚")
    @PostMapping("/wallet/rollback")
    public Object rollback(@RequestBody JILIRollbackReq req, @RequestHeader("X-Signature") String signature){
        req.setSignature(signature);
        return jiliGameApi.rollback(req);
    }

    @Operation(summary = "结果调整")
    @PostMapping("/wallet/adjustment")
    public Object betResult(@RequestBody JILIAdjustmentReq req, @RequestHeader("X-Signature") String signature){
        req.setSignature(signature);
        return jiliGameApi.adjustment(req);
    }

    @Operation(summary = "扣除余额")
    @PostMapping("/wallet/bet_debit")
    public Object betDebit(@RequestBody JILIDebitReq req, @RequestHeader("X-Signature") String signature){
        req.setSignature(signature);
        return jiliGameApi.betDebit(req);
    }

    @Operation(summary = "结算赌注和更新余额")
    @PostMapping("/wallet/bet_credit")
    public Object betCredit(@RequestBody JILICreditReq req, @RequestHeader("X-Signature") String signature){
        req.setSignature(signature);
        return jiliGameApi.betCredit(req);
    }


}

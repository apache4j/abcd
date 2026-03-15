package com.cloud.baowang.play.wallet.controller;

import com.cloud.baowang.play.api.api.third.MarblesGameApi;
import com.cloud.baowang.play.api.vo.marbles.MarblesBalanceResp;
import com.cloud.baowang.play.api.vo.marbles.MarblesReq;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import com.cloud.baowang.play.api.vo.marbles.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * IM-弹珠游戏单一钱包
 */
@LogExecution
@Slf4j
@RestController
@RequestMapping("callback/marbles/api")
@Tag(name = "IM-弹珠游戏单一钱包")
@AllArgsConstructor
public class MarblesController {

//    private MarblesService marblesService;

    private final MarblesGameApi marblesGameApi;

    @Operation(summary = "查询余额")
    @PostMapping("/getBalance")
    public MarblesBalanceResp getBalance(@RequestBody MarblesReq req) {
        return marblesGameApi.getBalance(req);
    }


    @Operation(summary = "索取批准")
    @PostMapping("/getApproval")
    public MarblesResp getApproval(@RequestBody MarblesApprovalReq req) {
        return marblesGameApi.getApproval(req);
    }


    @Operation(summary = "下注")
    @PostMapping("/placeBet")
    public MarblesPlaceBetResp placeBet(@RequestBody MarblesPlaceBetReq req) {
        return marblesGameApi.placeBet(req);
    }

    @Operation(summary = "结算")
    @PostMapping("/settleBet")
    public MarblesRefundResp settleBet(@RequestBody MarblesSettleBetReq req) {
        return marblesGameApi.settleBet(req);
    }

    @Operation(summary = "退款/取消")
    @PostMapping("/refund")
    public MarblesRefundResp refund(@RequestBody MarblesRefundReq req) {
        return marblesGameApi.refund(req);
    }



}

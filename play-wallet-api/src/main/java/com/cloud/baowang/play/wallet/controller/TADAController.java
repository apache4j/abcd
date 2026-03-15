package com.cloud.baowang.play.wallet.controller;

import com.cloud.baowang.play.api.api.third.JILIGameApi;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.zf.*;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LogExecution
@Slf4j
@RestController
@RequestMapping("callback/tada")
@Tag(name = "ZF单一钱包")
@AllArgsConstructor
public class TADAController {

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

}

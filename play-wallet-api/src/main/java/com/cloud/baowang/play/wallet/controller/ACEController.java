package com.cloud.baowang.play.wallet.controller;

import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.play.api.api.third.ACEGameApi;
import com.cloud.baowang.play.api.vo.ace.req.*;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@LogExecution
@Slf4j
@RestController
@RequestMapping("callback/ace")
@Tag(name = "ACE单一钱包")
@AllArgsConstructor
public class ACEController {

    private ACEGameApi aceGameApi;

    // 4-1、取得余额 (GetBalance)
    @Operation(summary = "验证")
    @PostMapping("/authenticate")
    public Object authenticate(@RequestBody ACEAuthenticateReq req, @RequestHeader(value = "token", required = false, defaultValue = "") String token) {
        log.info("ACE authenticate :{}", JSON.toJSONString(req));
        req.setToken(token);
        return aceGameApi.authenticate(req);
    }

    // 4-1、取得余额 (GetBalance)
    @Operation(summary = "取得余额")
    @GetMapping("/getbalance")
    public Object getbalance(@RequestParam("playerID") Integer playerID,
                             @RequestHeader(value = "token", required = false, defaultValue = "") String token )
    {
        ACEGetbalanceReq req = new ACEGetbalanceReq();
        req.setToken(token);
        req.setPlayerID(playerID.toString());
        log.info("ACE getbalance :{}, ip: {}", JSON.toJSONString(req), CurrReqUtils.getReqIp());
        return aceGameApi.getbalance(req);
    }

    // 4-4、下注 (Bet)
    @Operation(summary = "下注")
    @PostMapping("/bet")
    public Object bet(@RequestBody ACEBetReq req,
                      @RequestHeader(value = "token", required = false, defaultValue = "") String token )
    {
        req.setToken(token);
        log.info("ACE bet :{}", JSON.toJSONString(req));
        return aceGameApi.bet(req);
    }

    // 4-5、派彩 (Settle)
    @Operation(summary = "派彩")
    @PostMapping("/betresult")
    public Object betresult(@RequestBody ACEBetresultReq req,
                         @RequestHeader(value = "token", required = false, defaultValue = "") String token )
    {
        req.setToken(token);
        log.info("ACE betresult :{}", JSON.toJSONString(req));
        return aceGameApi.betresult(req);
    }

    @Operation(summary = "回退")
    @PostMapping("/refund")
    public Object refund(@RequestBody ACERefundReq req,
                            @RequestHeader(value = "token", required = false, defaultValue = "") String token )
    {
        req.setToken(token);
        log.info("ACE refund :{}", JSON.toJSONString(req));
        return aceGameApi.refund(req);
    }

    @Operation(summary = "活动派彩")
    @PostMapping("/jackpotwin")
    public Object jackpotwin(@RequestBody ACEJackpotwinReq req,
                              @RequestHeader(value = "token", required = false, defaultValue = "") String token )
    {
        log.info("ACE jackpotwin :{}", JSON.toJSONString(req));
        req.setToken(token);
        return aceGameApi.jackpotwin(req);
    }
}

package com.cloud.baowang.play.wallet.controller;

import com.cloud.baowang.play.api.api.third.FCGameApi;
import com.cloud.baowang.play.api.vo.fc.req.FCBaseReq;
import com.cloud.baowang.play.api.vo.fc.req.GetBalanceReq;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@LogExecution
@Slf4j
@RestController
@RequestMapping("callback/fc")
@Tag(name = "FC单一钱包")
@AllArgsConstructor
public class FCController {

    private FCGameApi fcGameApi;

    // 4-1、取得余额 (GetBalance)
    @Operation(summary = "取得余额")
    @PostMapping("/GetBalance")
    public Object GetBalance(@RequestParam String AgentCode,
                          @RequestParam String Currency,
                          @RequestParam String Params,
                          @RequestParam String Sign
    ) {
        return fcGameApi.GetBalance(FCBaseReq.builder().AgentCode(AgentCode).Currency(Currency).Params(Params).Sign(Sign).build());
    }

    // 4-2、下注信息及游戏结果 (BetNInfo)
    @Operation(summary = "下注信息及游戏结果")
    @PostMapping("/BetNInfo")
    public Object BetNInfo(@RequestParam String AgentCode,
                           @RequestParam String Currency,
                           @RequestParam String Params,
                           @RequestParam String Sign) {
        return fcGameApi.BetNInfo(FCBaseReq.builder().AgentCode(AgentCode).Currency(Currency).Params(Params).Sign(Sign).build());
    }

    // 4-3、取消下注與游戏结果 (CancelBetNInfo)
    @Operation(summary = "取消下注與游戏结果")
    @PostMapping("/CancelBetNInfo")
    public Object CancelBetNInfo(@RequestParam String AgentCode,
                                 @RequestParam String Currency,
                                 @RequestParam String Params,
                                 @RequestParam String Sign) {
        return fcGameApi.CancelBetNInfo(FCBaseReq.builder().AgentCode(AgentCode).Currency(Currency).Params(Params).Sign(Sign).build());

    }

    // 4-4、下注 (Bet)
    @Operation(summary = "下注")
    @PostMapping("/Bet")
    public Object Bet(@RequestParam String AgentCode,
                      @RequestParam String Currency,
                      @RequestParam String Params,
                      @RequestParam String Sign) {
        return fcGameApi.Bet(FCBaseReq.builder().AgentCode(AgentCode).Currency(Currency).Params(Params).Sign(Sign).build());

    }

    // 4-5、派彩 (Settle)
    @Operation(summary = "派彩")
    @PostMapping("/Settle")
    public Object Settle(@RequestParam String AgentCode,
                         @RequestParam String Currency,
                         @RequestParam String Params,
                         @RequestParam String Sign) {
        return fcGameApi.Settle(FCBaseReq.builder().AgentCode(AgentCode).Currency(Currency).Params(Params).Sign(Sign).build());
    }

    // 4-6、取消下注 (CancelBet)
    @Operation(summary = "取消下注")
    @PostMapping("/CancelBet")
    public Object CancelBet(@RequestParam String AgentCode,
                            @RequestParam String Currency,
                            @RequestParam String Params,
                            @RequestParam String Sign) {
        return fcGameApi.CancelBet(FCBaseReq.builder().AgentCode(AgentCode).Currency(Currency).Params(Params).Sign(Sign).build());
    }

    // 4-7、活动派彩 (EventSettle)
    @Operation(summary = "活动派彩")
    @PostMapping("/EventSettle")
    public Object EventSettle(@RequestParam String AgentCode,
                              @RequestParam String Currency,
                              @RequestParam String Params,
                              @RequestParam String Sign) {
        return fcGameApi.EventSettle(FCBaseReq.builder().AgentCode(AgentCode).Currency(Currency).Params(Params).Sign(Sign).build());
    }

    // 4-8、Free Spin 下注信息及游戏结果 (FreeSpinBetNInfo)
    @Operation(summary = "下注信息及游戏结果")
    @PostMapping("/FreeSpinBetNInfo")
    public Object FreeSpinBetNInfo(@RequestParam String AgentCode,
                                   @RequestParam String Currency,
                                   @RequestParam String Params,
                                   @RequestParam String Sign) {

        return fcGameApi.FreeSpinBetNInfo(FCBaseReq.builder().AgentCode(AgentCode).Currency(Currency).Params(Params).Sign(Sign).build());
    }
}

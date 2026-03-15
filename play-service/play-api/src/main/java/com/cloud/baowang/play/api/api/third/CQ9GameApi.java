package com.cloud.baowang.play.api.api.third;


import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.cq9.request.CQ9BetReq;
import com.cloud.baowang.play.api.vo.cq9.request.CQ9EndRoundReq;
import com.cloud.baowang.play.api.vo.cq9.request.CQ9PayoffReq;
import com.cloud.baowang.play.api.vo.cq9.request.CQ9RollinReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "cq9-api", value = ApiConstants.NAME)
@Tag(name = "CQ9-游戏")
public interface CQ9GameApi {
    String PREFIX = ApiConstants.PREFIX + "/cq9/api/";

    @Operation(summary = "校验用户")
    @GetMapping(PREFIX + "checkPlayer")
    JSONObject checkPlayer(@RequestParam("account") String account, @RequestParam("token") String token);

 /*   @Operation(summary = "查询余额")
    @PostMapping(PREFIX + "queryBalance")
    JSONObject queryBalance(@RequestBody FTGGetBalanceReq req);*/


    @Operation(summary = "转账到游戏")
    @PostMapping(PREFIX + "bet")
    JSONObject bet(@RequestBody CQ9BetReq req);

    @Operation(summary = "转账到游戏")
    @PostMapping(PREFIX + "rollout")
    JSONObject rollout(@RequestBody CQ9BetReq req);

    @Operation(summary = "下注/转账到游戏")
    @PostMapping(PREFIX + "takeall")
    JSONObject takeall(@RequestBody CQ9BetReq req);

    /*@Operation(summary = "回滚")
    @PostMapping(PREFIX + "cancelBet")
    JSONObject cancelBet(@RequestBody FTGBetReq req);*/

    @Operation(summary = "派彩")
    @PostMapping(PREFIX + "payOut")
    JSONObject payOut(@RequestBody CQ9EndRoundReq req);

    @Operation(summary = "派彩")
    @PostMapping(PREFIX + "payoff")
    JSONObject payoff(@RequestBody CQ9PayoffReq req);

    @Operation(summary = "下注/转账到游戏")
    @PostMapping(PREFIX + "rollin")
    JSONObject rollin(@RequestBody CQ9RollinReq req);

    @Operation(summary = "查询余额")
    @GetMapping(PREFIX + "balance")
    JSONObject balance(@RequestParam("account") String account,
                       @RequestParam(value = "gamecode", required = false) String gamecode,
                       @RequestParam("wtoken") String wtoken);

    @Operation(summary = "退回 bet/rollout/takeall 金額")
    @PostMapping(PREFIX + "refund")
    JSONObject refund(@RequestBody CQ9BetReq req);


    @Operation(summary = "錢包交易紀錄查詢")
    @GetMapping(PREFIX + "record")
    JSONObject record(@RequestParam("mtcode") String mtcode,
                      @RequestParam("wtoken") String wtoken);
}

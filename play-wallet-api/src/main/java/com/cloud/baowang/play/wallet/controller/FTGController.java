package com.cloud.baowang.play.wallet.controller;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.api.third.FTGGameApi;
import com.cloud.baowang.play.api.vo.base.FTGErrorRes;
import com.cloud.baowang.play.api.vo.ftg.FTGBetReq;
import com.cloud.baowang.play.api.vo.ftg.FTGCancelReq;
import com.cloud.baowang.play.api.vo.ftg.FTGGetBalanceReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/callback/ftg/")
@Tag(name = "FTG游戏回调")
public class FTGController {

    private final FTGGameApi ftgGameApi;

    @Operation(summary = "查询余额")
    @PostMapping("getBalance")
    public JSONObject getBalance(@RequestBody FTGGetBalanceReq request) {
        long startTime = System.currentTimeMillis();
        FTGErrorRes<JSONObject> ftgGetBalanceRes = ftgGameApi.queryBalance(request);
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("收到{}-查询免转钱包用户余额 ,执行了:{}秒, 消息:{}", VenueEnum.FTG.getVenueName(), durationSeconds, JSON.toJSON(request));
        return ftgGetBalanceRes.getData();
    }

    @Operation(summary = "下注")
    @PostMapping("bet")
    public JSONObject bet(@RequestBody FTGBetReq request) {
        long startTime = System.currentTimeMillis();
        FTGErrorRes<JSONObject> ftgGetBalanceRes = ftgGameApi.bet(request);
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("收到{}-下注 ,执行了:{}秒, 消息:{}", VenueEnum.FTG.getVenueName(), durationSeconds, JSON.toJSON(request));
        return ftgGetBalanceRes.getData();
    }

    @Operation(summary = "回滚")
    @PostMapping("cancelBet")
    public JSONObject cancelBet(@RequestBody FTGCancelReq request) {
        long startTime = System.currentTimeMillis();
        FTGErrorRes<JSONObject> ftgGetBalanceRes = ftgGameApi.cancelBet(request);
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("收到{}-回滚 ,执行了:{}秒, 消息:{}", VenueEnum.FTG.getVenueName(), durationSeconds, JSON.toJSON(request));
        return ftgGetBalanceRes.getData();
    }


    @Operation(summary = "派彩")
    @PostMapping("payout")
    public JSONObject payOut(@RequestBody FTGBetReq request) {
        long startTime = System.currentTimeMillis();
        FTGErrorRes<JSONObject> ftgGetBalanceRes = ftgGameApi.payOut(request);
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("收到{}-派彩 ,执行了:{}秒, 消息:{}", VenueEnum.FTG.getVenueName(), durationSeconds, JSON.toJSON(request));
        return ftgGetBalanceRes.getData();
    }


}

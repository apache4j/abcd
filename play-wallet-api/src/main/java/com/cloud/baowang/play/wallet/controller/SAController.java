package com.cloud.baowang.play.wallet.controller;

import com.cloud.baowang.play.api.api.third.SAGameApi;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LogExecution
@Slf4j
@RestController
@RequestMapping("/sa")
@Tag(name = "SA真人")
public class SAController {

    @Resource
    private SAGameApi saGameApi;

    @Operation(summary = "查询余额")
    @PostMapping(value = "/GetUserBalance", consumes = MediaType.TEXT_PLAIN_VALUE)
    public String queryBalance(@RequestBody String data) {
        long start = System.currentTimeMillis();
        log.info("SA查询余额----开始:{}", data);
        String result = saGameApi.queryBalance(data);
        long end = System.currentTimeMillis();
        log.info("SA查询余额----:结束:{},result:{}", end - start,result);
        return result;
    }

    @Operation(summary = "下注")
    @PostMapping(value = "/PlaceBet", consumes = MediaType.TEXT_PLAIN_VALUE)
    public String placeBet(@RequestBody String data) {
        long start = System.currentTimeMillis();
        log.info("SA下注----开始:{}", data);
        String result = saGameApi.placeBet(data);
        long end = System.currentTimeMillis();
        log.info("SA下注----结束:{},result:{}", end - start,result);
        return result;
    }

    @Operation(summary = "派彩")
    @PostMapping(value = "/PlayerWin", consumes = MediaType.TEXT_PLAIN_VALUE)
    public String playerWin(@RequestBody String data) {
        long start = System.currentTimeMillis();
        log.info("SA派彩----开始:{}", data);
        String result = saGameApi.playerWin(data);
        long end = System.currentTimeMillis();
        log.info("SA派彩----结束:{},result:{}", end - start,result);
        return result;
    }

    @Operation(summary = "游戏完结输")
    @PostMapping(value = "/PlayerLost", consumes = MediaType.TEXT_PLAIN_VALUE)
    public String playerLost(@RequestBody String data) {
        long start = System.currentTimeMillis();
        log.info("SA游戏完结输----开始:{}", data);
        String result = saGameApi.playerLost(data);
        long end = System.currentTimeMillis();
        log.info("SA游戏完结输----结束:{},result:{}", end - start,result);
        return result;
    }

    @Operation(summary = "取消下注")
    @PostMapping(value = "/PlaceBetCancel", consumes = MediaType.TEXT_PLAIN_VALUE)
    public String placeBetCancel(@RequestBody String data) {
        long start = System.currentTimeMillis();
        log.info("SA取消下注----开始:{}", data);
        String result = saGameApi.placeBetCancel(data);
        long end = System.currentTimeMillis();
        log.info("SA取消下注----结束:{},result:{}", end - start,result);
        return result;
    }



    @Operation(summary = "奖励")
    @PostMapping(value = "/BalanceAdjustment", consumes = MediaType.TEXT_PLAIN_VALUE)
    public String balanceAdjustment(@RequestBody String data) {
        long start = System.currentTimeMillis();
        log.info("SA奖励----开始:{}", data);
        String result = saGameApi.balanceAdjustment(data);
        long end = System.currentTimeMillis();
        log.info("SA奖励----结束:{},result:{}", end - start,result);
        return result;
    }


}

package com.cloud.baowang.play.wallet.controller;

import com.alibaba.fastjson.JSON;
import com.cloud.baowang.play.api.api.order.DBPanDaSportServiceApi;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.vo.base.DbPanDaBaseRes;
import com.cloud.baowang.play.api.vo.dbPanDaSport.DbPanDaBalanceReq;
import com.cloud.baowang.play.api.vo.dbPanDaSport.DbPanDaConfirmBetReq;
import com.cloud.baowang.play.api.vo.dbPanDaSport.DbPanDaSportBetReqVO;
import com.cloud.baowang.play.api.vo.dbPanDaSport.DbPanDaSportBetResVO;
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
@RequestMapping("/db/sport")
@Tag(name = "DB熊猫体育")
public class DBPanDaSportController {

    private final DBPanDaSportServiceApi dbPanDaSportServiceApi;

    @Operation(summary = "查询余额")
    @PostMapping("/getBalance")
    public DbPanDaBaseRes<String> getBalance(@RequestBody  DbPanDaBalanceReq request) {
        long startTime = System.currentTimeMillis();
        log.info("收到{} getBalance 消息:{}", VenueEnum.DB_PANDA_SPORT.getVenueName(), JSON.toJSON(request));
        DbPanDaBaseRes<String> balance = dbPanDaSportServiceApi.getBalance(request);
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("返回{} getBalance ,执行了:{}秒, 消息:{}", VenueEnum.DB_PANDA_SPORT.getVenueName(), durationSeconds, balance);
        return balance;
    }

    @Operation(summary = "下注")
    @PostMapping("/bet")
    public DbPanDaBaseRes<DbPanDaSportBetResVO> transfer(@RequestBody DbPanDaSportBetReqVO request) {
        long startTime = System.currentTimeMillis();
        log.info("收到{} bet 消息:{}", VenueEnum.DB_PANDA_SPORT.getVenueName(), JSON.toJSON(request));
        DbPanDaBaseRes<DbPanDaSportBetResVO> result = dbPanDaSportServiceApi.transfer(request);
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("返回{} bet ,执行了:{}秒, 消息:{}", VenueEnum.DB_PANDA_SPORT.getVenueName(), durationSeconds, result.getData());
        return result;
    }



    @Operation(summary = "确认下注")
    @PostMapping("/confirmBet")
    public DbPanDaBaseRes<Void> confirmBet(@RequestBody DbPanDaConfirmBetReq request) {
        long startTime = System.currentTimeMillis();
        log.info("收到{} confirmBet 消息:{}", VenueEnum.DB_PANDA_SPORT.getVenueName(), JSON.toJSON(request));
        DbPanDaBaseRes<Void> result =  dbPanDaSportServiceApi.confirmBet(request);
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("返回{} confirmBet ,执行了:{}秒", VenueEnum.DB_PANDA_SPORT.getVenueName(), durationSeconds);
        return result;
    }



}

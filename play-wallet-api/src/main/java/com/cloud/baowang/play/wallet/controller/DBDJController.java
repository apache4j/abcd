package com.cloud.baowang.play.wallet.controller;

import com.alibaba.fastjson.JSON;
import com.cloud.baowang.play.api.api.third.DbDjGameApi;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.vo.dbDj.DBBalanceReq;
import com.cloud.baowang.play.api.vo.dbDj.DBBalanceRes;
import com.cloud.baowang.play.api.vo.dbDj.DbDJTransferReq;
import com.cloud.baowang.play.api.vo.dbDj.DbDJTransferRes;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@LogExecution
@Slf4j
@RestController
@RequestMapping("/db/eSports/api/fund")
@Tag(name = "DB电竞")
@AllArgsConstructor
public class DBDJController {


    private DbDjGameApi dbDjGameApi;

    @Operation(summary = "查询余额")
    @PostMapping("/getBalance")
    public DBBalanceRes getBalance(DBBalanceReq request) {
        long startTime = System.currentTimeMillis();
        log.info("收到{} getBalance 消息:{}", VenueEnum.DB_DJ.getVenueName(), JSON.toJSON(request));
        DBBalanceRes balance = dbDjGameApi.queryBalance(request);
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("返回{} getBalance ,执行了:{}秒, 消息:{}", VenueEnum.DB_DJ.getVenueName(), durationSeconds, balance);
        return balance;
    }

    @Operation(summary = "账变")
    @PostMapping("/transfer")
    public DbDJTransferRes transfer(DbDJTransferReq request) {
        long startTime = System.currentTimeMillis();
        log.info("收到{} transfer 消息:{}", VenueEnum.DB_DJ.getVenueName(), JSON.toJSON(request));
        DbDJTransferRes res = dbDjGameApi.transfer(request);
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("返回{} transfer ,执行了:{}秒, 消息:{}", VenueEnum.DB_DJ.getVenueName(), durationSeconds, res);
        return res;
    }



}

package com.cloud.baowang.play.wallet.controller;

import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.play.api.api.third.SHGameApi;
import com.cloud.baowang.play.api.vo.base.ShBaseRes;
import com.cloud.baowang.play.api.vo.sh.ShAdjustBalanceReq;
import com.cloud.baowang.play.api.vo.sh.ShAdjustBalanceRes;
import com.cloud.baowang.play.api.vo.sh.ShBalanceRes;
import com.cloud.baowang.play.api.vo.sh.ShQueryBalanceReq;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LogExecution
@Slf4j
@RestController
@RequestMapping("/sh")
@Tag(name = "SH视界真人")
public class SHController {

//    @Resource
//    private ShService shService;

    @Resource
    private SHGameApi shGameApi;

    @Operation(summary = "查询余额")
    @PostMapping("/queryBalance")
    public ShBaseRes<ShBalanceRes> queryBalance(@RequestBody ShQueryBalanceReq request) {
        long start = System.currentTimeMillis();
        log.info("查询余额----开始:{}", JSON.toJSON(request));
        ShBaseRes<ShBalanceRes> result = shGameApi.queryBalance(request);
        long end = System.currentTimeMillis();
        log.info("查询余额----结束:{}", end - start);
        return result;
    }

    @Operation(summary = "余额加扣款")
    @PostMapping("/adjustBalance")
    public ShBaseRes<ShAdjustBalanceRes> adjustBalance(@RequestBody ShAdjustBalanceReq request) {
        long start = System.currentTimeMillis();
        log.info("余额加扣款----开始:{}", JSON.toJSON(request));
        ShBaseRes<ShAdjustBalanceRes> result = shGameApi.adjustBalance(request);
        long end = System.currentTimeMillis();
        log.info("余额加扣款----结束:{}", end - start);
        return result;
    }

}

package com.cloud.baowang.play.wallet.controller;

import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.play.api.api.third.WpACELTGameApi;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.vo.acelt.*;
import com.cloud.baowang.play.api.vo.base.ACELTBaseRes;
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
@RequestMapping("/aceltWp/rest/freetransfer")
@Tag(name = "彩票回调")
public class AceltWpController {

    private final WpACELTGameApi aceltGameApi;

    @Operation(summary = "查询免转钱包用户余额")
    @PostMapping("/getuserbalance")
    public ACELTBaseRes<ACELTGetBalanceRes> getUserBalance(@RequestBody ACELTGetBalanceReq request) {
        long startTime = System.currentTimeMillis();
        log.info("收到{}-查询免转钱包用户余额 消息:{}", VenueEnum.WP_ACELT.getVenueName(), JSON.toJSON(request));
        ACELTBaseRes<ACELTGetBalanceRes> result = aceltGameApi.queryBalance(request);
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("收到{}-查询免转钱包用户余额 ,执行了:{}秒, 消息:{}", VenueEnum.WP_ACELT.getVenueName(), durationSeconds, request);
        return result;
    }

    @Operation(summary = "账变接口")
    @PostMapping("/accountchange")
    public ACELTBaseRes<ACELTAccountCheangeRes> accountChange(@RequestBody ACELTAccountCheangeReq request) {
        long startTime = System.currentTimeMillis();
        log.info("收到{}-账变接口 消息:{}", VenueEnum.WP_ACELT.getVenueName(), JSON.toJSON(request));
        ACELTBaseRes<ACELTAccountCheangeRes> result = aceltGameApi.accountChange(request);
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("收到{}-账变接口 ,执行了:{}秒, 消息:{}", VenueEnum.WP_ACELT.getVenueName(), durationSeconds, request);
        return result;
    }


    @Operation(summary = "免转钱包帐变回调接口")
    @PostMapping("/accountChangeCallBack")
    public ACELTBaseRes<ACELTAccountCheangeRes> accountChangeCallBack(@RequestBody ACELTAccountChangeCallBackReq request) {
        long startTime = System.currentTimeMillis();
        log.info("收到{}-免转钱包帐变回调接口 消息:{}", VenueEnum.WP_ACELT.getVenueName(), JSON.toJSON(request));
        ACELTBaseRes<ACELTAccountCheangeRes> result = aceltGameApi.accountChangeCallBack(request);
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("收到{}-免转钱包帐变回调接口 ,执行了:{}秒, 消息:{}", VenueEnum.WP_ACELT.getVenueName(), durationSeconds, request);
        return result;
    }


}

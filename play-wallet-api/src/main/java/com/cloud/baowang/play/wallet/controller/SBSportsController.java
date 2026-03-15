package com.cloud.baowang.play.wallet.controller;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.api.order.SBASportServiceApi;
import com.cloud.baowang.play.api.enums.SBActionEnum;
import com.cloud.baowang.play.api.vo.sba.*;
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
@RequestMapping("/sbCall/api")
@Tag(name = "沙巴体育回调接口")
public class SBSportsController {

    private final SBASportServiceApi sbaSportServiceApi;

    @Operation(summary = "沙巴推送-下注")
    @PostMapping("/placebet")
    public Object placeBet(@RequestBody String request) {
        long startTime = System.currentTimeMillis();
        log.info("收到{} placeBet 消息:{}", VenueEnum.SBA.getVenueName(), request);
        SBBaseReq baseReq = JSONObject.parseObject(request, SBBaseReq.class);
        Object result = sbaSportServiceApi.toSBAction(baseReq, SBActionEnum.PLACE_BET.getCode());
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("返回{} placeBet ,执行了:{}秒, 消息:{}", VenueEnum.SBA.getVenueName(), durationSeconds, result);

        return result;
    }


    @Operation(summary = "沙巴推送-确认下注")
    @PostMapping("/confirmbet")
    public Object confirmBet(@RequestBody String request) {
        long startTime = System.currentTimeMillis();
        log.info("收到{} confirmBet 消息:{}", VenueEnum.SBA.getVenueName(), request);
        SBBaseReq baseReq = JSONObject.parseObject(request, SBBaseReq.class);
        Object result = sbaSportServiceApi.toSBAction(baseReq, SBActionEnum.CONFIRM_BET.getCode());
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("返回{} confirmBet ,执行了:{}秒, 消息:{}", VenueEnum.SBA.getVenueName(), durationSeconds, result);


        return result;
    }

    @Operation(summary = "沙巴推送-取消下注")
    @PostMapping("/cancelbet")
    public Object cancelBet(@RequestBody String request) {
        long startTime = System.currentTimeMillis();
        log.info("收到{} cancelBet 消息:{}", VenueEnum.SBA.getVenueName(), request);
        SBBaseReq baseReq = JSONObject.parseObject(request, SBBaseReq.class);
        Object result = sbaSportServiceApi.toSBAction(baseReq, SBActionEnum.CANCEL_BET.getCode());
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("返回{} cancelBet ,执行了:{}秒, 消息:{}", VenueEnum.SBA.getVenueName(), durationSeconds, result);
        return result;
    }


    @Operation(summary = "沙巴推送-结算")
    @PostMapping("/settle")
    public Object settle(@RequestBody String request) {
        long startTime = System.currentTimeMillis();
        log.info("收到{} settle 消息:{}", VenueEnum.SBA.getVenueName(), request);
        SBBaseReq baseReq = JSONObject.parseObject(request, SBBaseReq.class);
        Object result = sbaSportServiceApi.toSBAction(baseReq, SBActionEnum.SETTLE.getCode());
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("返回{} settle ,执行了:{}秒, 消息:{}", VenueEnum.SBA.getVenueName(), durationSeconds, result);
        return result;
    }


    @Operation(summary = "沙巴推送-重新结算")
    @PostMapping("/resettle")
    public Object resettle(@RequestBody String request) {
        long startTime = System.currentTimeMillis();
        log.info("收到{} resettle 消息:{}", VenueEnum.SBA.getVenueName(), request);
        SBBaseReq baseReq = JSONObject.parseObject(request, SBBaseReq.class);
        Object result = sbaSportServiceApi.toSBAction(baseReq, SBActionEnum.RE_SETTLE.getCode());
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("返回{} resettle ,执行了:{}秒, 消息:{}", VenueEnum.SBA.getVenueName(), durationSeconds, result);
        return result;
    }


    @Operation(summary = "沙巴推送-失败重试")
    @PostMapping("/unsettle")
    public Object unsettle(@RequestBody String request) {
        long startTime = System.currentTimeMillis();
        log.info("收到{} unsettle 消息:{}", VenueEnum.SBA.getVenueName(), request);
        SBBaseReq baseReq = JSONObject.parseObject(request, SBBaseReq.class);
        Object result = sbaSportServiceApi.toSBAction(baseReq, SBActionEnum.UN_SETTLE.getCode());
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("返回{} unsettle ,执行了:{}秒, 消息:{}", VenueEnum.SBA.getVenueName(), durationSeconds, result);
        return result;
    }


    @Operation(summary = "沙巴推送-串关下注")
    @PostMapping("/placebetparlay")
    public Object placeBetParlay(@RequestBody String request) {
        long startTime = System.currentTimeMillis();
        log.info("收到{} placeBetParlay 消息:{}", VenueEnum.SBA.getVenueName(), request);
        SBBaseReq baseReq = JSONObject.parseObject(request, SBBaseReq.class);
        Object result = sbaSportServiceApi.toSBAction(baseReq, SBActionEnum.PLACE_BET_PARLAY.getCode());
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("返回{} placeBetParlay ,执行了:{}秒, 消息:{}", VenueEnum.SBA.getVenueName(), durationSeconds, result);
        return result;
    }


    @Operation(summary = "确认串关下注")
    @PostMapping("/confirmbetparlay")
    public Object confirmBetParlay(@RequestBody String request) {
        long startTime = System.currentTimeMillis();
        log.info("收到{} confirmBetParlay 消息:{}", VenueEnum.SBA.getVenueName(), request);
        SBBaseReq baseReq = JSONObject.parseObject(request, SBBaseReq.class);
        Object result = sbaSportServiceApi.toSBAction(baseReq, SBActionEnum.CONFIRM_BET_PARLAY.getCode());
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("返回{} confirmBetParlay ,执行了:{}秒, 消息:{}", VenueEnum.SBA.getVenueName(), durationSeconds, result);
        return result;
    }


}

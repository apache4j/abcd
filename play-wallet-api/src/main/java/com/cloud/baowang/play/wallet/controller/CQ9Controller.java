package com.cloud.baowang.play.wallet.controller;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.play.api.api.third.CQ9GameApi;
import com.cloud.baowang.play.api.enums.cq9.CQ9CallEnums;
import com.cloud.baowang.play.api.vo.cq9.request.*;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

@LogExecution
@Slf4j
@RestController
@RequestMapping("/callback/cq9")
@Tag(name = "CQ9单一钱包")
@AllArgsConstructor
public class CQ9Controller {


    private final CQ9GameApi cq9GameApi;




    @Operation(summary = "檢查玩家帳號")
    @GetMapping("/player/check/{account}")
    public JSONObject checkPlayer(@PathVariable String account, @RequestHeader("wtoken") String wtoken) {
        long startTime = System.currentTimeMillis();
        log.info("收到{}-檢查玩家帳號 消息:{}", VenueEnum.CQ9.getVenueName(), account);
        JSONObject cq9StatusRsp = cq9GameApi.checkPlayer(account, wtoken);
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("收到{}-檢查玩家帳號 ,执行了:{}秒, 消息:{}", VenueEnum.CQ9.getVenueName(), durationSeconds, JSON.toJSONString(cq9StatusRsp));
        return cq9StatusRsp;
    }

    @Operation(summary = "取得玩家錢包餘額")
    @GetMapping("/transaction/balance/{account}")
    public JSONObject balance(@PathVariable("account") String account,
                              @RequestParam(value = "gamecode", required = false) String gamecode,
                              @RequestHeader("wtoken") String wtoken) {
        long startTime = System.currentTimeMillis();
        log.info("收到{}-取得玩家錢包餘額 消息:{}", VenueEnum.CQ9.getVenueName(), account);
        JSONObject cq9StatusRsp = cq9GameApi.balance(account, gamecode, wtoken);
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("收到{}-取得玩家錢包餘額 ,执行了:{}秒, 消息:{}", VenueEnum.CQ9.getVenueName(), durationSeconds, JSON.toJSONString(cq9StatusRsp));
        return cq9StatusRsp;
    }

    @Operation(summary = "下注")
    @PostMapping("/transaction/game/bet")
    public JSONObject bet(CQ9BetReq req, @RequestHeader("wtoken") String wtoken) {
        long startTime = System.currentTimeMillis();
        log.info("收到{}-取得玩家下注 消息:{}", VenueEnum.CQ9.getVenueName(), req.getAccount());
        req.setWtoken(wtoken);
        req.setCallType(CQ9CallEnums.BET.getCode());
        req.setType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        req.setBalanceType(CommonConstant.business_two_str);
        req.setActionFlag(true);

        JSONObject cq9StatusRsp = cq9GameApi.bet(req);
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("收到{}-取得玩家下注 ,执行了:{}秒, 消息:{}", VenueEnum.CQ9.getVenueName(), durationSeconds, JSON.toJSONString(cq9StatusRsp));
        return cq9StatusRsp;
    }

    @Operation(summary = "電子遊戲派彩")
    @PostMapping("/transaction/game/endround")
    public JSONObject endround(@RequestParam Map<String, String> params, @RequestHeader("wtoken") String wtoken) throws JsonProcessingException {
        //log.info(JSONObject.toJSONString(req));
        ObjectMapper objectMapper = new ObjectMapper();

        CQ9EndRoundReq request = new CQ9EndRoundReq();
        request.setAccount(params.get("account"));
        request.setGamehall(params.get("gamehall"));
        request.setGamecode(params.get("gamecode"));
        request.setRoundid(params.get("roundid"));
        request.setCreateTime(params.get("createTime"));

        // 手动解析 data
        String dataJson = params.get("data");
        if(ObjectUtil.isNotEmpty(dataJson)){
            List<EventData> eventDataList = objectMapper.readValue(dataJson, new TypeReference<List<EventData>>() {});
            request.setData(eventDataList);
        }
        long startTime = System.currentTimeMillis();
        request.setWtoken(wtoken);
        request.setCallType(CQ9CallEnums.ENDROUND.getCode());
        log.info("收到{}-電子遊戲派彩 消息:{}", VenueEnum.CQ9.getVenueName(), JSONObject.toJSONString(request));
        JSONObject cq9StatusRsp = cq9GameApi.payOut(request);
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("收到{}-電子遊戲派彩 ,执行了:{}秒, 消息:{}", VenueEnum.CQ9.getVenueName(), durationSeconds, JSON.toJSONString(cq9StatusRsp));
        return cq9StatusRsp;
    }

    @Operation(summary = "轉出玩家『部分』額度至遊戲(轉回額度接口使用Rollin API")
    @PostMapping("/transaction/game/rollout")
    public JSONObject rollout(CQ9BetReq req, @RequestHeader("wtoken") String wtoken) {
        long startTime = System.currentTimeMillis();
        req.setWtoken(wtoken);
        req.setCallType(CQ9CallEnums.ROLLOUT.getCode());
        log.info("收到{}-轉出玩家『部分』額度至遊戲 消息:{}", VenueEnum.CQ9.getVenueName(), req.getAccount());
        //
        req.setType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        req.setBalanceType(CommonConstant.business_two_str);
        req.setActionFlag(true);
        JSONObject cq9StatusRsp = cq9GameApi.rollout(req);
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("收到{}-轉出玩家『部分』額度至遊戲,执行了:{}秒, 消息:{}", VenueEnum.CQ9.getVenueName(), durationSeconds, JSON.toJSONString(cq9StatusRsp));
        return cq9StatusRsp;
    }

    @Operation(summary = "一場遊戲結束，轉回額度至玩家錢包")
    @PostMapping("/transaction/game/rollin")
    public JSONObject rollin(CQ9RollinReq req, @RequestHeader("wtoken") String wtoken) {
        long startTime = System.currentTimeMillis();
        req.setWtoken(wtoken);
        req.setCallType(CQ9CallEnums.ROLLIN.getCode());
        log.info("收到{}-一場遊戲結束，轉回額度至玩家錢包 消息:{}", VenueEnum.CQ9.getVenueName(), req.getAccount());
        req.setType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        req.setBalanceType(CommonConstant.business_one_str);
        JSONObject cq9StatusRsp = cq9GameApi.rollin(req);
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("收到{}-一場遊戲結束，轉回額度至玩家錢包,执行了:{}秒, 消息:{}", VenueEnum.CQ9.getVenueName(), durationSeconds, JSON.toJSONString(cq9StatusRsp));
        return cq9StatusRsp;
    }


    @Operation(summary = "轉出玩家『全部』額度至遊戲")
    @PostMapping("/transaction/game/takeall")
    public JSONObject takeall(CQ9BetReq req, @RequestHeader("wtoken") String wtoken) {
        long startTime = System.currentTimeMillis();
        req.setWtoken(wtoken);
        req.setCallType(CQ9CallEnums.TAKEALL.getCode());
        // 特殊
        req.setType(WalletEnum.CoinTypeEnum.OTHER_SUBTRACT.getCode());
        req.setTakeAllFlag(true);
        req.setActionFlag(true);
        req.setBalanceType(CommonConstant.business_two_str);
        log.info("收到{}-轉出玩家『部分』額度至遊戲 消息:{}", VenueEnum.CQ9.getVenueName(), req.getAccount());
        JSONObject cq9StatusRsp = cq9GameApi.takeall(req);
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("收到{}-轉出玩家『部分』額度至遊戲,执行了:{}秒, 消息:{}", VenueEnum.CQ9.getVenueName(), durationSeconds, JSON.toJSONString(cq9StatusRsp));
        return cq9StatusRsp;
    }
    @Operation(summary = "針對『已完成』的訂單做扣款")
    @PostMapping("/transaction/game/debit")
    public JSONObject debit(CQ9BetReq req, @RequestHeader("wtoken") String wtoken) {
        long startTime = System.currentTimeMillis();
        req.setWtoken(wtoken);
        req.setCallType(CQ9CallEnums.DEBIT.getCode());
        log.info("收到{}-針對『已完成』的訂單做扣款 消息:{}", VenueEnum.CQ9.getVenueName(), req.getAccount());
        req.setType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
        req.setBalanceType(CommonConstant.business_two_str);
        // 补扣款也不限制
        req.setActionFlag(false);
        JSONObject cq9StatusRsp = cq9GameApi.bet(req);
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("收到{}-針對『已完成』的訂單做扣款,执行了:{}秒, 消息:{}", VenueEnum.CQ9.getVenueName(), durationSeconds, JSON.toJSONString(cq9StatusRsp));
        return cq9StatusRsp;
    }

    @Operation(summary = "針對『已完成』的訂單做補款。例如，遊戲邏輯錯誤進行修正")
    @PostMapping("/transaction/game/credit")
    public JSONObject credit(CQ9BetReq req, @RequestHeader("wtoken") String wtoken) {
        long startTime = System.currentTimeMillis();
        req.setWtoken(wtoken);
        req.setCallType(CQ9CallEnums.CREDIT.getCode());
        log.info("收到{}-針對『已完成』的訂單做補款 消息:{}", VenueEnum.CQ9.getVenueName(), req.getAccount());
        req.setType(WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode());
        req.setBalanceType(CommonConstant.business_one_str);
        // 補款也不限制
        req.setActionFlag(false);
        JSONObject cq9StatusRsp = cq9GameApi.bet(req);
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        log.info("收到{}-針對『已完成』的訂單做補款,执行了:{}秒, 消息:{}", VenueEnum.CQ9.getVenueName(), durationSeconds, JSON.toJSONString(cq9StatusRsp));
        return cq9StatusRsp;
    }

    @Operation(summary = "貴司如有參與我方舉辦之活動，活動獎勵透過此支API派發給玩家")
    @PostMapping("/transaction/user/payoff")
    public JSONObject payoff(CQ9PayoffReq req, @RequestHeader("wtoken") String wtoken) {
        long startTime = System.currentTimeMillis();
        req.setWtoken(wtoken);
        req.setCallType(CQ9CallEnums.PAYOFF.getCode());
        log.info("收到{}-活動獎勵 消息:{}", VenueEnum.CQ9.getVenueName(), req.getAccount());
        req.setType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        req.setBalanceType(CommonConstant.business_one_str);
        // 補款也不限制
        req.setActionFlag(false);
        JSONObject cq9StatusRsp = cq9GameApi.payoff(req);
        long durationSeconds = (System.currentTimeMillis() - startTime) / 1000;
        log.info("收到{}-活動獎勵,执行了:{}秒, 消息:{}", VenueEnum.CQ9.getVenueName(), durationSeconds, JSON.toJSONString(cq9StatusRsp));
        return cq9StatusRsp;
    }

    @Operation(summary = "退回 bet/rollout/takeall 金額")
    @PostMapping("/transaction/game/refund")
    public JSONObject refund(CQ9BetReq req, @RequestHeader("wtoken") String wtoken) {
        long startTime = System.currentTimeMillis();
        req.setWtoken(wtoken);
        req.setCallType(CQ9CallEnums.REFUND.getCode());
        log.info("收到{}-活動獎勵 消息:{}", VenueEnum.CQ9.getVenueName(), req.getAccount());
        req.setType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
        req.setBalanceType(CommonConstant.business_one_str);
        // 退回也不限制
        req.setActionFlag(false);
        JSONObject cq9StatusRsp = cq9GameApi.refund(req);
        log.info("收到{}-活動獎勵,执行了:{}秒, 消息:{}", VenueEnum.CQ9.getVenueName(), (System.currentTimeMillis() - startTime) / 1000, JSON.toJSONString(cq9StatusRsp));
        return cq9StatusRsp;
    }
    @Operation(summary = "錢包交易紀錄查詢 ")
    @GetMapping("/transaction/record/{mtcode}")
    public JSONObject record(@PathVariable("mtcode") String mtcode, @RequestHeader("wtoken") String wtoken) {
        long startTime = System.currentTimeMillis();
        log.info("收到{}-錢包交易紀錄查詢  消息:{}", VenueEnum.CQ9.getVenueName(), mtcode);
        JSONObject cq9StatusRsp = cq9GameApi.record(mtcode, wtoken);
        long durationSeconds = (System.currentTimeMillis() - startTime) / 1000;
        log.info("收到{}-錢包交易紀錄查詢 ,执行了:{}秒, 消息:{}", VenueEnum.CQ9.getVenueName(), durationSeconds, JSON.toJSONString(cq9StatusRsp));
        return cq9StatusRsp;
    }



    @Operation(hidden = true)
    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST})
    public JSONObject handleInvalidCq9Path(HttpServletRequest request) {
        log.warn("未匹配到CQ9接口路径: {}", request.getRequestURI());
        JSONObject status = new JSONObject();
        status.put("code", "1002");
        status.put("datetime", getNowTimeRF33());
        status.put("message", "Method not allow");

        JSONObject response = new JSONObject();
        response.put("data", null);

        response.put("status", status);
        return response;
    }
    public static final ZoneId gmt4ZoneId = ZoneId.of("Etc/GMT+4");
    /**
     * 将 Date 转为固定 UTC-4（Etc/GMT+4）时区的 RFC3339 格式字符串
     */
    public static String getNowTimeRF33() {
        // 获取 UTC-4 时区对应的 Date 对象（你已有的方法）
        Date canadaCurrentDate = TimeZoneUtils.getCurrentDate4ZoneId(gmt4ZoneId);

        // 将 Date 转为 ZonedDateTime，指定为固定 UTC-4 时区
        ZonedDateTime zonedTime = canadaCurrentDate.toInstant().atZone(gmt4ZoneId);

        // 格式化为 RFC3339 格式
        return zonedTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }


}

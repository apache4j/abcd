package com.cloud.baowang.play.wallet.controller;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.play.api.api.third.PPGameApi;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;


@LogExecution
@Slf4j
@RestController
@RequestMapping("callback/pp")
@Tag(name = "PP电子单一钱包")
@AllArgsConstructor
public class PPController {

    private PPGameApi ppGameApi;

    /**
     * Retrieve user's latest balance.
     *  String hash;
     *     String providerId;
     *     String userId;
     */
    @Operation(summary = "身份验证")
    @PostMapping("/Authenticate")
    public Object authenticate(
            @Parameter(name = "hash", description = "请求的哈希代码") String hash,
            @Parameter(name = "providerId", description = "游戏供应商 ID") String providerId,
            @Parameter(name = "userId", description = "用户标识符") String userId,
                               @RequestParam   Map<String, Object> vo) {
        log.info("pp Authenticate param: {}",vo);

        Object authenticate = ppGameApi.authenticate(new JSONObject(vo));

        log.info("pp Authenticate back: {}",authenticate);

        return authenticate;

    }

    /**
     *
     * String hash;
 *     String providerId;
 *     String userId;
     */

    @Operation(summary = "查詢余额")
    @PostMapping("/Balance")
    public Object balance(
            @Parameter(name = "hash", description = "请求的哈希代码") String hash,
            @Parameter(name = "providerId", description = "游戏供应商 ID") String providerId,
            @Parameter(name = "userId", description = "用户标识符") String userId,
            @RequestParam Map<String, Object> vo) {
        log.info("pp Balance param: {}",vo);
        Object authenticate = ppGameApi.balance(new JSONObject(vo));

        log.info("pp Balance back: {}",authenticate);

        return authenticate;
    }

    /**
     *
     *     String hash;
     *     String userId;
     *     String gameId;
     *     String roundId;
     *     BigDecimal amount;
     *     String reference;
     *     String providerId;
     *
     *     String timestamp;
     *     String roundDetails;
     *     String bonusCode;
     */
    @Operation(summary = "支付赌注")
    @PostMapping("/Bet")
    public Object bet(
            @Parameter(name = "hash", description = "请求的哈希代码") String hash,
            @Parameter(name = "providerId", description = "游戏供应商 ID") String providerId,
            @Parameter(name = "userId", description = "用户标识符") String userId,
            @Parameter(name = "gameId" ,description = "游戏的 ID") String gameId,
            @Parameter(name = "roundId", description = "回合ID") String roundId,
            @Parameter(name = "amount" , description = "赌注金额") BigDecimal amount,
            @Parameter(name = "timestamp" , description = "处理交易的日期和时间") String timestamp,
            @Parameter(name = "roundDetails" , description = "游戏回合的其他信息") String roundDetails,
            @Parameter(name = "bonusCode" , description = "娱乐场运营商系统中的奖励 ID") String bonusCode,
            @RequestParam Map<String, Object> vo) {
        log.info("pp Bet param: {}",vo);
        Object authenticate = ppGameApi.bet(new JSONObject(vo));

        log.info("pp Bet back: {}",authenticate);

        return authenticate;
    }

    @Operation(summary = "赌注的赢奖结果")
    @PostMapping("/Result")
    public Object result(@Parameter(name = "hash", description = "请求的哈希代码") String hash,
                         @Parameter(name = "providerId", description = "游戏供应商 ID") String providerId,
                         @Parameter(name = "userId", description = "用户标识符") String userId,
                         @Parameter(name = "gameId" ,description = "游戏的 ID") String gameId,
                         @Parameter(name = "roundId", description = "回合ID") String roundId,
                         @Parameter(name = "amount" , description = "赌注金额") BigDecimal amount,
                         @Parameter(name = "reference" , description = "此交易的唯一参考") String reference,
                         @Parameter(name = "timestamp" , description = "处理交易的日期和时间") String timestamp,
                         @Parameter(name = "roundDetails" , description = "游戏回合的其他信息") String roundDetails,
                         @Parameter(name = "bonusCode" , description = "娱乐场运营商系统中的奖励 ID") String bonusCode,
                         @RequestParam Map<String, Object> vo) {
        log.info("pp Result param: {}",vo);

        Object authenticate = ppGameApi.result(new JSONObject(vo));

        log.info("pp Result back: {}",authenticate);

        return authenticate;
    }

    @Operation(summary = "免费旋转奖励")
    @PostMapping("/Bonus")
    public Object bonusWin(@Parameter(name = "hash", description = "请求的哈希代码") String hash,
                           @Parameter(name = "userId", description = "用户标识符") String userId,
                           @Parameter(name = "amount" , description = "赌注金额") BigDecimal amount,
                           @Parameter(name = "reference" , description = "此交易的唯一参考") String reference,
                           @Parameter(name = "providerId", description = "游戏供应商 ID") String providerId,
                           @Parameter(name = "timestamp" , description = "处理交易的日期和时间") String timestamp,
                           @Parameter(name = "bonusCode" , description = "娱乐场运营商系统中的奖励ID") String bonusCode,
                           @Parameter(name = "roundId", description = "回合ID") String roundId,
                           @Parameter(name = "gameId" ,description = "游戏的 ID") String gameId,
                           @RequestParam Map<String, Object> vo) {
        log.info("pp BonusWin param: {}",vo);

        Object authenticate = ppGameApi.bonusWin(new JSONObject(vo));

        log.info("pp BonusWin back: {}",authenticate);

        return authenticate;
    }

    @Operation(summary = "中奖的信息")
    @PostMapping("/JackpotWin")
    public Object JackpotWin(@Parameter(name = "hash", description = "请求的哈希代码") String hash,
                             @Parameter(name = "providerId", description = "游戏供应商 ID") String providerId,
                             @Parameter(name = "timestamp" , description = "处理交易的日期和时间") String timestamp,
                             @Parameter(name = "userId", description = "用户标识符") String userId,
                             @Parameter(name = "gameId" ,description = "游戏的 ID") String gameId,
                             @Parameter(name = "roundId", description = "回合ID") String roundId,
                             @Parameter(name = "jackpotId" , description = "累积奖金的 ID") String jackpotId,
                             @Parameter(name = "jackpotDetails" , description = "本轮赢得大奖的详细信息") String jackpotDetails,
                             @Parameter(name = "amount" , description = "赌注金额") BigDecimal amount,
                             @Parameter(name = "reference" , description = "此交易的唯一参考") String reference,
                             @RequestParam Map<String, Object> vo) {
        log.info("pp JackpotWin param: {}",vo);

        Object authenticate = ppGameApi.jackpotWin(new JSONObject(vo));

        log.info("pp JackpotWin back: {}",authenticate);

        return authenticate;
    }

    @Operation(summary = "实时结束游戏回合的交易")
    @PostMapping("/EndRound")
    public Object endRound(@Parameter(description = "实时结束游戏回合的交易, 业务不实现") Map<String, Object> vo) {
        log.info("pp EndRound param: {}",vo);

        Object authenticate = ppGameApi.endRound(new JSONObject(vo));

        log.info("pp EndRound back: {}",authenticate);

        return authenticate;
    }

    @Operation(summary = "退回赌场运营商方的投注交易")
    @PostMapping("/Refund")
    public Object refund(
            @Parameter(name = "hash", description = "请求的哈希代码") String hash,
            @Parameter(name = "userId", description = "用户标识符") String userId,
            @Parameter(name = "providerId", description = "游戏供应商 ID") String providerId,
            @Parameter(name = "gameId" ,description = "游戏的 ID") String gameId,
            @Parameter(name = "roundId", description = "回合ID") String roundId,
            @Parameter(name = "amount" , description = "赌注金额") BigDecimal amount,
            @Parameter(name = "reference" , description = "此交易的唯一参考") String reference,
            @Parameter(name = "timestamp" , description = "处理交易的日期和时间") String timestamp,
            @Parameter(name = "roundDetails" , description = "游戏回合的其他信息") String roundDetails,
            @Parameter(name = "bonusCode" , description = "娱乐场运营商系统中的奖励 ID") String bonusCode,
            @RequestParam Map<String, Object> vo) {

        log.info("pp Refund param: {}",vo);
        Object authenticate = ppGameApi.refund(new JSONObject(vo));

        log.info("pp Refund back: {}",authenticate);

        return authenticate;
    }

    /**
     *
     *      String hash;
     *     String providerId;
     *     String timestamp;
     *     String userId;
     *
     *     String campaignId;
     *
     *     String campaignType;
     *     BigDecimal amount;
     *     String currency;
     *     String reference;
     *
     *     String roundId;
     *
     *     String gameId;
     *
     *     String dataType;
     */
    @Operation(summary = "最终获得的奖励")
    @PostMapping("/PromoWin")
    public Object promoWin(@Parameter(name = "hash", description = "请求的哈希代码") String hash,
                           @Parameter(name = "providerId", description = "游戏供应商 ID") String providerId,
                           @Parameter(name = "timestamp" , description = "处理交易的日期和时间") String timestamp,
                           @Parameter(name = "userId", description = "用户标识符") String userId,
                           @Parameter(name = "campaignId" , description = "活动 ID") String campaignId,
                           @Parameter(name = "campaignType" , description = "营销活动的类型") String campaignType,
                           @Parameter(name = "amount" , description = "赌注金额") BigDecimal amount,
                           @Parameter(name = "currency" , description = "玩家的货币") String currency,
                           @Parameter(name = "reference" , description = "此交易的唯一参考") String reference,
                           @Parameter(name = "roundId", description = "回合ID") String roundId,
                           @Parameter(name = "gameId" ,description = "游戏的 ID") String gameId,
                           @Parameter(name = "dataType" , description = "促销活动的投资组合类型（可选）") String dataType,
                           @RequestParam Map<String, Object> vo) {
        log.info("pp PromoWin param: {}",vo);
        Object authenticate = ppGameApi.promoWin(new JSONObject(vo));

        log.info("pp PromoWin back: {}",authenticate);

        return authenticate;
    }

    @Operation(summary = "需要调整的余额金额")
    @PostMapping("/Adjustment")
    public Object adjustment(@Parameter(name = "hash", description = "请求的哈希代码") String hash,
                             @Parameter(name = "userId", description = "用户标识符") String userId,
                             @Parameter(name = "gameId" ,description = "游戏的 ID") String gameId,
                             @Parameter(name = "token" ,description = "token") String token,
                             @Parameter(name = "roundId", description = "回合ID") String roundId,
                             @Parameter(name = "amount" , description = "赌注金额") BigDecimal amount,
                             @Parameter(name = "reference" , description = "此交易的唯一参考") String reference,
                             @Parameter(name = "providerId", description = "游戏供应商 ID") String providerId,
                             @Parameter(name = "timestamp" , description = "处理交易的日期和时间") String timestamp,
                             @Parameter(name = "validBetAmount" , description = "有效投注金额") String validBetAmount,
                             @RequestParam Map<String, Object> vo) {
        log.info("pp Adjustment: {}",vo);
        Object authenticate = ppGameApi.adjustment(new JSONObject(vo));

        log.info("pp Adjustment back: {}",authenticate);

        return authenticate;
    }


}

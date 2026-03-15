package com.cloud.baowang.play.api.api.third;


import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.pp.PPBaseResVO;
import com.cloud.baowang.play.api.vo.pp.req.PPFreeRoundCancelReqVO;
import com.cloud.baowang.play.api.vo.pp.req.PPFreeRoundGetReqVO;
import com.cloud.baowang.play.api.vo.pp.req.PPFreeRoundGiveReqVO;
import com.cloud.baowang.play.api.vo.pp.req.PPGameLimitReqVO;
import com.cloud.baowang.play.api.vo.pp.res.PPFreeRoundResVO;
import com.cloud.baowang.play.api.vo.pp.res.PPGameLimitResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(contextId = "pp-api", value = ApiConstants.NAME)
@Tag(name = "pp-游戏")
public interface PPGameApi {

    String PREFIX = ApiConstants.PREFIX + "/pp/api";

    @Operation(summary = "身份验证")
    @PostMapping(PREFIX + "/authenticate")
    Object authenticate(@RequestBody JSONObject vo);

    //NOTE 3.5 Balance
    @Operation(summary = "查詢余额")
    @PostMapping(PREFIX + "/balance")
    Object balance(@RequestBody JSONObject vo);

    //NOTE 3.6 Bet
    @Operation(summary = "支付赌注")
    @PostMapping(PREFIX + "/bet")
    Object bet(@RequestBody JSONObject vo);

    //NOTE 3.7 Result
    @Operation(summary = "赌注的赢奖结果")
    @PostMapping(PREFIX + "/result")
    Object result(@RequestBody JSONObject vo);

    //NOTE 3.8 BonusWin
    @Operation(summary = "免费旋转奖励")
    @PostMapping(PREFIX + "/bonusWin")
    Object bonusWin(@RequestBody JSONObject vo);

    //NOTE 3.9 JackpotWin
    @Operation(summary = "中奖的信息")
    @PostMapping(PREFIX + "/jackpotWin")
    Object jackpotWin(@RequestBody JSONObject vo);

    //NOTE 3.10 EndRound
    @Operation(summary = "实时结束游戏回合的交易")
    @PostMapping(PREFIX + "/endRound")
    Object endRound(@RequestBody JSONObject vo);

    //NOTE 3.11 Refund
    @Operation(summary = "退回赌场运营商方的投注交易")
    @PostMapping(PREFIX + "/refund")
    Object refund(@RequestBody JSONObject vo);

    //NOTE 3.13 Refund
    @Operation(summary = "最终获得的奖励")
    @PostMapping(PREFIX + "/promoWin")
    Object promoWin(@RequestBody JSONObject vo);

    //NOTE 3.15 Adjustment
    @Operation(summary = "需要调整的余额金额")
    @PostMapping(PREFIX + "/adjustment")
    Object adjustment(@RequestBody JSONObject vo);

    @Operation(summary = "给玩家增加免费旋转次数")
    @PostMapping(PREFIX + "/giveFRB")
    ResponseVO<Boolean> giveFRB( @RequestBody PPFreeRoundGiveReqVO req);

    @Operation(summary = "取消玩家免费旋转次数")
    @PostMapping(PREFIX + "/cancelFRB")
    ResponseVO<Boolean> cancelFRB(@RequestBody PPFreeRoundCancelReqVO req);

    @Operation(summary = "查询玩家免费旋转详情")
    @PostMapping(PREFIX + "/getPlayersFRB")
    ResponseVO<List<PPFreeRoundResVO>> getPlayersFRB(@RequestBody PPFreeRoundGetReqVO req);

    @Operation(summary = "获得赌注范围")
    @PostMapping(PREFIX + "/getLimitGameLine")
    ResponseVO<List<PPGameLimitResVO>> getLimitGameLine(@RequestBody PPGameLimitReqVO req);

}
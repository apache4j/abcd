package com.cloud.baowang.play.api.api.third;


import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.jili.req.*;
import com.cloud.baowang.play.api.vo.zf.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 *   JILI Latest Version Interface
 *
 *   https://<operator_site>/wallet/balance         Retrieve user's latest balance.
 *   https://<operator_site>/wallet/bet             A bet transaction to deduct amount from the wallet balance.
 *   https://<operator_site>/wallet/bet_result      A bet transaction request to add and/or deduct funds from the user balance.
 *   https://<operator_site>/wallet/rollback        A rollback action on a bet transaction
 *   https://<operator_site>/wallet/adjustment      An adjustment on the win amount of a game round.
 *   https://<operator_site>/wallet/bet_debit       Debit: Deduct Balance for Game Room Entry
 *   https://<operator_site>/wallet/bet_credit      Credit: Settle Bet and Update Balance
 *
 */

@FeignClient(contextId = "jili-api", value = ApiConstants.NAME)
@Tag(name = "JILI-游戏")
public interface JILIGameApi {

    String PREFIX = ApiConstants.PREFIX + "/jili/api";

    @Operation(summary = "查詢余额")
    @PostMapping(PREFIX + "/wallet/balance")
    JSONObject balance(@RequestBody JILIBalanceReq jiliBalanceReq);

    @Operation(summary = "投注")
    @PostMapping(PREFIX + "/wallet/bet")
    JSONObject bet(@RequestBody JILIBetReq jiliBetReq);


    @Operation(summary = "查詢投注结果")
    @PostMapping(PREFIX + "/wallet/bet_result")
    JSONObject betResult(@RequestBody JILIBetResultReq jiliBetResultReq);

    @Operation(summary = "回滚")
    @PostMapping(PREFIX + "/wallet/rollback")
    JSONObject rollback(@RequestBody JILIRollbackReq jiliRollbackReq);

    @Operation(summary = "结果调整")
    @PostMapping(PREFIX + "/wallet/adjustment")
    JSONObject adjustment(@RequestBody JILIAdjustmentReq jiliAdjustmentReq);

    @Operation(summary = "扣除余额")
    @PostMapping(PREFIX + "/wallet/bet_debit")
    JSONObject betDebit(@RequestBody JILIDebitReq jiliDebitReq);

    @Operation(summary = "结算赌注和更新余额")
    @PostMapping(PREFIX + "/wallet/bet_credit")
    JSONObject betCredit(@RequestBody JILICreditReq jiliCreditReq);











    @PostMapping(PREFIX + "/zfAuth")
    ZfResp zfAuth(@RequestBody ZfAuthReq req);

    @PostMapping(PREFIX + "/zfBet")
    ZfBetResp zfBet(@RequestBody ZfBetReq req);

    @PostMapping(PREFIX + "/zfCancelBet")
    ZfCancelBetResp zfCancelBet(@RequestBody ZfCancelBetReq req);

    @PostMapping(PREFIX + "/zfSessionBet")
    ZfBetResp zfSessionBet(@RequestBody ZfSessionBetReq req);

    @PostMapping(PREFIX + "/zfCancelSessionBet")
    ZfCancelBetResp zfCancelSessionBet(@RequestBody ZfCancelSessionBetReq req);


}

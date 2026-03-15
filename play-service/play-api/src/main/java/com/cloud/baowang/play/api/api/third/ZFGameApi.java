package com.cloud.baowang.play.api.api.third;


import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.zf.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

@FeignClient(contextId = "zf-api", value = ApiConstants.NAME)
@Tag(name = "zf-游戏")
public interface ZFGameApi {

    String PREFIX = ApiConstants.PREFIX + "/zf/api";

    @PostMapping(PREFIX + "/auth")
    ZfResp auth(@RequestBody ZfAuthReq req);

    @PostMapping(PREFIX + "/bet")
    ZfBetResp bet(@RequestBody ZfBetReq req);

    @PostMapping(PREFIX + "/cancelBet")
    ZfCancelBetResp cancelBet(@RequestBody ZfCancelBetReq req);

    @PostMapping(PREFIX + "/sessionBet")
    ZfBetResp sessionBet(@RequestBody ZfSessionBetReq req);

    @PostMapping(PREFIX + "/cancelSessionBet")
    ZfCancelBetResp cancelSessionBet(@RequestBody ZfCancelSessionBetReq req);


}

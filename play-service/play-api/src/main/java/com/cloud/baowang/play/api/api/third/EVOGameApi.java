package com.cloud.baowang.play.api.api.third;


import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.evo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "evo-api", value = ApiConstants.NAME)
@Tag(name = "evo-游戏")
public interface EVOGameApi {

    String PREFIX = ApiConstants.PREFIX + "/evo/api";

    @PostMapping(PREFIX +"/balance")
    BalanceResponse balance(@RequestBody BalanceRequest request);

    @PostMapping(PREFIX +"/check")
    CheckUserResponse check(@RequestBody CheckUserRequest request);

    @PostMapping(PREFIX +"/sid")
    CheckUserResponse sid(@RequestBody CheckUserRequest request);

    @PostMapping(PREFIX +"/debit")
    BalanceResponse debit(@RequestBody DebitRequest request);

    @PostMapping(PREFIX +"/credit")
    BalanceResponse credit(@RequestBody CreditRequest request);

    @Operation(summary = "jackpot派奖")
    @PostMapping(PREFIX +"/cancel")
    BalanceResponse cancel(@RequestBody CancelRequest request);

    @Operation(summary = "jackpot派奖")
    @PostMapping(PREFIX +"/promoPayout")
    BalanceResponse promoPayout(@RequestBody PromoPayoutRequest request);

}
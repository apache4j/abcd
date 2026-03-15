package com.cloud.baowang.play.api.third;

import com.cloud.baowang.play.api.api.third.EVOGameApi;
import com.cloud.baowang.play.api.vo.evo.*;
import com.cloud.baowang.play.game.evo.impl.EvoServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
public class EVOGameApiImpl implements EVOGameApi {

    private final EvoServiceImpl evoService;


    @Override
    public BalanceResponse balance(BalanceRequest request) {
        return evoService.balance(request);
    }

    @Override
    public CheckUserResponse check(CheckUserRequest request) {
        return evoService.check(request);
    }

    @Override
    public CheckUserResponse sid(CheckUserRequest request) {
        return evoService.sid(request);
    }

    @Override
    public BalanceResponse debit(DebitRequest request) {
        return evoService.debit(request);
    }

    @Override
    public BalanceResponse credit(CreditRequest request) {
        return evoService.credit(request);
    }

    @Override
    public BalanceResponse cancel(CancelRequest request) {
        return evoService.cancel(request);
    }

    @Override
    public BalanceResponse promoPayout(PromoPayoutRequest request) {
        return evoService.promoPayout(request);
    }
}

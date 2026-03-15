package com.cloud.baowang.play.api.third;

import com.cloud.baowang.play.api.api.third.S128GameApi;
import com.cloud.baowang.play.api.api.third.SHGameApi;
import com.cloud.baowang.play.api.vo.base.ShBaseRes;
import com.cloud.baowang.play.api.vo.s128.*;
import com.cloud.baowang.play.api.vo.sh.ShAdjustBalanceReq;
import com.cloud.baowang.play.api.vo.sh.ShAdjustBalanceRes;
import com.cloud.baowang.play.api.vo.sh.ShBalanceRes;
import com.cloud.baowang.play.api.vo.sh.ShQueryBalanceReq;
import com.cloud.baowang.play.game.s128.impl.S128GameServiceImpl;
import com.cloud.baowang.play.game.sh.impl.ShGameServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
public class S128GameApiImpl implements S128GameApi {

    private final S128GameServiceImpl s128GameService;

    @Override
    public GetBalanceRes getBalance(GetBalanceReq req) {
        return s128GameService.getBalance(req);
    }

    @Override
    public BetRes bet(BetReq req) {
        return s128GameService.bet(req);
    }

    @Override
    public CancelBetRes cancelBet(CancelBetReq req) {
        return s128GameService.cancelBet(req);
    }
}

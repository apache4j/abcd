package com.cloud.baowang.play.api.third;

import com.cloud.baowang.play.api.api.third.FCGameApi;
import com.cloud.baowang.play.api.api.third.FastSpinGameApi;
import com.cloud.baowang.play.api.vo.fastSpin.req.FSBalanceReq;
import com.cloud.baowang.play.api.vo.fastSpin.req.FSTransferReq;
import com.cloud.baowang.play.api.vo.fc.req.FCBaseReq;
import com.cloud.baowang.play.api.vo.fc.req.GetBalanceReq;
import com.cloud.baowang.play.game.fastSpin.impl.FastSpinGamServiceImpl;
import com.cloud.baowang.play.game.fc.impl.FCGamServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@AllArgsConstructor
@Slf4j
@RestController
public class FCGameApiImpl implements FCGameApi {

    private final FCGamServiceImpl fcGamService;


    @Override
    public Object GetBalance(FCBaseReq vo) {
        return fcGamService.GetBalance(vo);
    }

    @Override
    public Object BetNInfo(FCBaseReq vo) {
        return fcGamService.BetNInfo(vo);
    }

    @Override
    public Object CancelBetNInfo(FCBaseReq vo) {
        return fcGamService.CancelBetNInfo(vo);
    }

    @Override
    public Object Bet(FCBaseReq vo) {
        return fcGamService.Bet(vo);
    }

    @Override
    public Object Settle(FCBaseReq vo) {
        return fcGamService.Settle(vo);
    }

    @Override
    public Object CancelBet(FCBaseReq vo) {
        return fcGamService.CancelBet(vo);
    }

    @Override
    public Object EventSettle(FCBaseReq vo) {
        return fcGamService.EventSettle(vo);
    }

    @Override
    public Object FreeSpinBetNInfo(FCBaseReq vo) {
        return fcGamService.FreeSpinBetNInfo(vo);
    }
}

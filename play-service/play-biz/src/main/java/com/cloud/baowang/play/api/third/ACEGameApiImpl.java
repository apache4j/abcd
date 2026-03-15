package com.cloud.baowang.play.api.third;

import com.cloud.baowang.play.api.api.third.ACEGameApi;
import com.cloud.baowang.play.api.vo.ace.req.*;
import com.cloud.baowang.play.api.vo.ace.res.ACEAuthenticateRes;
import com.cloud.baowang.play.api.vo.ace.res.ACEBaseRes;
import com.cloud.baowang.play.game.ace.impl.ACEGameServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
public class ACEGameApiImpl implements ACEGameApi {

    private final ACEGameServiceImpl aceGameService;


    @Override
    public Object authenticate(ACEAuthenticateReq req) {
        return aceGameService.authenticate(req);
    }

    @Override
    public Object getbalance(ACEGetbalanceReq req) {
        return aceGameService.getbalance(req);
    }

    @Override
    public Object bet(ACEBetReq req) {
        return aceGameService.bet(req);
    }

    @Override
    public Object betresult(ACEBetresultReq req) {
        return aceGameService.betresult(req);
    }

    @Override
    public Object refund(ACERefundReq req) {
        return aceGameService.refund(req);
    }

    @Override
    public Object jackpotwin(ACEJackpotwinReq req) {
        return aceGameService.jackpotwin(req);
    }
}

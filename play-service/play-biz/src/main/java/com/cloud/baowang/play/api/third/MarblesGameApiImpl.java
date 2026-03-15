package com.cloud.baowang.play.api.third;

import com.cloud.baowang.play.api.api.third.MarblesGameApi;
import com.cloud.baowang.play.api.vo.marbles.*;
import com.cloud.baowang.play.game.im.impl.marbles.MarblesGameService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
public class MarblesGameApiImpl implements MarblesGameApi {

    private final MarblesGameService marblesGameService;

    @Override
    public MarblesBalanceResp getBalance(MarblesReq req) {
        return marblesGameService.getBalance(req);
    }

    @Override
    public MarblesResp getApproval(MarblesApprovalReq req) {
        return marblesGameService.getApproval(req);
    }

    @Override
    public MarblesPlaceBetResp placeBet(MarblesPlaceBetReq req) {
        return marblesGameService.placeBet(req);
    }

    @Override
    public MarblesRefundResp settleBet(MarblesSettleBetReq req) {
        return marblesGameService.settleBet(req);
    }

    @Override
    public MarblesRefundResp refund(MarblesRefundReq req) {
        return marblesGameService.refund(req);
    }
}

package com.cloud.baowang.play.api.third;

import com.cloud.baowang.play.api.api.third.PT2GameApi;
import com.cloud.baowang.play.api.api.third.PgGameApi;
import com.cloud.baowang.play.api.vo.pg.req.PgAdjustmentReq;
import com.cloud.baowang.play.api.vo.pg.req.PgBaseReq;
import com.cloud.baowang.play.api.vo.pg.req.PgBetReq;
import com.cloud.baowang.play.api.vo.pg.req.VerifySessionReq;
import com.cloud.baowang.play.api.vo.pg.rsp.*;
import com.cloud.baowang.play.api.vo.pt2.PT2ActionVO;
import com.cloud.baowang.play.api.vo.pt2.PT2BaseVO;
import com.cloud.baowang.play.api.vo.pt2.vo.rps.PT2BaseRsp;
import com.cloud.baowang.play.api.vo.pt2.vo.settle.GameRoundResultVO;
import com.cloud.baowang.play.api.vo.pt2.vo.settle.TransferFundsVO;
import com.cloud.baowang.play.game.pg.impl.PgServiceImpl;
import com.cloud.baowang.play.game.playtech.PlayTechServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
public class PT2GameApiImpl implements PT2GameApi {

    private final PlayTechServiceImpl playTechService;


    @Override
    public PT2BaseRsp authenticate(PT2ActionVO actionVo) {
        return playTechService.authenticate(actionVo);
    }

    @Override
    public PT2BaseRsp bet(PT2ActionVO actionVo) {
        return playTechService.bet(actionVo);
    }

    @Override
    public PT2BaseRsp gameroundresult(GameRoundResultVO actionVo) {
        return playTechService.gameroundresult(actionVo);
    }

    @Override
    public PT2BaseRsp getbalance(PT2BaseVO actionVo) {
        return playTechService.getbalance(actionVo);
    }

    @Override
    public PT2BaseRsp logout(PT2BaseVO actionVo) {
        return playTechService.logout(actionVo);
    }

    @Override
    public PT2BaseRsp transferFunds(TransferFundsVO actionVo) {
        return playTechService.transferFunds(actionVo);
    }
}

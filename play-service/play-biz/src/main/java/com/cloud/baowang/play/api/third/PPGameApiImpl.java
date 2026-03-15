package com.cloud.baowang.play.api.third;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.third.PPGameApi;
import com.cloud.baowang.play.api.vo.pp.PPBaseReqVO;
import com.cloud.baowang.play.api.vo.pp.PPBaseResVO;
import com.cloud.baowang.play.api.vo.pp.req.PPFreeRoundCancelReqVO;
import com.cloud.baowang.play.api.vo.pp.req.PPFreeRoundGetReqVO;
import com.cloud.baowang.play.api.vo.pp.req.PPFreeRoundGiveReqVO;
import com.cloud.baowang.play.api.vo.pp.req.PPGameLimitReqVO;
import com.cloud.baowang.play.api.vo.pp.res.*;
import com.cloud.baowang.play.game.pp.impl.PPGameServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@Slf4j
@RestController
public class PPGameApiImpl implements PPGameApi {

    private final PPGameServiceImpl ppGameService;

    @Override
    public Object authenticate(JSONObject vo) {
        return ppGameService.authenticate(vo);
    }

    @Override
    public Object balance(JSONObject vo) {
        return ppGameService.balance(vo);
    }

    @Override
    public Object bet(JSONObject vo) {
        return ppGameService.bet(vo);
    }

    @Override
    public Object result(JSONObject vo) {
        return ppGameService.result(vo);
    }

    @Override
    public Object bonusWin(JSONObject vo) {
        return ppGameService.bonusWin(vo);
    }

    @Override
    public Object jackpotWin(JSONObject vo) {
        return ppGameService.jackpotWin(vo);
    }

    @Override
    public Object endRound(JSONObject vo) {
        return ppGameService.endRound(vo);
    }

    @Override
    public Object refund(JSONObject vo) {
        return ppGameService.refund(vo);
    }

    @Override
    public Object promoWin(JSONObject vo) {
        return ppGameService.promoWin(vo);
    }

    @Override
    public Object adjustment(JSONObject vo) {
        return ppGameService.adjustment(vo);
    }

    @Override
    public ResponseVO<Boolean> giveFRB(PPFreeRoundGiveReqVO req) {
        return ppGameService.giveFRB(req);
    }

    @Override
    public ResponseVO<Boolean> cancelFRB(PPFreeRoundCancelReqVO req) {
        return ppGameService.cancelFRB(req);
    }

    @Override
    public ResponseVO<List<PPFreeRoundResVO>> getPlayersFRB(PPFreeRoundGetReqVO req) {
        return ppGameService.getPlayersFRB(req);
    }

    @Override
    public ResponseVO<List<PPGameLimitResVO>> getLimitGameLine(PPGameLimitReqVO req) {
        return ppGameService.getLimitGameLine(req);
    }
}

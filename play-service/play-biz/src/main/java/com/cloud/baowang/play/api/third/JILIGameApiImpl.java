package com.cloud.baowang.play.api.third;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.third.JILIGameApi;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.vo.jili.JILIBaseRes;
import com.cloud.baowang.play.api.vo.jili.req.*;
import com.cloud.baowang.play.api.vo.zf.*;
import com.cloud.baowang.play.game.zf.jili.impl.JILIGameThreeServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
public class JILIGameApiImpl implements JILIGameApi {


    private final JILIGameThreeServiceImpl jiliGameThreeService;

    @Override
    public JSONObject balance(JILIBalanceReq req) {
        JILIBaseRes res = jiliGameThreeService.balance(req);
        return JSON.parseObject(JSON.toJSONString(res));
    }

    @Override
    public JSONObject bet(JILIBetReq req) {
        JILIBaseRes res = jiliGameThreeService.bet(req);
        return JSON.parseObject(JSON.toJSONString(res));
    }

    @Override
    public JSONObject betResult(JILIBetResultReq req) {
        JILIBaseRes res = jiliGameThreeService.betResult(req);
        return JSON.parseObject(JSON.toJSONString(res));
    }

    @Override
    public JSONObject rollback(JILIRollbackReq req) {
        JILIBaseRes res = jiliGameThreeService.rollback(req);
        return JSON.parseObject(JSON.toJSONString(res));
    }

    @Override
    public JSONObject adjustment(JILIAdjustmentReq req) {
        JILIBaseRes res = jiliGameThreeService.adjustment(req);
        return JSON.parseObject(JSON.toJSONString(res));
    }

    @Override
    public JSONObject betDebit(JILIDebitReq req) {
        JILIBaseRes res = jiliGameThreeService.betDebit(req);
        return JSON.parseObject(JSON.toJSONString(res));
    }

    @Override
    public JSONObject betCredit(JILICreditReq req) {
        JILIBaseRes res = jiliGameThreeService.betCredit(req);
        return JSON.parseObject(JSON.toJSONString(res));
    }

    @Override
    public ZfResp zfAuth(ZfAuthReq req) {
        return jiliGameThreeService.auth(req,  req.getPlatformCode());
    }

    @Override
    public ZfBetResp zfBet(ZfBetReq req) {
        return jiliGameThreeService.bet(req,  req.getPlatformCode());
    }

    @Override
    public ZfCancelBetResp zfCancelBet(ZfCancelBetReq req) {
        return jiliGameThreeService.cancelBet(req,  req.getPlatformCode());
    }

    @Override
    public ZfBetResp zfSessionBet(ZfSessionBetReq req) {
        return jiliGameThreeService.sessionBet(req,  req.getPlatformCode());
    }

    @Override
    public ZfCancelBetResp zfCancelSessionBet(ZfCancelSessionBetReq req) {
        return jiliGameThreeService.cancelSessionBet(req, req.getPlatformCode());
    }

}

package com.cloud.baowang.play.api.third;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cloud.baowang.play.api.api.third.FTGGameApi;
import com.cloud.baowang.play.api.vo.base.FTGErrorRes;
import com.cloud.baowang.play.api.vo.ftg.FTGBetReq;
import com.cloud.baowang.play.api.vo.ftg.FTGCancelReq;
import com.cloud.baowang.play.api.vo.ftg.FTGGetBalanceReq;
import com.cloud.baowang.play.api.vo.ftg.FTGGetBalanceRes;
import com.cloud.baowang.play.game.ftg.FTGGameServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class FTGGameApiImpl implements FTGGameApi {

    @Autowired
    private FTGGameServiceImpl gameService;

    @Override
    public FTGErrorRes<JSONObject> queryBalance(FTGGetBalanceReq req) {
        FTGGetBalanceRes res = gameService.queryBalance(req);
        return FTGErrorRes.success(JSON.parseObject(JSON.toJSONString(res)));
    }

    @Override
    public FTGErrorRes<JSONObject> bet(FTGBetReq req) {
        FTGGetBalanceRes res = gameService.bet(req);
        return FTGErrorRes.success(JSON.parseObject(JSON.toJSONString(res)));
    }

    @Override
    public FTGErrorRes<JSONObject> cancelBet(FTGCancelReq req) {
        FTGGetBalanceRes res = gameService.cancelBet(req);
        return FTGErrorRes.success(JSON.parseObject(JSON.toJSONString(res)));
    }

    @Override
    public FTGErrorRes<JSONObject> payOut(FTGBetReq req) {
        FTGGetBalanceRes res = gameService.payOut(req);
        return FTGErrorRes.success(JSON.parseObject(JSON.toJSONString(res)));
    }
}

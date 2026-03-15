package com.cloud.baowang.play.api.third;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.play.api.api.third.CQ9GameApi;
import com.cloud.baowang.play.api.enums.cq9.CQ9ResultCodeEnums;
import com.cloud.baowang.play.api.vo.cq9.request.CQ9BetReq;
import com.cloud.baowang.play.api.vo.cq9.request.CQ9EndRoundReq;
import com.cloud.baowang.play.api.vo.cq9.request.CQ9PayoffReq;
import com.cloud.baowang.play.api.vo.cq9.request.CQ9RollinReq;
import com.cloud.baowang.play.api.vo.cq9.response.CQ9BalanceRsp;
import com.cloud.baowang.play.api.vo.cq9.response.CQ9BaseRsp;
import com.cloud.baowang.play.game.cq9.impl.CQ9ApiServiceImpl;
import com.cloud.baowang.play.game.cq9.utils.CQ9Utils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class CQ9GameApiImpl implements CQ9GameApi {


    private final CQ9ApiServiceImpl cq9ApiService;

    @Override
    public JSONObject checkPlayer(String account, String token) {
        CQ9BaseRsp cqRes = cq9ApiService.checkPlayer(account, token);
        return JSON.parseObject(JSON.toJSONString(cqRes));
    }


    @Override
    public JSONObject bet(CQ9BetReq req) {
        CQ9BaseRsp<CQ9BalanceRsp> res = cq9ApiService.bet(req);
        if(ObjectUtil.isNotEmpty(res.getData())){
            res.getData().setAmount(null);
        }
        return JSON.parseObject(JSON.toJSONString(res));
    }
    @Override
    public JSONObject rollout(CQ9BetReq req) {
        CQ9BaseRsp<CQ9BalanceRsp> res = cq9ApiService.bet(req);
        if(ObjectUtil.isNotEmpty(res.getData())){
            res.getData().setAmount(null);
        }
        return JSON.parseObject(JSON.toJSONString(res));
    }
    @Override
    public JSONObject takeall(CQ9BetReq req) {
        CQ9BaseRsp res = cq9ApiService.bet(req);
        return JSON.parseObject(JSON.toJSONString(res));
    }


    @Override
    public JSONObject payOut(CQ9EndRoundReq req) {
        CQ9BaseRsp<CQ9BalanceRsp> res= cq9ApiService.payOut(req);
        if(ObjectUtil.isNotEmpty(res.getData())){
            res.getData().setAmount(null);
        }
        return JSON.parseObject(JSON.toJSONString(res));
    }

    @Override
    public JSONObject payoff(CQ9PayoffReq req) {
        CQ9BaseRsp<CQ9BalanceRsp> res= cq9ApiService.payoff(req);
        if(ObjectUtil.isNotEmpty(res.getData())){
            res.getData().setAmount(null);
        }
        return JSON.parseObject(JSON.toJSONString(res));
    }

    @Override
    public JSONObject rollin(CQ9RollinReq req) {
        CQ9BaseRsp<CQ9BalanceRsp> res = cq9ApiService.rollin(req);
        if(ObjectUtil.isNotEmpty(res.getData())){
            res.getData().setAmount(null);
        }
        return JSON.parseObject(JSON.toJSONString(res));
    }

    @Override
    public JSONObject balance(String account, String gamecode, String token) {
        CQ9BaseRsp<CQ9BalanceRsp> cqRes = cq9ApiService.balance(account, gamecode, token);
        if(ObjectUtil.isNotEmpty(cqRes.getData())){
            cqRes.getData().setAmount(null);
        }
        return JSON.parseObject(JSON.toJSONString(cqRes));
    }

    @Override
    public JSONObject refund(CQ9BetReq req) {
        CQ9BaseRsp<CQ9BalanceRsp> cqRes = cq9ApiService.refund(req);
        if(ObjectUtil.isNotEmpty(cqRes.getData())){
            cqRes.getData().setAmount(null);
        }

        return JSON.parseObject(JSON.toJSONString(cqRes));
    }

    @Override
    public JSONObject record(String mtcode, String wtoken) {
        CQ9BaseRsp cqRes = cq9ApiService.record(mtcode, wtoken);
        return JSON.parseObject(JSON.toJSONString(cqRes));
    }
}

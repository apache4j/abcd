package com.cloud.baowang.play.wallet.controller;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.play.api.api.third.DBSHGameApi;
import com.cloud.baowang.play.api.vo.db.rsp.sh.DBSHBaseRsp;
import com.cloud.baowang.play.api.vo.db.sh.vo.*;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@LogExecution
@Slf4j
@RestController
@RequestMapping("callback/db/sh")
@Tag(name = "db彩票")
@AllArgsConstructor
public class DBSHController {


    private DBSHGameApi shGameApi;

    @PostMapping("/getBalance")
    public JSONObject getBalance(@RequestBody SHBalanceQueryVO reqVO) {
        log.info("DB真人 getBalance: {}", reqVO);
        DBSHBaseRsp<SHOrderRspData> rsp = shGameApi.getBalance(reqVO);

        JSONObject jsonObject = (JSONObject) JSON.toJSON(rsp);
        jsonObject.put("data", JSON.toJSONString(rsp.getData()));

        return jsonObject;
    }


    @PostMapping("/getBatchBalance")
    public JSONObject getBatchBalance(@RequestBody SHBalanceQueryBatchVO reqVO) {
        log.info("DB真人 getBatchBalance: {}", reqVO);
        DBSHBaseRsp<List<SHOrderRspData>> rsp = shGameApi.getBatchBalance(reqVO);

        JSONObject jsonObject = (JSONObject) JSON.toJSON(rsp);
        jsonObject.put("data", JSON.toJSONString(rsp.getData()));

        return jsonObject;
    }


    @PostMapping("/betConfirm")
    public JSONObject betConfirm(@RequestBody BetRequestVO reqVO) {
        log.info("DB真人 betConfirm:"+reqVO );
        DBSHBaseRsp<BetRspParams> rsp = shGameApi.betConfirm(reqVO);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(rsp);
        jsonObject.put("data", JSON.toJSONString(rsp.getData()));

        return jsonObject;
    }


    @PostMapping("/betCancel")
    public JSONObject betCancel(@RequestBody DBSHRequestVO reqVO) {
        log.info("DB真人 betCancel:"+reqVO );
        DBSHBaseRsp<BetRspParams> rsp = shGameApi.betCancel(reqVO);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(rsp);
        jsonObject.put("data", JSON.toJSONString(rsp.getData()));

        return jsonObject;
    }

    @PostMapping("/gamePayout")
    public JSONObject gamePayout(@RequestBody DBSHRequestVO reqVO) {
        log.info("DB真人 gamePayout:"+reqVO );
        DBSHBaseRsp<BetRspParams> rsp = shGameApi.gamePayout(reqVO);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(rsp);
        jsonObject.put("data", JSON.toJSONString(rsp.getData()));

        return jsonObject;
    }



    @PostMapping("/activityPayout")
    public JSONObject activityPayout(@RequestBody DBSHRequestVO reqVO) {
        log.info("DB真人 activityPayout:"+reqVO );
        DBSHBaseRsp<BetRspParams> rsp = shGameApi.activityPayout(reqVO);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(rsp);
        jsonObject.put("data", JSON.toJSONString(rsp.getData()));

        return jsonObject;
    }

    @PostMapping("/playerbetting")
    public JSONObject playerbetting(@RequestBody DBSHRequestVO reqVO) {
        log.info("DB真人 playerbetting:"+reqVO );
        DBSHBaseRsp<BetRspParams> rsp = shGameApi.playerbetting(reqVO);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(rsp);
        jsonObject.put("data", JSON.toJSONString(rsp.getData()));
        return jsonObject;
    }

    @PostMapping("/activityRebate")
    public DBSHBaseRsp<BetRspParams> activityRebate(@RequestBody DBSHRequestVO reqVO) {
        log.info("DB真人 activityRebate:"+reqVO );
        DBSHBaseRsp<BetRspParams> rsp = shGameApi.activityRebate(reqVO);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(rsp);
        jsonObject.put("data", JSON.toJSONString(rsp.getData()));
        return rsp;
    }


    /**
     * —DB真人回调地址
     *  https://console.playesoversea.pro/api/play-wallet-api/callback/db/sh/getBalance
     *  https://console.playesoversea.pro/api/play-wallet-api/callback/db/sh/getBatchBalance
     *  https://console.playesoversea.pro/api/play-wallet-api/callback/db/sh/betConfirm
     *  https://console.playesoversea.pro/api/play-wallet-api/callback/db/sh/betCancel
     *  https://console.playesoversea.pro/api/play-wallet-api/callback/db/sh/gamePayout
     *  https://console.playesoversea.pro/api/play-wallet-api/callback/db/sh/activityPayout
     *  https://console.playesoversea.pro/api/play-wallet-api/callback/db/sh/playerbetting
     *  https://console.playesoversea.pro/api/play-wallet-api/callback/db/sh/activityRebate
     *
     *
     */


}

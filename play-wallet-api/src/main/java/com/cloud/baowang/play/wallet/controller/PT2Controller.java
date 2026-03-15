package com.cloud.baowang.play.wallet.controller;


import com.cloud.baowang.play.api.api.third.PT2GameApi;
import com.cloud.baowang.play.api.vo.pt2.vo.NotifyBonusEventVO;
import com.cloud.baowang.play.api.vo.pt2.vo.rps.PT2RspVO;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cloud.baowang.play.api.vo.pt2.PT2ActionVO;
import com.cloud.baowang.play.api.vo.pt2.PT2BaseVO;
import com.cloud.baowang.play.api.vo.pt2.vo.rps.PT2BaseRsp;
import com.cloud.baowang.play.api.vo.pt2.vo.settle.GameRoundResultVO;
import com.cloud.baowang.play.api.vo.pt2.vo.settle.TransferFundsVO;

@LogExecution
@Slf4j
@RestController
@RequestMapping("callback/playtech")
@Tag(name = "PT2单一钱包")
@AllArgsConstructor
public class PT2Controller {


    private final PT2GameApi pt2GameApi;

    @PostMapping("/authenticate")
    public PT2BaseRsp authenticate(@RequestBody PT2ActionVO actionVo) {
        log.info("authenticate pt2 : " +" actionVo : "+actionVo);
        return pt2GameApi.authenticate(actionVo);
    }

    @PostMapping("/bet")
    public PT2BaseRsp bet(@RequestBody PT2ActionVO actionVo) {
        log.info("bet pt2 : " +" actionVo : "+actionVo);
        return pt2GameApi.bet(actionVo);
    }


    @PostMapping("/gameroundresult")
    public PT2BaseRsp gameroundresult(@RequestBody GameRoundResultVO actionVo) {
        //TODO
        log.info("gameroundresult pt2 : " +" actionVo : "+actionVo);
        return pt2GameApi.gameroundresult(actionVo);
    }


    @PostMapping("/getbalance")
    public PT2BaseRsp getbalance(@RequestBody PT2BaseVO actionVo) {
        log.info("getbalance pt2 : " +" actionVo : "+actionVo);
        return pt2GameApi.getbalance(actionVo);
    }

    @PostMapping("/healthcheck")
    public ResponseEntity<Void> healthcheck(@RequestBody PT2BaseVO actionVo) {
        log.info("healthcheck pt2 : actionVo : {}", actionVo);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/keepalive")
    public PT2BaseRsp keepalive(@RequestBody PT2BaseVO actionVo) {
        log.info("getbalance pt2 : " +" actionVo : "+actionVo);
        PT2RspVO rspVO = PT2RspVO.builder().requestId(actionVo.getRequestId()).build();
        return PT2BaseRsp.success(rspVO);
    }

    @PostMapping("/logout")
    public PT2BaseRsp logout(@RequestBody PT2BaseVO actionVo) {
        log.info("logout pt2 : " +" actionVo : "+actionVo);
        return pt2GameApi.logout(actionVo);
    }

    @PostMapping("/notifyBonusEvent")
    public PT2BaseRsp notifyBonusEvent(@RequestBody NotifyBonusEventVO actionVo) {
        log.info("transferFunds pt2 : " +" actionVo : "+actionVo);
        PT2RspVO rspVO = PT2RspVO.builder().requestId(actionVo.getRequestId()).build();
        return PT2BaseRsp.success(rspVO);
    }

    @PostMapping("/transferFunds")
    public PT2BaseRsp transferFunds(@RequestBody TransferFundsVO actionVo) {
        log.info("transferFunds pt2 : " +" actionVo : "+actionVo);
        return pt2GameApi.transferFunds(actionVo);
    }



}

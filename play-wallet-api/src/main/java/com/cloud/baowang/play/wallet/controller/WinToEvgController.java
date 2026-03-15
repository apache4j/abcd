package com.cloud.baowang.play.wallet.controller;


import com.cloud.baowang.play.api.api.third.WinToEvgApi;
import com.cloud.baowang.play.api.vo.winto.req.WintoBaseVO;
import com.cloud.baowang.play.api.vo.winto.req.WintoTransferVO;
import com.cloud.baowang.play.api.vo.winto.rsp.WinToEvgRsp;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import com.cloud.baowang.play.wallet.vo.req.pt2.PT2ActionVO;
import com.cloud.baowang.play.wallet.vo.req.pt2.PT2BaseVO;
import com.cloud.baowang.play.wallet.vo.req.pt2.vo.NotifyBonusEventVO;
import com.cloud.baowang.play.wallet.vo.req.pt2.vo.settle.GameRoundResultVO;
import com.cloud.baowang.play.wallet.vo.req.pt2.vo.settle.TransferFundsVO;
import com.cloud.baowang.play.wallet.vo.res.pt2.PT2BaseRsp;
import com.cloud.baowang.play.wallet.vo.res.pt2.PT2RspVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LogExecution
@Slf4j
@RestController
@RequestMapping("callback/winto/evg")
@Tag(name = "winto电子")
@AllArgsConstructor
public class WinToEvgController {


    private final WinToEvgApi winToEvgApi;

    @PostMapping("/verifySession")
    public WinToEvgRsp verifySession(@RequestBody WintoBaseVO actionVo) {
        log.info("authenticate winto电子 : " +" actionVo : "+actionVo);
        return winToEvgApi.verifySession(actionVo);
    }


    @PostMapping("/getBalance")
    public WinToEvgRsp getBalance(@RequestBody WintoBaseVO actionVo) {
        log.info("getBalance winto电子 : " +" actionVo : "+actionVo);
        return winToEvgApi.getBalance(actionVo);
    }

    @PostMapping("/betTransfer")
    public WinToEvgRsp betTransfer(@RequestBody WintoTransferVO actionVo) {
        log.info("betTransfer winto电子 : " +" actionVo : "+actionVo);
        return winToEvgApi.betTransfer(actionVo);
    }



    @PostMapping("/cancelBetTransfer")
    public WinToEvgRsp cancelBetTransfer(@RequestBody WintoTransferVO actionVo) {
        log.info("cancelBetTransfer winto电子 : " +" actionVo : "+actionVo);
        return winToEvgApi.cancelBetTransfer(actionVo);
    }


    @PostMapping("/adjustment")
    public WinToEvgRsp adjustment(@RequestBody WintoTransferVO actionVo) {
        log.info("adjustment winto电子 : " +" actionVo : "+actionVo);
        return winToEvgApi.adjustment(actionVo);
    }




    @PostMapping("/manualBetTransfer")
    public WinToEvgRsp manualBetTransfer(@RequestBody WintoTransferVO actionVo) {
        log.info("adjustment winto电子 : " +" actionVo : "+actionVo);
        return winToEvgApi.manualBetTransfer(actionVo);
    }

}

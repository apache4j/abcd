package com.cloud.baowang.play.wallet.controller;


import com.cloud.baowang.play.api.api.third.DBAceltGameApi;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.cloud.baowang.play.api.vo.db.acelt.vo.BalanceQueryVO;
import com.cloud.baowang.play.api.vo.db.acelt.vo.TransferCheckVO;
import com.cloud.baowang.play.api.vo.db.acelt.vo.TransferRequestVO;
import com.cloud.baowang.play.api.vo.db.acelt.vo.TransferRspData;
import com.cloud.baowang.play.api.vo.db.rsp.acelt.DBAceltBaseRsp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@LogExecution
@Slf4j
@RestController
@RequestMapping("callback/db/acelt")
@Tag(name = "db彩票")
@AllArgsConstructor
public class DBAceltController {


    private DBAceltGameApi aceltGameApi;

    @PostMapping("/getBalance")
    public DBAceltBaseRsp getBalance(@RequestBody BalanceQueryVO reqVO) {
        log.info("acelt getBalance:"+reqVO );
        return aceltGameApi.getBalance(reqVO);
    }


    @PostMapping("/upateBalance")
    public DBAceltBaseRsp upateBalance(@RequestBody TransferRequestVO reqVO) {
        log.info("acelt upateBalance:"+reqVO );
        return aceltGameApi.upateBalance(reqVO);
    }


    @PostMapping("/boracay/api/safety/transfer")
    public DBAceltBaseRsp transfer(@RequestBody TransferCheckVO reqVO) {
        log.info("acelt transfer:"+reqVO );
        return aceltGameApi.transfer(reqVO);
    }


    @PostMapping("/safetyTransfer")
    public TransferRspData safetyTransfer(@RequestBody TransferCheckVO reqVO) {
        log.info("acelt safetyTransfer:"+reqVO );
        return aceltGameApi.safetyTransfer(reqVO);
    }




}

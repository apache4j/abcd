package com.cloud.baowang.play.wallet.controller;

import com.cloud.baowang.play.api.api.third.TFGameApi;
import com.cloud.baowang.play.api.vo.tf.*;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@LogExecution
@Slf4j
@RestController
@RequestMapping("callback/tf")
@Tag(name = "TF单一钱包")
@AllArgsConstructor
public class TFController {

//    private TfService tfService;

    private TFGameApi tfGameApi;

    @Operation(summary = "登录token验证")
    @PostMapping("/token/validate")
    public TfValidResp validate(@RequestBody TfValidReq req) {
        return tfGameApi.validate(req);
    }

    @Operation(summary = "会员余额")
    @GetMapping("wallet")
    public TfWalletResp wallet(@RequestParam("loginName") String loginName) {
        return tfGameApi.wallet(loginName);
    }

    @Operation(summary = "资金调动")
    @PostMapping("transfer")
    public TfTransferResp transfer(@RequestBody TfTransferReq req) {
        return tfGameApi.transfer(req);
    }

    @Operation(summary = "回滚")
    @PostMapping("rollback")
    public TfTransferResp rollback(@RequestBody TfTransferReq req) {
        return tfGameApi.rollback(req);
    }




}

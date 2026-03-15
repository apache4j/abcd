package com.cloud.baowang.play.wallet.controller;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.play.api.api.third.NextSpinGameApi;
import com.cloud.baowang.play.api.enums.nextSpin.NextSpinRespErrEnums;
import com.cloud.baowang.play.api.vo.nextSpin.NextSpinReq;
import com.cloud.baowang.play.api.vo.nextSpin.NextSpinResp;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

@LogExecution
@Slf4j
@RestController
@RequestMapping("callback")
@Tag(name = "NextSpin单一钱包")
public class NextSpinController {

//    @Resource
//    private NextSpinService nextSpinService;

    @Resource
    private NextSpinGameApi nextSpinGameApi;

    @Operation(summary = "单一钱包")
    @PostMapping("/nextSpin")
    public Object nextSpinRequest(@RequestHeader("API") String method,@RequestBody NextSpinReq request) {
        log.info("nextSpin request head method {}, placeBet下注入参{}",method, JSONObject.toJSONString(request));
        if (StringUtils.isEmpty(method)) {
            return NextSpinResp.err(NextSpinRespErrEnums.INVALID_REQUEST,request.getMerchantCode(), request.getSerialNo());
        }
        return switch (method) {
            case "authorize":
                yield nextSpinGameApi.oauth(request);
            case "getBalance":
                yield nextSpinGameApi.checkBalance(request);
            case "transfer":
                yield nextSpinGameApi.bet(request);
            default:
                yield NextSpinResp.err(NextSpinRespErrEnums.INVALID_REQUEST,request.getMerchantCode(), request.getSerialNo());
        };
    }
}

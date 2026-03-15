package com.cloud.baowang.play.wallet.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.play.api.api.third.FastSpinGameApi;
import com.cloud.baowang.play.api.vo.fastSpin.req.FSBalanceReq;
import com.cloud.baowang.play.api.vo.fastSpin.req.FSTransferReq;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.stream.Collectors;


@LogExecution
@Slf4j
@RestController
@RequestMapping("callback/fastSpin")
@Tag(name = "FS单一钱包")
@AllArgsConstructor
public class FSEController {

    private FastSpinGameApi fastSpinGameApi;



    @Operation(summary = "FastSpin API")
    @PostMapping()
    public Object balance(HttpServletRequest request, @RequestHeader(value = "Digest", required = false, defaultValue = "unknown") String digest,
    @RequestHeader(value = "API", required = false, defaultValue = "") String api) throws IOException {

        String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

        String GET_BALANCE = "getBalance";
        String TRANSFER = "transfer";

        if (TRANSFER.equals(api)){
            log.info("fs transfer param: {}",body);
            FSTransferReq fsTransferReq = JSON.parseObject(body, FSTransferReq.class);
            fsTransferReq.setBody(body);
            Object authenticate = fastSpinGameApi.transfer(fsTransferReq , digest);

            log.info("fs transfer back: {}",authenticate);

            return authenticate;
        }else if (GET_BALANCE.equals(api)){
            log.info("fs getBalance param: {}",body);
            FSBalanceReq fsBalanceReq = JSON.parseObject(body, FSBalanceReq.class);
            fsBalanceReq.setBody(body);
            Object authenticate = fastSpinGameApi.getBalance(fsBalanceReq , digest);

            log.info("fs getBalance back: {}",authenticate);

            return authenticate;
        }
        log.error("fastSpin callback unknown api : {}",body);
        return "fastSpin callback unknown api";
    }
}

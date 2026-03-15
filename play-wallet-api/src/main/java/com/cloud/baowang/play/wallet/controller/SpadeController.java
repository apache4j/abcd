package com.cloud.baowang.play.wallet.controller;

import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.play.api.api.third.SpadeGameApi;
import com.cloud.baowang.play.api.vo.spade.req.SpadeBalanceReq;
import com.cloud.baowang.play.api.vo.spade.req.SpadeTransferReq;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import com.cloud.baowang.play.wallet.service.SpadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.stream.Collectors;


@LogExecution
@Slf4j
@RestController
@RequestMapping("callback/spade")
@Tag(name = "spade单一钱包")
@AllArgsConstructor
public class SpadeController {

    private SpadeGameApi spadeGameApi;

    @Operation(summary = "spade API")
    @PostMapping()
    public Object balance(HttpServletRequest request, @RequestHeader(value = "Digest", required = false, defaultValue = "unknown") String digest,
    @RequestHeader(value = "API", required = false, defaultValue = "") String api) throws IOException {
        String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        String GET_BALANCE = "getBalance";
        String TRANSFER = "transfer";
        if (TRANSFER.equals(api)){
            log.info("Spade transfer param: {}  API:{}  Digest:{}",body,api,digest);
            SpadeTransferReq spadeTransferReq = JSON.parseObject(body, SpadeTransferReq.class);
            spadeTransferReq.setBody(body);
            spadeTransferReq.setDigest(digest);
            Object authenticate = spadeGameApi.transfer(spadeTransferReq);
            log.info("Spade transfer back: {}",authenticate);
            return authenticate;
        }else if (GET_BALANCE.equals(api)){
            log.info("Spade getBalance param: {}  API:{}  Digest:{}",body,api,digest);
            SpadeBalanceReq spadeBalanceReq = JSON.parseObject(body, SpadeBalanceReq.class);
            spadeBalanceReq.setBody(body);
            spadeBalanceReq.setDigest(digest);
            Object authenticate = spadeGameApi.getBalance(spadeBalanceReq);
            log.info("Spade getBalance back: {}",authenticate);
            return authenticate;
        }
        log.error("spade callback unknown api : {}",body);
        return "spade callback unknown api";
    }
}

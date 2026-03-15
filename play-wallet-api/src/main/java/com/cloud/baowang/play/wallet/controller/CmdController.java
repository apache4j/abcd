package com.cloud.baowang.play.wallet.controller;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.AccountFlowMqVO;
import com.cloud.baowang.common.kafka.vo.AccountRequestMqVO;
import com.cloud.baowang.play.api.api.third.CmdGameApi;
import com.cloud.baowang.play.api.vo.cmd.CmdReq;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@LogExecution
@Slf4j
@RestController
@RequestMapping("callback/cmd")
@Tag(name = "CMD单一钱包")
public class CmdController {

//    @Resource
//    private CmdService cmdService;

    @Resource
    private CmdGameApi cmdGameApi;

    @Operation(summary = "获取用户鉴权")
    @GetMapping(value = "token", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> token(@RequestParam(value = "token" , required = false, defaultValue = "")String token) {
        log.info("CMD request Token :{},Token :{}",token,token);
        String xml = cmdGameApi.token(token);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(xml);
    }

    @Operation(summary = "获取用户余额")
    @GetMapping("getBalance")
    public String getBalance(@RequestParam("method")String method,
                             @RequestParam("balancePackage")String balancePackage,
                             @RequestParam("packageId")String packageId,
                             @RequestParam("dateSent")Long dateSent) {
         CmdReq cmdReq= CmdReq.builder().method(method).balancePackage(balancePackage).packageId(packageId).dateSent(dateSent).build();
         log.info("CMD request head method {}, getBalance下注入参{}",cmdReq.getMethod(), JSONObject.toJSONString(cmdReq));
         return cmdGameApi.doAction(cmdReq);
    }

    @Operation(summary = "下注和更新用户余额")
    @PostMapping("updateBalance")
    public String updateBalance(@RequestBody CmdReq request) {
        try {
            request.setBalancePackage(URLDecoder.decode(request.getBalancePackage(), StandardCharsets.UTF_8.toString()));
        } catch (UnsupportedEncodingException e) {
            log.info("CMD request head method {}, updateBalance下注入参{}",request.getMethod(), JSONObject.toJSONString(request));
            throw new BaowangDefaultException(e.getMessage());
        }
        log.info("CMD request head method {}, updateBalance下注入参{}",request.getMethod(), JSONObject.toJSONString(request));
        return cmdGameApi.doAction(request);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
//        String encoded = URLEncoder.encode("NBVJgMYySvjBbGWgB8hU0y+ZpQHOvCcFvl5oo9igl34s6AAHdx4EYwkrj9I6Ofbco5qjk6y5zZODtTH3heFBaZlr3eoz6tZJH91ggZGp2zX2odA6tfDPwa9AsVXMW2gspY/90dylxxAu1U+gIMnZIhpW2nKNu4q0yoH4fipxo+OBW/Cm7tUIRfUiU4iBNm8dRUNWelOTF2Qipc2KFJEyLo+fIt90hzfxzWvDztLtArE=", StandardCharsets.UTF_8.toString());
//        System.out.println(encoded);
        String decode = URLDecoder.decode("wyPsvBOQQqMU9PVTVBgPo92Uuz%2FC67w1Y5nU33THRN2NF%2F%2BJFqCkiNQhVltk8CKTOAqgNouV0KioL0O1pOmwV5tx%2Bmj6uF%2BfkrxKN39BQCxb1iOUB7IACAICuTr8sWwJlY5LVuwvQWnTW7aDqwToMMQbK4p%2B6AG84c%2B5FNXbZqUAaD2gbqNHAgMWqMHiJH%2Bu4E%2BUs7WQKgVd04wJ%2Fk%2B1MYRwxzM43lTPv1rQmq3tO1o%3D", StandardCharsets.UTF_8.toString());
        System.out.println(decode);
    }

}

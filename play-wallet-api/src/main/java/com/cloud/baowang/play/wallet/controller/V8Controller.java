package com.cloud.baowang.play.wallet.controller;

import com.cloud.baowang.play.api.api.third.V8GameApi;
import com.cloud.baowang.play.api.vo.v8.SeamlesswalletResp;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * v8单一钱包
 */
@LogExecution
@Slf4j
@RestController
@RequestMapping("callback/v8/api")
@Tag(name = "V8-棋牌游戏")
@AllArgsConstructor
public class V8Controller {

//    private V8Service marblesService;

    private final V8GameApi v8GameApi;

    @Operation(summary = "查询余额")
    @GetMapping("/seamlesswallet")
    public SeamlesswalletResp getBalance(HttpServletRequest request) {
        return v8GameApi.seamlessWallet(request);
    }



}

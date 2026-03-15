package com.cloud.baowang.play.wallet.controller;

import com.cloud.baowang.play.api.api.third.OMGGameApi;
import com.cloud.baowang.play.api.vo.omg.OmgReq;
import com.cloud.baowang.play.api.vo.omg.OmgResp;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OMG单一钱包
 * 包含 JILIPLUS, PPPLUS, PGPLUS
 */
@LogExecution
@Slf4j
@RestController
@RequestMapping("callback/omg")
@Tag(name = "OMG单一钱包")
@AllArgsConstructor
public class OMGController {

//    private OmgService omgService;

    private final OMGGameApi omgGameApi;

    @Operation(summary = "游戏登入后令牌验证")
    @PostMapping("/api/luck/user/verify_session")
    public OmgResp verify(@RequestBody OmgReq req) {
        return omgGameApi.verify(req);
    }


    @Operation(summary = "获取玩家金额")
    @PostMapping("/api/luck/balance/get_balance")
    public OmgResp getBalance(@RequestBody OmgReq req) {
        return omgGameApi.getBalance(req);
    }

    @Operation(summary = "改变用户余额)")
    @PostMapping("/api/luck/balance/change_balance")
    public OmgResp changeBalance(@RequestBody OmgReq req) {
        return omgGameApi.changeBalance(req);
    }



}

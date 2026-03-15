package com.cloud.baowang.play.wallet.controller;

import com.cloud.baowang.play.api.api.third.IMGameApi;
import com.cloud.baowang.play.api.vo.im.ImReq;
import com.cloud.baowang.play.api.vo.im.ImResp;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
//import com.cloud.baowang.play.wallet.service.ImService;
//import com.cloud.baowang.play.wallet.vo.req.im.ImReq;
//import com.cloud.baowang.play.wallet.vo.res.im.ImResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.annotation.Resource;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LogExecution
@Slf4j
@RestController
@RequestMapping("callback")
@Tag(name = "IM单一钱包")
public class IMController {

//    @Resource
//    private ImService imService;

    @Resource
    private IMGameApi imGameApi;


    @Operation(summary = "im电子请求")
    @PostMapping("im")
    public ImResp imRequest(@RequestBody ImReq request) {
        String cmd = request.getCmd();
        if (StringUtils.isEmpty(cmd)) {
            return ImResp.err("cmd not exist");
        }
        return switch (cmd) {
            // 获取余额
            case "getBalance":
                yield imGameApi.getBalance(request);
            // 结果 投注/赢
            case "writeBet":
                yield imGameApi.writeBet(request);
            default:
                yield ImResp.err("cmd not exsit");
        };
    }

}

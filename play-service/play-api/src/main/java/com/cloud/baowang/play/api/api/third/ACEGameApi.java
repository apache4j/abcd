package com.cloud.baowang.play.api.api.third;


import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.ace.req.*;
import com.cloud.baowang.play.api.vo.ace.res.ACEAuthenticateRes;
import com.cloud.baowang.play.api.vo.ace.res.ACEBaseRes;
import com.cloud.baowang.play.api.vo.fc.req.FCBaseReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(contextId = "ace-api", value = ApiConstants.NAME)
@Tag(name = "ace-游戏")
public interface ACEGameApi {

    String PREFIX = ApiConstants.PREFIX + "/ace/api";

    @Operation(summary = "验证")
    @PostMapping("/authenticate")
    public Object authenticate(@RequestBody ACEAuthenticateReq req) ;

    @Operation(summary = "取得余额")
    @PostMapping("/getbalance")
    public Object getbalance(@RequestBody ACEGetbalanceReq vo) ;

    @Operation(summary = "下注")
    @PostMapping(PREFIX +"/bet")
    public Object bet(@RequestBody ACEBetReq vo) ;

    @Operation(summary = "派彩")
    @PostMapping(PREFIX +"/betresult")
    public Object betresult(@RequestBody ACEBetresultReq vo) ;

    @Operation(summary = "取消下注")
    @PostMapping(PREFIX +"/refund")
    public Object refund(@RequestBody ACERefundReq vo);

    @Operation(summary = "jackpot派奖")
    @PostMapping(PREFIX +"/jackpotwin")
    public Object jackpotwin(@RequestBody ACEJackpotwinReq vo) ;

}
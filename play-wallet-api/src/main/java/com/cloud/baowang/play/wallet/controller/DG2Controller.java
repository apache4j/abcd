package com.cloud.baowang.play.wallet.controller;


import com.cloud.baowang.play.api.api.third.DG2GameApi;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.cloud.baowang.play.api.vo.dg2.req.DGActionVo;
import com.cloud.baowang.play.api.vo.dg2.rsp.DGBaseRsp;

@LogExecution
@Slf4j
@RestController
@RequestMapping("callback/dg")
@Tag(name = "DG单一钱包")
@AllArgsConstructor
public class DG2Controller {


    private final DG2GameApi dg2GameApi;
    private static final String prefix = "/v2/specification";

    @PostMapping(prefix+"/user/getBalance/{agentName}")
    public DGBaseRsp getBalance(@PathVariable("agentName") String agentName , @RequestBody DGActionVo actionVo) {
//        log.info(" getBalance api : agentName : "+agentName +" actionVo : "+actionVo);
        return dg2GameApi.getBalance(agentName,actionVo);
    }
    @PostMapping(prefix+"/account/transfer/{agentName}")
    public DGBaseRsp transfer(@PathVariable("agentName") String agentName , @RequestBody DGActionVo actionVo) {
//        log.info(" transfer api : agentName : "+agentName +" actionVo : "+actionVo);
        return dg2GameApi.transfer(agentName,actionVo);
    }
    @PostMapping(prefix+"/account/inform/{agentName}")
    public DGBaseRsp inform(@PathVariable("agentName") String agentName , @RequestBody DGActionVo actionVo) {
//        log.info(" inform api : agentName : "+agentName +" actionVo : "+actionVo);
        return dg2GameApi.inform(agentName,actionVo);
    }



}

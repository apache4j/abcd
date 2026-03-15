package com.cloud.baowang.play.wallet.controller;


import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.play.api.api.third.SexyGameApi;
import com.cloud.baowang.play.api.vo.sexy.req.SexyActionVo;
import com.cloud.baowang.play.wallet.annotations.LogExecution;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@LogExecution
@Slf4j
@RestController
@RequestMapping("callback/sexy")
@Tag(name = "sexy单一钱包")
@AllArgsConstructor
public class SexyController {


    private final SexyGameApi sexyGameApi;

    @PostMapping("/action")
    public JSONObject action(@RequestParam String key,
                             @RequestParam String message) {
        log.info("callback/sexy : "+key+" --- "+message);
        SexyActionVo actionVo = new SexyActionVo();
        actionVo.setKey(key);
        actionVo.setMessage(message);
        return sexyGameApi.action(actionVo);
    }


}

package com.cloud.baowang.play.wallet.controller;


import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.play.api.api.third.JDBGameApi;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@LogExecution
@Slf4j
@RestController
@RequestMapping("callback/jdb")
@Tag(name = "jdb单一钱包")
@AllArgsConstructor
public class JDBController {


    private final JDBGameApi jdbGameApi;

    @PostMapping("/action")
    public JSONObject action(@RequestParam("x") String x) {
//        log.info("JDB请求参数 : "+x);
        return jdbGameApi.action(x);
    }


}

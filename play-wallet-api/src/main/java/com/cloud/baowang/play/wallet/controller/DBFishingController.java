package com.cloud.baowang.play.wallet.controller;


import com.cloud.baowang.play.api.api.third.DBFishingGameApi;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import com.cloud.baowang.play.api.vo.db.evg.vo.DBEVGBasicInfo;
import com.cloud.baowang.play.api.vo.db.rsp.evg.DBEVGBaseRsp;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@LogExecution
@Slf4j
@RestController
@RequestMapping("callback/db/fishing")
@Tag(name = "db捕鱼")
@AllArgsConstructor
public class DBFishingController {


    private DBFishingGameApi fishingGameApi;

    @PostMapping("/queryBalance")
    public DBEVGBaseRsp queryBalance(@RequestParam String agent,
                                     @RequestParam long timestamp,
                                     @RequestParam long randno,
                                     @RequestParam String sign,
                                     @RequestBody String body) {
        DBEVGBasicInfo evgBasicInfo = DBEVGBasicInfo.builder().agent(agent).timestamp(timestamp).randno(randno).sign(sign).build();
        log.info("fishingqueryBalance:"+evgBasicInfo +" BalanceChangeBody : "+body);
        return fishingGameApi.queryBalance(evgBasicInfo,body);
    }


    @PostMapping("/balanceChange")
    public DBEVGBaseRsp balanceChange(
            @RequestParam String agent,
            @RequestParam long timestamp,
            @RequestParam long randno,
            @RequestParam String sign,
            @RequestBody String body) {
        DBEVGBasicInfo evgBasicInfo = DBEVGBasicInfo.builder().agent(agent).timestamp(timestamp).randno(randno).sign(sign).build();
        log.info("fishingbalanceChange:"+evgBasicInfo +" BalanceChangeBody : "+body);
        return fishingGameApi.balanceChange(evgBasicInfo,body);
    }


    @PostMapping("/queryOrderStatus")
    public DBEVGBaseRsp queryOrderStatus( @RequestParam String agent,
                                 @RequestParam long timestamp,
                                 @RequestParam long randno,
                                 @RequestParam String sign,
                                          @RequestBody String body) {
        DBEVGBasicInfo evgBasicInfo = DBEVGBasicInfo.builder().agent(agent).timestamp(timestamp).randno(randno).sign(sign).build();
        log.info("fishingqueryOrderStatus:"+evgBasicInfo +" BalanceChangeBody : "+body);
        return fishingGameApi.queryOrderStatus(evgBasicInfo,body);
    }


}

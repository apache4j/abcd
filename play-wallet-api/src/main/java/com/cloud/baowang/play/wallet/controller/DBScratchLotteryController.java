package com.cloud.baowang.play.wallet.controller;


import com.cloud.baowang.play.api.api.third.DBScratchGameApi;
import com.cloud.baowang.play.api.vo.db.evg.vo.DBEVGBasicInfo;
import com.cloud.baowang.play.api.vo.db.rsp.evg.DBEVGBaseRsp;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@LogExecution
@Slf4j
@RestController
@RequestMapping("callback/db/scratch")
@Tag(name = "db刮刮乐")
@AllArgsConstructor
public class DBScratchLotteryController {


    private DBScratchGameApi scratchLotteryGameApi;

    @PostMapping("/queryBalance")
    public DBEVGBaseRsp queryBalance(@RequestParam String agent,
                                     @RequestParam long timestamp,
                                     @RequestParam long randno,
                                     @RequestParam String sign,
                                     @RequestBody String body) {
        DBEVGBasicInfo evgBasicInfo = DBEVGBasicInfo.builder().agent(agent).timestamp(timestamp).randno(randno).sign(sign).build();
        log.info("scratch - queryBalance:"+evgBasicInfo );
        return scratchLotteryGameApi.queryBalance(evgBasicInfo,body);
    }


    @PostMapping("/balanceChange")
    public DBEVGBaseRsp balanceChange(
            @RequestParam String agent,
            @RequestParam long timestamp,
            @RequestParam long randno,
            @RequestParam String sign,
            @RequestBody String body) {
        DBEVGBasicInfo evgBasicInfo = DBEVGBasicInfo.builder().agent(agent).timestamp(timestamp).randno(randno).sign(sign).build();
        log.info("scratch - balanceChange:"+evgBasicInfo);
        return scratchLotteryGameApi.balanceChange(evgBasicInfo,body);
    }


    @PostMapping("/queryOrderStatus")
    public DBEVGBaseRsp queryOrderStatus( @RequestParam String agent,
                                 @RequestParam long timestamp,
                                 @RequestParam long randno,
                                 @RequestParam String sign,
                                          @RequestBody String body) {
        DBEVGBasicInfo evgBasicInfo = DBEVGBasicInfo.builder().agent(agent).timestamp(timestamp).randno(randno).sign(sign).build();
        log.info("scratch - queryOrderStatus:"+evgBasicInfo );
        return scratchLotteryGameApi.queryOrderStatus(evgBasicInfo,body);
    }


}

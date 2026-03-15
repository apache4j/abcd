package com.cloud.baowang.play.wallet.controller;


import com.cloud.baowang.play.api.api.third.DBChessGameApi;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.cloud.baowang.play.api.vo.db.evg.vo.DBEVGBasicInfo;
import com.cloud.baowang.play.api.vo.db.rsp.evg.DBEVGBaseRsp;

@LogExecution
@Slf4j
@RestController
@RequestMapping("callback/db/chess")
@Tag(name = "博雅棋牌")
@AllArgsConstructor
public class DBChessController {


    private DBChessGameApi chessGameApi;

    @PostMapping("/queryBalance")
    public DBEVGBaseRsp queryBalance(@RequestParam String agent,
                                     @RequestParam long timestamp,
                                     @RequestParam long randno,
                                     @RequestParam String sign,
                                     @RequestBody String body) {
        DBEVGBasicInfo evgBasicInfo = DBEVGBasicInfo.builder().agent(agent).timestamp(timestamp).randno(randno).sign(sign).build();
        log.info("chessqueryBalance:"+evgBasicInfo +" BalanceChangeBody : -"+body);
        return chessGameApi.queryBalance(evgBasicInfo,body);
    }


    @PostMapping("/balanceChange")
    public DBEVGBaseRsp balanceChange(
            @RequestParam String agent,
            @RequestParam long timestamp,
            @RequestParam long randno,
            @RequestParam String sign,
            @RequestBody String body) {
        DBEVGBasicInfo evgBasicInfo = DBEVGBasicInfo.builder().agent(agent).timestamp(timestamp).randno(randno).sign(sign).build();
        log.info("chessbalanceChange:"+evgBasicInfo +" BalanceChangeBody : -"+body);
        return chessGameApi.balanceChange(evgBasicInfo,body);
    }


    @PostMapping("/queryOrderStatus")
    public DBEVGBaseRsp queryOrderStatus( @RequestParam String agent,
                                 @RequestParam long timestamp,
                                 @RequestParam long randno,
                                 @RequestParam String sign,
                                          @RequestBody String body) {
        DBEVGBasicInfo evgBasicInfo = DBEVGBasicInfo.builder().agent(agent).timestamp(timestamp).randno(randno).sign(sign).build();
        log.info("chessqueryOrderStatus:"+evgBasicInfo +" BalanceChangeBody : -"+body);
        return chessGameApi.queryOrderStatus(evgBasicInfo,body);
    }


}

package com.cloud.baowang.play.api.api.third;


import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.fastSpin.req.FSBalanceReq;
import com.cloud.baowang.play.api.vo.fastSpin.req.FSTransferReq;
import com.cloud.baowang.play.api.vo.fc.req.FCBaseReq;
import com.cloud.baowang.play.api.vo.fc.req.GetBalanceReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


@FeignClient(contextId = "fc-api", value = ApiConstants.NAME)
@Tag(name = "fc-游戏")
public interface FCGameApi {

    String PREFIX = ApiConstants.PREFIX + "/fc/api";

    @Operation(summary = "取得余额")
    @PostMapping("/GetBalance")
    public Object GetBalance(@RequestBody FCBaseReq vo) ;
    // 4-2、下注信息及游戏结果 (BetNInfo)
    @Operation(summary = "下注信息及游戏结果")
    @PostMapping(PREFIX +"/BetNInfo")
    public Object BetNInfo(@RequestBody FCBaseReq vo) ;

    // 4-3、取消下注與游戏结果 (CancelBetNInfo)
    @Operation(summary = "取消下注與游戏结果")
    @PostMapping(PREFIX +"/CancelBetNInfo")
    public Object CancelBetNInfo(@RequestBody FCBaseReq vo) ;

    // 4-4、下注 (Bet)
    @Operation(summary = "下注")
    @PostMapping(PREFIX +"/Bet")
    public Object Bet(@RequestBody FCBaseReq vo) ;

    // 4-5、派彩 (Settle)
    @Operation(summary = "派彩")
    @PostMapping(PREFIX +"/Settle")
    public Object Settle(@RequestBody FCBaseReq vo) ;

    // 4-6、取消下注 (CancelBet)
    @Operation(summary = "取消下注")
    @PostMapping(PREFIX +"/CancelBet")
    public Object CancelBet(@RequestBody FCBaseReq vo);

    // 4-7、活动派彩 (EventSettle)
    @Operation(summary = "活动派彩")
    @PostMapping(PREFIX +"/EventSettle")
    public Object EventSettle(@RequestBody FCBaseReq vo);

    // 4-8、Free Spin 下注信息及游戏结果 (FreeSpinBetNInfo)
    @Operation(summary = "下注信息及游戏结果")
    @PostMapping(PREFIX +"/FreeSpinBetNInfo")
    public Object FreeSpinBetNInfo(@RequestBody FCBaseReq vo) ;

}
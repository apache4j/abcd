package com.cloud.baowang.play.api.api.third;


import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.pt2.PT2ActionVO;
import com.cloud.baowang.play.api.vo.pt2.PT2BaseVO;
import com.cloud.baowang.play.api.vo.pt2.vo.rps.PT2BaseRsp;
import com.cloud.baowang.play.api.vo.pt2.vo.settle.GameRoundResultVO;
import com.cloud.baowang.play.api.vo.pt2.vo.settle.TransferFundsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(contextId = "pt2-api", value = ApiConstants.NAME)
@Tag(name = "pt2真人,电子")
public interface PT2GameApi {

    String PREFIX = ApiConstants.PREFIX + "pt2/api/";

    @Operation(summary = "获取余额")
    @PostMapping(PREFIX + "authenticate")
    PT2BaseRsp authenticate(PT2ActionVO actionVo);


    @Operation(summary = "获取余额")
    @PostMapping(PREFIX + "bet")
    PT2BaseRsp bet(PT2ActionVO actionVo);

    @Operation(summary = "获取余额")
    @PostMapping(PREFIX + "gameroundresult")
    PT2BaseRsp gameroundresult(GameRoundResultVO actionVo);

    @Operation(summary = "获取余额")
    @PostMapping(PREFIX + "getbalance")
    PT2BaseRsp getbalance(PT2BaseVO actionVo);

    @Operation(summary = "获取余额")
    @PostMapping(PREFIX + "logout")
    PT2BaseRsp logout(PT2BaseVO actionVo);

    @Operation(summary = "获取余额")
    @PostMapping(PREFIX + "transferFunds")
    PT2BaseRsp transferFunds(TransferFundsVO actionVo);


}

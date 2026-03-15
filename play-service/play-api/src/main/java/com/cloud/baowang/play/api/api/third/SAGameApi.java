package com.cloud.baowang.play.api.api.third;

import com.cloud.baowang.play.api.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "sa-api", value = ApiConstants.NAME)
@Tag(name = "SA-游戏")
public interface SAGameApi {
    String PREFIX = ApiConstants.PREFIX + "/sa/api/";

    @Operation(summary = "查询余额")
    @PostMapping(PREFIX + "queryBalance")
    String queryBalance(@RequestBody String data);


    @Operation(summary = "下注")
    @PostMapping(PREFIX + "placeBet")
    String placeBet(@RequestBody String data);

    @Operation(summary = "下注结果输")
    @PostMapping(PREFIX + "playerLost")
    String playerLost(@RequestBody String data);

    @Operation(summary = "取消下注")
    @PostMapping(PREFIX + "placeBetCancel")
    String placeBetCancel(@RequestBody String data);


    @Operation(summary = "派彩")
    @PostMapping(PREFIX + "playerWin")
    String playerWin(@RequestBody String data);

    @Operation(summary = "奖励")
    @PostMapping(PREFIX + "balanceAdjustment")
    String balanceAdjustment(@RequestBody String data);





}

package com.cloud.baowang.play.api.api.third;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.base.FTGErrorRes;
import com.cloud.baowang.play.api.vo.ftg.FTGBetReq;
import com.cloud.baowang.play.api.vo.ftg.FTGCancelReq;
import com.cloud.baowang.play.api.vo.ftg.FTGGetBalanceReq;
import com.cloud.baowang.play.api.vo.ftg.FTGGetBalanceRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "ftg-api", value = ApiConstants.NAME)
@Tag(name = "FTG-游戏")
public interface FTGGameApi {
    String PREFIX = ApiConstants.PREFIX + "/ftg/api/";

    @Operation(summary = "查询余额")
    @PostMapping(PREFIX + "queryBalance")
    FTGErrorRes<JSONObject> queryBalance(@RequestBody FTGGetBalanceReq req);


    @Operation(summary = "下注")
    @PostMapping(PREFIX + "bet")
    FTGErrorRes<JSONObject> bet(@RequestBody FTGBetReq req);

    @Operation(summary = "回滚")
    @PostMapping(PREFIX + "cancelBet")
    FTGErrorRes<JSONObject> cancelBet(@RequestBody FTGCancelReq req);

    @Operation(summary = "派彩")
    @PostMapping(PREFIX + "payOut")
    FTGErrorRes<JSONObject> payOut(@RequestBody FTGBetReq req);



}

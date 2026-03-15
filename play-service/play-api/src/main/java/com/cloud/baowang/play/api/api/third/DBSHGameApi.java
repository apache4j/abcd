package com.cloud.baowang.play.api.api.third;


import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.db.rsp.sh.DBSHBaseRsp;
import com.cloud.baowang.play.api.vo.db.sh.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "db-sh-api", value = ApiConstants.NAME)
@Tag(name = "db真人")
public interface DBSHGameApi {

    String PREFIX = ApiConstants.PREFIX + "db/sh/api/";

    @Operation(summary = "获取余额")
    @PostMapping(PREFIX + "getBalance")
    DBSHBaseRsp<SHOrderRspData> getBalance(@RequestBody SHBalanceQueryVO reqVO);

    @Operation(summary = "获取余额")
    @PostMapping(PREFIX + "getBatchBalance")
    DBSHBaseRsp<List<SHOrderRspData>> getBatchBalance(@RequestBody SHBalanceQueryBatchVO reqVO);

    @Operation(summary = "下注")
    @PostMapping(PREFIX + "betConfirm")
    DBSHBaseRsp<BetRspParams> betConfirm(@RequestBody BetRequestVO reqVO);

    @Operation(summary = "下注取消")
    @PostMapping(PREFIX + "betCancel")
    DBSHBaseRsp<BetRspParams> betCancel(@RequestBody DBSHRequestVO reqVO);

    @Operation(summary = "派彩")
    @PostMapping(PREFIX + "gamePayout")
    DBSHBaseRsp<BetRspParams> gamePayout(@RequestBody DBSHRequestVO reqVO);

    @Operation(summary = "活动派彩")
    @PostMapping(PREFIX + "activityPayout")
    DBSHBaseRsp<BetRspParams> activityPayout(@RequestBody DBSHRequestVO reqVO);

    @Operation(summary = "下注")
    @PostMapping(PREFIX + "playerbetting")
    DBSHBaseRsp<BetRspParams> playerbetting(@RequestBody DBSHRequestVO reqVO);

    @Operation(summary = "活动返利")
    @PostMapping(PREFIX + "activityRebate")
    DBSHBaseRsp<BetRspParams> activityRebate(@RequestBody DBSHRequestVO reqVO);
}

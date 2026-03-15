package com.cloud.baowang.play.api.api.third;


import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.db.evg.vo.DBEVGBasicInfo;
import com.cloud.baowang.play.api.vo.db.rsp.evg.DBEVGBaseRsp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "db-scratch-api", value = ApiConstants.NAME)
@Tag(name = "db刮刮乐")
public interface DBScratchGameApi {
    String PREFIX = ApiConstants.PREFIX + "db/scratch/api/";

    @Operation(summary = "获取余额")
    @PostMapping(PREFIX + "queryBalance")
    DBEVGBaseRsp queryBalance(@RequestBody DBEVGBasicInfo evgBasicInfo,@RequestParam("req") String req);

    @Operation(summary = "下注/派彩")
    @PostMapping(PREFIX + "balanceChange")
    DBEVGBaseRsp balanceChange(@RequestBody DBEVGBasicInfo evgBasicInfo,@RequestParam("req") String req);

    @Operation(summary = "查询订单")
    @PostMapping(PREFIX + "queryOrderStatus")
    DBEVGBaseRsp queryOrderStatus(@RequestBody DBEVGBasicInfo evgBasicInfo,@RequestParam("req") String req);
}

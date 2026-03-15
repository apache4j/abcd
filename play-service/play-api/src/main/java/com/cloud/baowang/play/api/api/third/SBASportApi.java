package com.cloud.baowang.play.api.api.third;

import com.cloud.baowang.play.api.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "sba-sport-api", value = ApiConstants.NAME)
@Tag(name = "沙巴体育-接口")
public interface SBASportApi {
    String PREFIX = ApiConstants.PREFIX + "/third-pull-bet/api/";


    @Operation(summary = "拉取未处理的订单状态")
    @PostMapping(PREFIX + "sbaPullStatus")
    void sbaPullStatus(@RequestParam("orderStatus") Integer orderStatus,
                       @RequestParam("order") String order);

    @Operation(summary = "拉取有已达重试上限的注单")
    @PostMapping(PREFIX + "sbaPullReachLimitTrans")
    void sbaPullReachLimitTrans(@RequestParam("date") String date);


    @Operation(summary = "拉取沙巴体育-未来赛事")
    @PostMapping(PREFIX + "sbaPullGameEventsTask")
    void sbaPullGameEventsTask();


    @Operation(summary = "拉取沙巴体育-联赛基础信息")
    @PostMapping(PREFIX + "sbaPullEventInfo")
    void sbaPullEventInfo();
}

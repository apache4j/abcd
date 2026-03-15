package com.cloud.baowang.play.api.api.rebate;

import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.vip.VIPSendReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "userAwardApi", value = ApiConstants.NAME)
@Tag(name = "用户VIP奖励相关api")
public interface UserAwardApi {

    String PREFIX = ApiConstants.PREFIX + "/award/api/";

    @Operation(summary = "周返水奖励")
    @PostMapping(PREFIX + "weekRebate")
    void weekRebate(@RequestBody VIPSendReqVO vo);

    @Operation(summary = "月返水奖励")
    @PostMapping(PREFIX + "monthRebate")
    void monthRebate(@RequestBody VIPSendReqVO vo);

    @Operation(summary = "周体育返水奖励")
    @PostMapping(PREFIX + "weekSportRebate")
    void weekSportRebate(@RequestBody VIPSendReqVO vo);
}

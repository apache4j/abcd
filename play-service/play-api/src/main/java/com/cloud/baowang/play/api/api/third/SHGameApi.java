package com.cloud.baowang.play.api.api.third;

import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.base.ShBaseRes;
import com.cloud.baowang.play.api.vo.sh.ShAdjustBalanceReq;
import com.cloud.baowang.play.api.vo.sh.ShAdjustBalanceRes;
import com.cloud.baowang.play.api.vo.sh.ShBalanceRes;
import com.cloud.baowang.play.api.vo.sh.ShQueryBalanceReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "sh-game-api", value = ApiConstants.NAME)
@Tag(name = "WinTo真人")
public interface SHGameApi {
    String PREFIX = ApiConstants.PREFIX + "/sh/api/";

    @Operation(summary = "查询余额")
    @PostMapping(PREFIX + "queryBalance")
    ShBaseRes<ShBalanceRes> queryBalance(@RequestBody ShQueryBalanceReq request);

    @Operation(summary = "账变接口")
    @PostMapping(PREFIX + "adjustBalance")
    ShBaseRes<ShAdjustBalanceRes> adjustBalance(@RequestBody ShAdjustBalanceReq request);

}

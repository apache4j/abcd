package com.cloud.baowang.play.api.api.third;

import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.acelt.*;
import com.cloud.baowang.play.api.vo.base.ACELTBaseRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "wp-acelt-api", value = ApiConstants.NAME)
@Tag(name = "WinTo-彩票")
public interface ACELTGameApi {
    String PREFIX = ApiConstants.PREFIX + "/acelt/api/";


    @Operation(summary = "查询余额")
    @PostMapping(PREFIX + "queryBalance")
    ACELTBaseRes<ACELTGetBalanceRes> queryBalance(@RequestBody ACELTGetBalanceReq aceltGetBalanceReq);

    @Operation(summary = "账变接口")
    @PostMapping(PREFIX + "accountChange")
    ACELTBaseRes<ACELTAccountCheangeRes> accountChange(@RequestBody ACELTAccountCheangeReq request);

    @Operation(summary = "账变接口")
    @PostMapping(PREFIX + "accountChangeCallBack")
    ACELTBaseRes<ACELTAccountCheangeRes> accountChangeCallBack(@RequestBody ACELTAccountChangeCallBackReq request);

}

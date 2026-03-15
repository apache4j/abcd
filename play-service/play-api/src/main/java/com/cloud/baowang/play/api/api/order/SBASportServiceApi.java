package com.cloud.baowang.play.api.api.order;

import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.sba.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "sbaSportApi", value = ApiConstants.NAME)
@Tag(name = "沙巴推送接口")
public interface SBASportServiceApi {

    String PREFIX = ApiConstants.PREFIX + "/sbaSport/api/";

    @Operation(summary = "沙巴推送")
    @PostMapping(PREFIX + "toSBAction")
    String toSBAction(@RequestBody SBBaseReq req, @RequestParam("code") String code);

}

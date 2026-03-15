package com.cloud.baowang.play.api.api.third;

import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.cmd.CmdReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "cmd-api", value = ApiConstants.NAME)
@Tag(name = "CMD")
public interface CmdGameApi {
    String PREFIX = ApiConstants.PREFIX + "/cmd/api/";

    @Operation(summary = "doAction")
    @PostMapping(PREFIX + "doAction")
    String doAction(@RequestBody CmdReq cmdReq);


    @Operation(summary = "获取用户鉴权")
    @PostMapping(PREFIX + "token")
    String token (@RequestBody String token);


}

package com.cloud.baowang.play.api.api.third;


import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.sexy.req.SexyActionVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "sexy-api", value = ApiConstants.NAME)
@Tag(name = "sexy真人")
public interface SexyGameApi {

    String PREFIX = ApiConstants.PREFIX + "sexy/api/";

    @Operation(summary = "获取余额")
    @PostMapping(PREFIX + "action")
    JSONObject action(@RequestBody SexyActionVo actionVo);
}

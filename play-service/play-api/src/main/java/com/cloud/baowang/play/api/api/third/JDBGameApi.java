package com.cloud.baowang.play.api.api.third;


import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.play.api.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "jdb-api", value = ApiConstants.NAME)
@Tag(name = "jdb电子")
public interface JDBGameApi {

    String PREFIX = ApiConstants.PREFIX + "/jdb/api/";
    @Operation(summary = "jdb电子回调")
    @PostMapping(PREFIX + "action")
    JSONObject action(@RequestParam("x") String x);
}

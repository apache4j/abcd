package com.cloud.baowang.user.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteUserJobHandlerApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员XXL-JOB 服务")
public interface UserJobHandlerApi {

    String PREFIX = ApiConstants.PREFIX + "/userJobHandler/api/";

    @Operation(summary = "会员离线天数")
    @PostMapping(value = PREFIX + "userOfflineDays")
    void userOfflineDays();

    @Operation(summary = "统计会员注册时长")
    @PostMapping(value = PREFIX + "userRegisterDays")
    void userRegisterDays(@RequestParam("siteCode") String siteCode);
}

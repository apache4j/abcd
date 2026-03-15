package com.cloud.baowang.user.api.api;

import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.userreview.UserAddVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteUserAddApi", value = ApiConstants.NAME)
@Tag(name = "RPC 新增会员配置 服务")
public interface UserAddApi {

    String PREFIX = ApiConstants.PREFIX + "/userAdd/api/";

    @Operation(summary = "新增会员")
    @PostMapping(value = PREFIX + "addUser")
    ResponseVO<?> addUser(@RequestBody UserAddVO vo, @RequestParam("adminId") String adminId, @RequestParam("adminName") String adminName);
}

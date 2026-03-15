package com.cloud.baowang.user.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.user.UserLoginDeviceVO;
import com.cloud.baowang.user.api.vo.user.request.UserDeviceReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteUserLoginDeviceApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 会员登录设备记录")
public interface UserLoginDeviceApi {

    String PREFIX = ApiConstants.PREFIX + "/user-login-device/api/";

    @Operation(summary = "查询会员登录设备记录")
    @PostMapping(value = PREFIX + "queryUserLoginDevice")
    ResponseVO<Page<UserLoginDeviceVO>> queryUserLoginDevice(@RequestBody UserDeviceReqVO requestVO);


    @Operation(summary = "删除设备")
    @PostMapping(value = PREFIX + "deleteUserLoginDevice")
    ResponseVO<Void> deleteUserLoginDevice(@RequestBody IdVO idVO);
}

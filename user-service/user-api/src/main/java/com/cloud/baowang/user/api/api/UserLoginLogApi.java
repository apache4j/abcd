package com.cloud.baowang.user.api.api;

import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
import com.cloud.baowang.user.api.vo.user.UserLoginLogVO;
import com.cloud.baowang.user.api.vo.user.UserLoginRequestVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteUserLoginLogApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 会员登录记录")
public interface UserLoginLogApi {

    String PREFIX = ApiConstants.PREFIX + "/user-login/api/";

    @Operation(summary = "查询会员登录记录")
    @PostMapping(value = PREFIX + "queryUserLogin")
    ResponseVO<UserLoginLogVO> queryUserLogin(@RequestBody UserLoginRequestVO requestVO);

    @Operation(summary = "查询会员登录记录-总记录数")
    @PostMapping(value = PREFIX + "getTotalCount")
    Long getTotalCount(@RequestBody UserLoginRequestVO vo);


    @Operation(summary = "插入会员登录记录")
    @PostMapping(value = PREFIX + "insertUserLogin")
    ResponseVO<Void> insertUserLogin(@RequestBody UserLoginInfoVO userLoginInfoVO);
}

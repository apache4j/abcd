package com.cloud.baowang.admin.controller.user;

import com.cloud.baowang.admin.service.UserLoginLogService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.user.UserLoginLogVO;
import com.cloud.baowang.user.api.vo.user.UserLoginRequestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: 小智
 */
@Tag(name = "会员-会员管理-会员登录记录")
@RestController
@RequestMapping("/user-login-log/api")
@AllArgsConstructor
public class UserLoginLogController {

    private final UserLoginLogService userLoginLogService;

    @Operation(summary = "会员登录日志")
    @PostMapping(value = "/queryUserLogin")
    public ResponseVO<UserLoginLogVO> queryUserLogin( @Valid @RequestBody UserLoginRequestVO requestVO) {
        return userLoginLogService.queryUserLogin(requestVO);
    }
}

package com.cloud.baowang.user.api.api;

import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.UserGuideVO;
import com.cloud.baowang.user.api.vo.user.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteUserLoginApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员注册登录 服务")
public interface UserLoginApi {

    String PREFIX = ApiConstants.PREFIX + "/userLogin/api";

    @Operation(summary = "会员注册")
    @PostMapping(value = PREFIX + "/userRegister")
    ResponseVO<?> userRegister(@RequestBody UserRegisterVO vo);

    @Operation(summary = "发送邮箱验证码")
    @PostMapping(value = PREFIX + "/sendMail")
    ResponseVO<?> sendMail(@RequestBody LoginGetMailCodeVO vo);

    @Operation(summary = "发送手机验证码")
    @PostMapping(value = PREFIX + "/sendSms")
    ResponseVO sendSms(@RequestBody LoginGetSmsCodeVO vo);

    @Operation(summary = "会员登录")
    @PostMapping(value = PREFIX + "/userLogin")
    ResponseVO<UserInfoVO> userLogin(@RequestBody UserLoginVO vo);

    @Operation(summary = "验证码校验")
    @PostMapping(value = PREFIX + "/checkVerifyCode")
    ResponseVO checkVerifyCode(@RequestBody VerifyCodeVO vo);

    @Operation(summary = "重置密码")
    @PostMapping(value = PREFIX + "/resetPassword")
    ResponseVO resetPassword(@RequestBody ResetPasswordVO vo);

    @PostMapping(value = PREFIX + "/setNewUserGuide")
    ResponseVO<?> setNewUserGuide(@RequestBody UserGuideVO vo);
}
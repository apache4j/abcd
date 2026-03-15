package com.cloud.baowang.user.api.api;

import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.user.*;
import com.cloud.baowang.user.api.vo.user.reponse.UserGlobalSetResVO;
import com.cloud.baowang.user.api.vo.user.request.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteUserGlobalSetApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员全局设置 服务")
public interface UserGlobalSetApi {

    String PREFIX = ApiConstants.PREFIX + "/userGlobalSet/api";

    @Operation(summary = "绑定邮箱或手机")
    @PostMapping(value = PREFIX + "/bindAccount")
    ResponseVO<?> bindAccount(@RequestBody BindAccountVO vo);


    @Operation(summary = "修改密码")
    @PostMapping(value = PREFIX + "/changePassword")
    ResponseVO changePassword(@RequestBody ChangePasswordReqVO vo);

    @Operation(summary = "安全中心基本信息")
    @PostMapping(value = PREFIX + "/getUserGlobalSetInfo")
    UserGlobalSetResVO getUserGlobalSetInfo(@RequestBody UserQueryVO queryVO);


    @Operation(summary = "更新用户头像与名称")
    @PostMapping(value = PREFIX + "/setUserInfo")
    ResponseVO<Boolean> setUserInfo(@RequestBody UserEditVO vo);

    @Operation(summary = "发送邮箱验证码")
    @PostMapping(value = PREFIX + "/sendMail")
    ResponseVO<?> sendMail(@RequestBody UserGetMailCodeVO vo);

    @Operation(summary = "发送手机验证码")
    @PostMapping(value = PREFIX + "/sendSms")
    ResponseVO sendSms(@RequestBody UserGetSmsCodeVO vo);

    @Operation(summary = "校验邮箱/手机验证码")
    @PostMapping(value = PREFIX + "/checkVerifyCode")
    ResponseVO<ResultCode> checkVerifyCode(@RequestBody SafeVerifyCodeVO vo);

    @Operation(summary = "设置交易密码")
    @PostMapping(value = PREFIX + "/setWithdrawPwd")
    ResponseVO setWithdrawPwd(@RequestBody SetWithdrawalPasswordReqVO vo);

    @Operation(summary = "修改交易密码")
    @PostMapping(value = PREFIX + "/changeWithdrawPwd")
    ResponseVO changeWithdrawPwd(@RequestBody ChangePasswordReqVO vo);

    @Operation(summary = "找回交易密码")
    @PostMapping(value = PREFIX + "/reFindWithdrawPwd")
    ResponseVO reFindWithdrawPwd(@RequestBody ReFindWPwdVO vo);

    @Operation(summary = "找回交易密码,重置密码")
    @PostMapping(value = PREFIX + "/reSetWithdrawPwd")
    ResponseVO reSetWithdrawPwd(@RequestBody ChangeWithPasswordReqVO vo);
}
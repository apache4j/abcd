package com.cloud.baowang.user.controller.user;

import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.user.request.ChangeWithPasswordReqVO;
import com.cloud.baowang.user.api.vo.user.request.SetWithdrawalPasswordReqVO;
import com.cloud.baowang.user.api.vo.user.request.UserQueryVO;
import com.cloud.baowang.user.api.api.UserGlobalSetApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.api.UserLoginDeviceApi;
import com.cloud.baowang.user.api.vo.user.*;
import com.cloud.baowang.user.api.vo.user.reponse.UserGlobalSetResVO;
import com.cloud.baowang.user.api.vo.user.request.ChangePasswordReqVO;
import com.cloud.baowang.user.service.LoginService;
import com.cloud.baowang.user.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@Tag(name = "我的页面-安全中心")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/user-info/global")
public class UserGlobalSetController {

    private final UserGlobalSetApi userGlobalSetApi;
    private final LoginService loginService;
    private final UserLoginDeviceApi userLoginDeviceApi;
    private final UserInfoApi userInfoApi;
    private final MemberService memberService;


    @Operation(summary = "安全中心基本信息")
    @PostMapping(value = "getUserGlobalSetInfo")
    private ResponseVO<UserGlobalSetResVO> getUserGlobalSetInfo() {
        String userAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        if (StringUtils.isEmpty(userAccount) || StringUtils.isEmpty(siteCode)) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }

        UserQueryVO queryVO = UserQueryVO.builder().userAccount(userAccount).siteCode(siteCode).build();
        return ResponseVO.success(userGlobalSetApi.getUserGlobalSetInfo(queryVO));
    }

    @Operation(summary = "绑定/修改邮箱或手机")
    @PostMapping( "bindAccount")
    private ResponseVO bindAccount(@Valid @RequestBody BindAccountVO vo){
        String userAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();

        if (StringUtils.isEmpty(userAccount) || StringUtils.isEmpty(siteCode)) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }

        if (vo.getType() == 2 && StringUtils.isEmpty(vo.getAreaCode())) {
            throw new BaowangDefaultException(ResultCode.AREA_EMPTY);
        }

        vo.setUserAccount(userAccount);
        vo.setSiteCode(siteCode);

        return userGlobalSetApi.bindAccount(vo);
    }

    @Operation(summary = "发送邮箱验证码")
    @PostMapping("/sendMail")
    ResponseVO<?> sendMail(@Valid  @RequestBody UserGetMailCodeVO vo) {
        String userAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        if (StringUtils.isEmpty(userAccount) || StringUtils.isEmpty(siteCode)) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }
        vo.setSiteCode(siteCode);
        vo.setUserAccount(userAccount);
        return userGlobalSetApi.sendMail(vo);
    }

    @Operation(summary = "发送手机验证码")
    @PostMapping("/sendSms")
    ResponseVO sendSms(@Valid @RequestBody UserGetSmsCodeVO vo) {
        String userAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        if (StringUtils.isEmpty(userAccount) || StringUtils.isEmpty(siteCode)) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }

        if (StringUtils.isEmpty(vo.getAreaCode())) {
            throw new BaowangDefaultException(ResultCode.AREA_CODE_IS_EXIST);
        }

        vo.setUserAccount(userAccount);
        vo.setSiteCode(siteCode);
        return userGlobalSetApi.sendSms(vo);
    }

    @Operation(summary = "修改邮箱/手机验证码校验")
    @PostMapping("/checkVerifyCode")
    ResponseVO checkVerifyCode(@Valid @RequestBody SafeVerifyCodeVO vo) {
        String userAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        if (StringUtils.isEmpty(userAccount) || StringUtils.isEmpty(siteCode)) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }
        vo.setSiteCode(siteCode);
        vo.setUserAccount(userAccount);
        return userGlobalSetApi.checkVerifyCode(vo);
    }

    @Operation(summary = "修改密码")
    @PostMapping( "changePassword")
    private ResponseVO changePassword(@Valid @RequestBody ChangePasswordReqVO vo){
        String userAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        if (StringUtils.isEmpty(userAccount) || StringUtils.isEmpty(siteCode)) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }
        vo.setUserAccount(userAccount);
        vo.setSiteCode(siteCode);
        memberService.checkPwChangeTimeLimit(CurrReqUtils.getOneId());
        ResponseVO responseVO = userGlobalSetApi.changePassword(vo);
        if (responseVO.isOk()){
            memberService.incrPwChangeTimeLimit(CurrReqUtils.getOneId());
        }
        return responseVO;
    }

    @Operation(summary = "设置交易密码")
    @PostMapping( "setWithdrawPwd")
    private ResponseVO setWithdrawPwd(@Valid @RequestBody SetWithdrawalPasswordReqVO vo){
        String userAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        if (StringUtils.isEmpty(userAccount) || StringUtils.isEmpty(siteCode)) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }
        vo.setUserAccount(userAccount);
        vo.setSiteCode(siteCode);

        return userGlobalSetApi.setWithdrawPwd(vo);
    }

    @Operation(summary = "修改交易密码")
    @PostMapping( "changeWithdrawPwd")
    public ResponseVO changeWithdrawPwd(@Valid @RequestBody ChangePasswordReqVO vo){
        String userAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        if (StringUtils.isEmpty(userAccount) || StringUtils.isEmpty(siteCode)) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }
        vo.setUserAccount(userAccount);
        vo.setSiteCode(siteCode);

        return userGlobalSetApi.changeWithdrawPwd(vo);
    }

    @Operation(summary = "找回交易密码第一步")
    @PostMapping("/reFindWithdrawPwd")
    public ResponseVO reFindWithdrawPwd(@Valid @RequestBody ReFindWPwdVO vo) {
        String userAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();

        if (StringUtils.isEmpty(userAccount) || StringUtils.isEmpty(siteCode)) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }
        vo.setUserAccount(userAccount);
        vo.setSiteCode(siteCode);

        return userGlobalSetApi.reFindWithdrawPwd(vo);
    }

    @Operation(summary = "找回交易密码-重置交易密码")
    @PostMapping("/reSetWithdrawPwd")
    public ResponseVO reSetWithdrawPwd(@Valid @RequestBody ChangeWithPasswordReqVO vo) {
        if(vo.getVerifyCode() == null) {
            throw new BaowangDefaultException(ResultCode.CODE_IS_EMPTY);
        }

        String userAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();

        if (StringUtils.isEmpty(userAccount) || StringUtils.isEmpty(siteCode)) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }
        vo.setUserAccount(userAccount);
        vo.setSiteCode(siteCode);

        return userGlobalSetApi.reSetWithdrawPwd(vo);
    }
}

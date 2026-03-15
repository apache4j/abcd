package com.cloud.baowang.user.controller.login;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.captcha20230305.Client;
import com.aliyun.captcha20230305.models.VerifyIntelligentCaptchaRequest;
import com.aliyun.captcha20230305.models.VerifyIntelligentCaptchaResponse;
import com.aliyun.tea.TeaException;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.UserChecker;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.constants.UserConstant;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.user.request.UserQueryVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.api.UserLoginApi;
import com.cloud.baowang.user.api.vo.user.*;
import com.cloud.baowang.user.service.LoginService;
import com.cloud.baowang.user.vo.VerifyReq;
import com.cloud.baowang.user.vo.VerifyRsp;
import com.cloud.baowang.user.vo.VerifyVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author fangfei
 */

@Slf4j
@Tag(name = "会员注册登录")
@RestController
@RequestMapping(value = "/login/api")
public class UserLoginController {

    private static String url = "https://api.hcaptcha.com/siteverify";
    @Autowired
    private  UserInfoApi userInfoApi;
    @Autowired
    private  UserLoginApi userLoginApi;
    @Autowired
    private  LoginService loginService;
    @Autowired
    private  Client codeClient;
    @Autowired
    private  SystemParamApi systemParamApi;
    @Autowired
    private  SiteCurrencyInfoApi siteCurrencyInfoApi;
    @Autowired
    private  SystemDictConfigApi systemDictConfigApi;

    @Value("${spring.profiles.active}")
    private String currentEnv;

/*
    @Operation(summary = "获取验证码--测试使用")
    @PostMapping("/getVerifyCode")   //fixme
    public String getVerifyCode(@RequestParam("userAccount") String userAccount) {
        String siteCode = CurrReqUtils.getSiteCode();
        userInfoApi.getUserInfoByAccountAndSiteCode(userAccount, siteCode);
        String key = String.format(RedisConstants.VERIFY_CODE_CACHE, siteCode, userAccount);
        return RedisUtil.getValue(key);
    }
*/

    public Boolean getVerifyConfig() {
        Map<String, String> map = systemParamApi.getSystemParamMapInner("captcha_switch");
        if (map != null && map.size() > 0) {
            if ("0".equals(map.get("0"))) {
                return false;
            }
        }

        return true;
    }

    @Operation(summary = "验证码验证")
    @PostMapping("/verifyCode")
    public ResponseVO verifyCode(@RequestBody VerifyReq req) {
        if (!getVerifyConfig()) {
            return ResponseVO.success(VerifyRsp.builder().captchaResult(true).build());
        }

        try {
            VerifyVO verifyVO = JSONObject.parseObject(req.getCaptchaVerifyParam(), VerifyVO.class);
            VerifyIntelligentCaptchaRequest request = new VerifyIntelligentCaptchaRequest();
            request.sceneId = verifyVO.getSceneId();
            // 前端传来的验证参数 CaptchaVerifyParam
            request.captchaVerifyParam = req.getCaptchaVerifyParam();
            // ====================== 3. 发起请求） ======================

            VerifyIntelligentCaptchaResponse resp = codeClient.verifyIntelligentCaptcha(request);
            Boolean captchaVerifyResult = resp.body.result.verifyResult;
                String captchaVerifyCode = resp.body.result.verifyCode;
            if(captchaVerifyCode.equals("F009")) {  //fixme
                RedisUtil.setValue("verifyCode::" + verifyVO.getCertifyId(), "1", 5L, TimeUnit.MINUTES);
                return ResponseVO.success(VerifyRsp.builder().captchaResult(true).build());
            }
            if (!captchaVerifyResult) {
                log.info("验证码不通过：{}", captchaVerifyCode);
                return ResponseVO.success(VerifyRsp.builder().captchaResult(false).build());
            }

            RedisUtil.setValue("verifyCode::" + verifyVO.getCertifyId(), "1", 5L, TimeUnit.MINUTES);
        } catch (TeaException error) {
            log.info("验证码错误error：", error);
        } catch (Exception _error) {
//            TeaException error = new TeaException(_error.getMessage(), _error);
            log.info("验证码错误_error：", _error);
        }


        return ResponseVO.success(VerifyRsp.builder().captchaResult(true).build());
    }


    @Operation(summary = "会员注册")
    @PostMapping("/userRegister")
    ResponseVO<UserLoginRspVO>
    userRegister(@Valid @RequestBody UserRegisterVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        if (getVerifyConfig()) {
            String verify = RedisUtil.getValue("verifyCode::" + vo.getCertifyId());
            if (verify == null) {
                throw new BaowangDefaultException(ResultCode.CODE_ERROR);
            }
        }

        if (UserChecker.checkPassword(vo.getPassword())) {
            return ResponseVO.fail(ResultCode.PASSWORD_ERROR);
        }

        if (!vo.getPassword().equals(vo.getConfirmPassword())) {
            return ResponseVO.fail(ResultCode.PASSWORD_CONFIRM_ERROR);
        }

        SiteCurrencyInfoRespVO siteCurrencyInfoRespVO = siteCurrencyInfoApi.getByCurrencyCode(siteCode, vo.getMainCurrency());
        if (EnableStatusEnum.DISABLE.getCode().equals(siteCurrencyInfoRespVO.getStatus())) {
            return ResponseVO.fail(ResultCode.CURRENCY_FORBID);
        }

        if (!checkIpCountConfigMax(siteCode,CurrReqUtils.getReqIp())) {
            SystemDictConfigRespVO configValue = systemDictConfigApi.getByCode(DictCodeConfigEnums.SITE_IP_MAXCOUNT.getCode(),CurrReqUtils.getSiteCode()).getData();
            return ResponseVO.fail(ResultCode.CURRENCY_FORBID,configValue.getHintInfo());
        }
        if (RedisUtil.getValue("register::" + siteCode + "::" + vo.getUserAccount()) != null) {
            return ResponseVO.fail(ResultCode.ACCOUNT_IS_EXIST);
        }
        vo.setVersion(CurrReqUtils.getReqVersion());
        vo.setDeviceNo(CurrReqUtils.getReqDeviceId());
        return loginService.submitRegister(vo);
    }

    private boolean checkIpCountConfigMax(String siteCode,String ip){
        //默认没通过
        return Optional.ofNullable(systemDictConfigApi.getByCode(DictCodeConfigEnums.SITE_IP_MAXCOUNT.getCode(),siteCode)).map(data ->{
            Integer maxcount=  Integer.parseInt(data.getData().getConfigParam());
            return userInfoApi.checkUserIpMax(maxcount,ip,siteCode);
        }).orElse(false);
    }


    @Operation(summary = "会员登录")
    @PostMapping("/userLogin")
    ResponseVO<UserLoginRspVO> userLogin(@RequestBody UserLoginVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        log.info("userLogin获取siteCode：{}", siteCode);
        //校验图形验证码是否已经正确校验
        vo.setSubmitKey(true);
        vo.setIsRegister(false);
        if (getVerifyConfig()) {
            String verify = RedisUtil.getValue("verifyCode::" + vo.getCertifyId());
            //如无必须， 不能注释验证码校验！
            if (verify == null || verify.equals("0")) {
                throw new BaowangDefaultException(ResultCode.CODE_ERROR);
            }
        }

        vo.setDeviceNo(CurrReqUtils.getReqDeviceId());
        vo.setVersion(CurrReqUtils.getReqVersion());
        return loginService.userLogin(vo);
    }
    /*@Operation(summary = "新手指引步骤记录")
    @PostMapping("/setNewUserGuide")
    ResponseVO<?> setNewUserGuide(@RequestBody UserGuideVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        vo.setSiteCode(siteCode);
        vo.setUserId(CurrReqUtils.getOneId());
        vo.setUserAccount(CurrReqUtils.getAccount());
        return loginService.setNewUserGuide(vo);
    }*/

    @Operation(summary = "发送邮箱验证码")
    @PostMapping("/sendMail")
    ResponseVO<?> sendMail(@Valid  @RequestBody LoginGetMailCodeVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        log.info("发送邮箱验证码 获取siteCode：{}", siteCode);

        vo.setSiteCode(siteCode);
        //校验邮箱是否是会员绑定的邮箱
        UserQueryVO queryVO = UserQueryVO.builder().userAccount(vo.getUserAccount()).siteCode(siteCode).build();
        UserInfoVO userInfoVO = userInfoApi.getUserInfoByQueryVO(queryVO);
        if (userInfoVO.getEmail() == null) {
            return ResponseVO.fail(ResultCode.EMAIL_NOT_EXIST);
        }
        if (!userInfoVO.getEmail().equals(vo.getEmail())) {
            return ResponseVO.fail(ResultCode.SELECT_RIGHT_EMAIL);
        }
        vo.setSiteCode(siteCode);
        return userLoginApi.sendMail(vo);
    }

    @Operation(summary = "发送手机验证码")
    @PostMapping("/sendSms")
    ResponseVO sendSms(@Valid @RequestBody LoginGetSmsCodeVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        log.info("发送手机验证码 获取siteCode：{}", siteCode);
        //判断邮箱手机是否存在
        vo.setSiteCode(siteCode);
        //校验邮箱是否是会员绑定的邮箱
        UserQueryVO queryVO = UserQueryVO.builder().userAccount(vo.getUserAccount()).siteCode(siteCode).build();
        UserInfoVO userInfoVO = userInfoApi.getUserInfoByQueryVO(queryVO);
        if (userInfoVO.getPhone() == null) {
            return ResponseVO.fail(ResultCode.PHONE_NOT_EXIST);
        }
        if (vo.getAreaCode().startsWith("+")) {
            vo.setAreaCode(vo.getAreaCode().substring(1));
        }
        vo.setAreaCode(vo.getAreaCode().trim());
        if (!vo.getAreaCode().equals(userInfoVO.getAreaCode()) || !vo.getPhone().equals(userInfoVO.getPhone())) {
            return ResponseVO.fail(ResultCode.SELECT_RIGHT_PHONE);
        }
        vo.setSiteCode(siteCode);
        return userLoginApi.sendSms(vo);
    }

    @Operation(summary = "邮箱手机验证码校验")
    @PostMapping("/checkVerifyCode")
    ResponseVO checkVerifyCode(@Valid @RequestBody VerifyCodeVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        //判断邮箱手机是否存在
        vo.setSiteCode(siteCode);
        //checkAccountExist(vo.getAccount(), vo.getSiteCode(), vo.getType());

        //校验邮箱是否是会员绑定的邮箱
        UserQueryVO userQueryVO = UserQueryVO.builder().userAccount(vo.getUserAccount()).siteCode(siteCode).build();
        UserInfoVO user = userInfoApi.getUserInfoByQueryVO(userQueryVO);
        if (vo.getType() == 1) {
            if (user.getEmail() == null) {
                return ResponseVO.fail(ResultCode.EMAIL_NOT_EXIST);
            }
            if (!user.getEmail().equals(vo.getAccount())) {
                return ResponseVO.fail(ResultCode.SELECT_RIGHT_EMAIL);
            }
        } else {
            if (user.getPhone() == null) {
                return ResponseVO.fail(ResultCode.PHONE_NOT_EXIST);
            }

            if (vo.getAreaCode().startsWith("+")) {
                vo.setAreaCode(vo.getAreaCode().substring(1));
            }
            vo.setAreaCode(vo.getAreaCode().trim());
            if (vo.getAreaCode() == null || !vo.getAreaCode().equals(user.getAreaCode()) || !vo.getAccount().equals(user.getPhone())) {
                return ResponseVO.fail(ResultCode.SELECT_RIGHT_PHONE);
            }
        }

        vo.setSiteCode(siteCode);
        if("dev".equalsIgnoreCase(currentEnv)){
            if(!siteCode.equalsIgnoreCase(vo.getSiteCode())){
                return ResponseVO.fail(ResultCode.PARAM_ERROR);
            }
            if(!UserConstant.DEFAULT_CODE.equalsIgnoreCase(vo.getVerifyCode())){
                return ResponseVO.fail(ResultCode.PARAM_ERROR);
            }
            return ResponseVO.success();
        }
        return userLoginApi.checkVerifyCode(vo);
    }

    @Operation(summary = "忘记密码提交账号")
    @PostMapping("/submitAccount")
    ResponseVO submitAccount(@Valid @RequestBody AccountCheckVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();

        UserQueryVO queryVO = UserQueryVO.builder().userAccount(vo.getUserAccount()).siteCode(siteCode).build();
        UserInfoVO userInfoVO = userInfoApi.getUserInfoByQueryVO(queryVO);
        if (userInfoVO == null) {
            return ResponseVO.fail(ResultCode.ACCOUNT_ERROR);
        }

        return ResponseVO.success();
    }

    @Operation(summary = "重置密码")
    @PostMapping("/resetPassword")
    ResponseVO resetPassword(@Valid @RequestBody ResetPasswordVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        vo.setSiteCode(siteCode);
        return userLoginApi.resetPassword(vo);
    }

    @Operation(summary = "登出")
    @PostMapping("/logout")
    ResponseVO logout() {
        String userAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        String userId = CurrReqUtils.getOneId();
        if (StringUtils.isEmpty(userAccount) || StringUtils.isEmpty(siteCode)) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);

        loginService.logout(userInfoVO);

        return ResponseVO.success();
    }

}



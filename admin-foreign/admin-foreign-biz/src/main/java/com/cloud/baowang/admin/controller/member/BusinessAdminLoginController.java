package com.cloud.baowang.admin.controller.member;

import cn.hutool.core.collection.CollectionUtil;
import com.cloud.baowang.admin.service.LoginInfoService;
import com.cloud.baowang.admin.service.PasswordService;
import com.cloud.baowang.admin.service.TokenService;
import com.cloud.baowang.admin.utils.auth.AuthUtil;
import com.cloud.baowang.admin.utils.auth.SecurityUtils;
import com.cloud.baowang.common.auth.util.AdminAuthUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.constants.TokenConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.GoogleAuthUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.core.vo.IPRespVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.utils.IpAPICoUtils;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.member.BusinessAdminApi;
import com.cloud.baowang.system.api.vo.adminLogin.*;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.api.vo.member.BusinessAdminResetPasswordVO;
import com.cloud.baowang.system.api.vo.member.BusinessAdminVO;
import com.cloud.baowang.system.api.vo.member.BusinessLoginInfoAddVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums.GOOGLE_VERIFY_MAX_TIMES;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("business_admin_login/api")
@Tag(name = "中控后台登录")
public class BusinessAdminLoginController {

    private final TokenService tokenService;

    private final BusinessAdminApi businessAdminApi;

    private final PasswordService passwordService;

    private final LoginInfoService loginInfoService;

    private final SystemDictConfigApi systemDictConfigApi;

    @PostMapping("checkAccount")
    @Operation(summary = "检查用户是否已设置google密钥, true 已设置  false 未设置")
    public ResponseVO<Boolean> checkAccount(@RequestBody UserNameVO userNameVO) {
        BusinessAdminVO adminVO = businessAdminApi.getAdminByUserName(userNameVO.getUserName());
        if (null == adminVO || null == adminVO.getId()) {
            throw new BaowangDefaultException(ResultCode.ADMIN_NAME_NOT_EXIST);
        }

        Boolean isSetGoogle = true;
        if (StringUtils.isEmpty(adminVO.getGoogleAuthKey()) || adminVO.getIsSetGoogle() == 1) {
            isSetGoogle = false;
        }

        return ResponseVO.success(isSetGoogle);
    }

    @PostMapping("bindGoogle")
    @Operation(summary = "绑定google密钥, 直接登录")
    public ResponseVO<AdminLoginResultVO> bindGoogle(@Valid @RequestBody GoogleBindVO googleBindVO, HttpServletRequest request) {
        if (!StringUtils.isNumeric(googleBindVO.getVerifyCode()) || googleBindVO.getVerifyCode().length() != 6) {
            throw new BaowangDefaultException(ResultCode.GOOGLE_AUTH_NO_PASS);
        }
        BusinessAdminVO adminVO = businessAdminApi.getBusinessAdminById(googleBindVO.getId());
        if (null == adminVO || null == adminVO.getId()) {
            throw new BaowangDefaultException(ResultCode.ADMIN_NAME_NOT_EXIST);
        }

        checkLoginErrorTimeLimit(adminVO.getUserId());

        //校验谷歌验证码
        boolean googleAuth = checkGoogleCode(googleBindVO.getGoogleAuthKey(), googleBindVO.getVerifyCode());
        if (!googleAuth) {
            incrGoogleAuthTimeLimit(adminVO.getUserId());
            log.info("谷歌校验不通过，校验码{}", googleBindVO.getVerifyCode());
            throw new BaowangDefaultException(ResultCode.GOOGLE_AUTH_NO_PASS);
        }
        resetLoginErrorTimeLimit(adminVO.getUserId(),false);
        //直接登录
        AdminLoginVO adminLoginParamVO = new AdminLoginVO();
        String ip = CurrReqUtils.getReqIp();
        adminLoginParamVO.setLoginIp(ip);

        String ipAddress = getIpAddress(adminLoginParamVO.getLoginIp());
        adminLoginParamVO.setLoginLocation(ipAddress);

        LoginAdmin loginAdmin = new LoginAdmin();
        BeanUtils.copyProperties(adminVO, loginAdmin);
        loginAdmin.setIsSuperAdmin(adminVO.getIsSuperAdmin().equals(YesOrNoEnum.YES.getCode()));
        AdminLoginResultVO adminLoginResultVO = new AdminLoginResultVO();
        adminLoginResultVO.setUserName(loginAdmin.getUserName());
        adminLoginResultVO.setNickName(loginAdmin.getNickName());
        adminLoginResultVO.setUserId(loginAdmin.getUserId());
        adminLoginResultVO.setId(loginAdmin.getId());
        adminLoginResultVO.setDataDesensitization(loginAdmin.getDataDesensitization());
        adminLoginResultVO.setNeedGoogle(false);

        //判断之前是否有登录，有登录的话要清空之前的token信息
        String oldToken = RedisUtil.getValue(AdminAuthUtil.getJwtKey(adminVO.getUserId()));
        if (!ObjectUtils.isEmpty(oldToken)) {
            tokenService.delLoginUser(oldToken);
        }

        loginInfoService.recordLoginInfoRecord(buildInsertAddLogInfoParam(adminVO.getUserName(),
                CommonConstant.business_zero, ResultCode.SUCCESS.getMessageCode(), adminLoginParamVO.getLoginIp(),
                adminLoginParamVO.getLoginLocation(), adminVO.getUserId(), adminVO.getSiteCode()));

        loginAdmin.setId(adminVO.getId());
        loginAdmin.setUserName(adminVO.getUserName());
        Map<String, Object> map = tokenService.createToken(loginAdmin, loginAdmin.getIsSuperAdmin());
        String token = map.get("access_token").toString();
        adminLoginResultVO.setToken(token);

        //更新google密钥
        businessAdminApi.updateGoogleKey(googleBindVO);

        AdminUpdateVO updateVO = new AdminUpdateVO();
        updateVO.setId(loginAdmin.getId());
        updateVO.setLastLoginTime(System.currentTimeMillis());
        businessAdminApi.update(updateVO);

        return ResponseVO.success(adminLoginResultVO);
    }

    @PostMapping("login")
    @Operation(summary = "中控后台登录")
    public ResponseVO<AdminLoginResultVO> login(@Valid @RequestBody AdminLoginParamVO adminLoginVO, HttpServletRequest request) {

        AdminLoginVO adminLoginParamVO = new AdminLoginVO();
        BeanUtils.copyProperties(adminLoginVO, adminLoginParamVO);
        String ip = CurrReqUtils.getReqIp();
        adminLoginParamVO.setLoginIp(ip);
        adminLoginParamVO.setSiteCode(CommonConstant.ADMIN_CENTER_SITE_CODE);

        String ipAddress = getIpAddress(adminLoginParamVO.getLoginIp());
        adminLoginParamVO.setLoginLocation(ipAddress);
        AdminLoginResultVO adminLoginResultVO = new AdminLoginResultVO();
        LoginAdmin loginAdmin = adminLogin(adminLoginParamVO);
        String authKey = "";
        adminLoginResultVO.setUserName(loginAdmin.getUserName());
        adminLoginResultVO.setNickName(loginAdmin.getNickName());
        adminLoginResultVO.setId(loginAdmin.getId());
        adminLoginResultVO.setUserId(loginAdmin.getUserId());
        adminLoginResultVO.setDataDesensitization(loginAdmin.getDataDesensitization());
        adminLoginResultVO.setNeedGoogle(false);
        authKey = loginAdmin.getGoogleAuthKey();
        if (loginAdmin.getIsFirstLogin()) {
            adminLoginResultVO.setGoogleAuthKey(authKey);
            adminLoginResultVO.setNeedGoogle(true);
            return ResponseVO.success(adminLoginResultVO);
        }

        if (!StringUtils.isNumeric(adminLoginParamVO.getVerifyCode()) || adminLoginParamVO.getVerifyCode().length() != 6) {
            throw new BaowangDefaultException(ResultCode.GOOGLE_AUTH_NO_PASS);
        }

        //判断之前是否有登录，有登录的话要清空之前的token信息
        String oldToken = RedisUtil.getValue(AdminAuthUtil.getJwtKey(adminLoginResultVO.getUserId()));
        if (!ObjectUtils.isEmpty(oldToken)) {
            tokenService.delLoginUser(oldToken);
        }

        checkLoginErrorTimeLimit(loginAdmin.getUserId());
        //校验谷歌验证码
        boolean googleAuth = checkGoogleCode(authKey, adminLoginParamVO.getVerifyCode());
        if (!googleAuth) {
            incrGoogleAuthTimeLimit(loginAdmin.getUserId());
            log.info("Admin 谷歌校验不通过，校验码{}", adminLoginParamVO.getVerifyCode());

            loginInfoService.recordLoginInfoRecord(buildInsertAddLogInfoParam(adminLoginParamVO.getUserName(), CommonConstant.business_one, ResultCode.GOOGLE_AUTH_NO_PASS.getMessageCode(),
                    adminLoginParamVO.getLoginIp(), adminLoginParamVO.getLoginLocation(), loginAdmin.getUserId(), loginAdmin.getSiteCode()));

            throw new BaowangDefaultException(ResultCode.GOOGLE_AUTH_NO_PASS);
        }

        //reset单日google验证错误次数
        resetLoginErrorTimeLimit(loginAdmin.getUserId(),false);

        loginInfoService.recordLoginInfoRecord(buildInsertAddLogInfoParam(adminLoginParamVO.getUserName(),
                CommonConstant.business_zero, ResultCode.SUCCESS.getMessageCode(), adminLoginParamVO.getLoginIp(),
                adminLoginParamVO.getLoginLocation(), loginAdmin.getUserId(), loginAdmin.getSiteCode()));

        loginAdmin.setId(adminLoginResultVO.getId());
        loginAdmin.setUserName(adminLoginParamVO.getUserName());
        Map<String, Object> map = tokenService.createToken(loginAdmin, loginAdmin.getIsSuperAdmin());
        String token = map.get("access_token").toString();
        adminLoginResultVO.setToken(token);

        AdminUpdateVO updateVO = new AdminUpdateVO();
        updateVO.setId(loginAdmin.getId());
        updateVO.setLastLoginTime(System.currentTimeMillis());
        updateVO.setIsSetGoogle(0);
        businessAdminApi.update(updateVO);
        adminLoginResultVO.setRoleNames(CollectionUtil.join(loginAdmin.getRoleNames(), ","));
        adminLoginResultVO.setRoleIds(CollectionUtil.join(loginAdmin.getRoleIds(),","));
        return ResponseVO.success(adminLoginResultVO);

    }

    /**
     * 登录账户密码错误次数缓存键名
     *
     * @param username 用户名
     * @return 缓存键key
     */
    private String getCacheKey(String username) {
        return TokenConstants.PWD_ERR_CNT_KEY + username;
    }

    private BusinessLoginInfoAddVO buildInsertAddLogInfoParam(String userName, int i, String errMsg, String ip, String loginLocation, String userId, String siteCode) {
        BusinessLoginInfoAddVO businessLoginInfoAddVO = new BusinessLoginInfoAddVO();
        businessLoginInfoAddVO.setUserName(userName);
        businessLoginInfoAddVO.setStatus(i);
        businessLoginInfoAddVO.setMsg(errMsg);
        businessLoginInfoAddVO.setIpaddr(ip);
        businessLoginInfoAddVO.setLoginLocation(loginLocation);
        businessLoginInfoAddVO.setUserId(userId);
        businessLoginInfoAddVO.setSiteCode(siteCode);
        return businessLoginInfoAddVO;
    }

    /**
     * ip转化地址,去重
     *
     * @param ip
     * @return
     */
    private String getIpAddress(String ip) {
            IPRespVO ipApiVO= IpAPICoUtils.getIp(ip);
            return ipApiVO.getAddress();
    }

    public LoginAdmin adminLogin(AdminLoginVO adminLoginVO) {
        BusinessAdminVO adminVO = businessAdminApi.getAdminByUserName(adminLoginVO.getUserName());
        if (null == adminVO || null == adminVO.getId()) {
            throw new BaowangDefaultException(ResultCode.ADMIN_NAME_NOT_EXIST);
        }
        if (CommonConstant.business_zero.equals(adminVO.getStatus())) {
            loginInfoService.recordLoginInfoRecord(
                    buildInsertAddLogInfoParam(adminVO.getUserName(), CommonConstant.business_one,
                            ResultCode.ACCOUNT_DISABLED.getMessageCode(), adminLoginVO.getLoginIp(),
                            adminLoginVO.getLoginLocation(), adminVO.getUserId(), adminVO.getSiteCode()));
            throw new BaowangDefaultException(ResultCode.ACCOUNT_DISABLED);
        }
        if (CommonConstant.business_one.equals(adminVO.getLockStatus())) {
            loginInfoService.recordLoginInfoRecord(
                    buildInsertAddLogInfoParam(adminVO.getUserName(), CommonConstant.business_one,
                            ResultCode.ACCOUNT_LOCK.getMessageCode(), adminLoginVO.getLoginIp(),
                            adminLoginVO.getLoginLocation(), adminVO.getUserId(), adminVO.getSiteCode()));

            throw new BaowangDefaultException(ResultCode.ACCOUNT_LOCK);
        }

        passwordService.validate(adminVO, adminLoginVO.getPassword(), adminLoginVO.getLoginIp(), adminLoginVO.getLoginLocation());
        LoginAdmin loginAdmin = new LoginAdmin();

        BeanUtils.copyProperties(adminVO, loginAdmin);
        loginAdmin.setDataDesensitization(adminVO.getDataDesensitization());
        loginAdmin.setIsFirstLogin(false);
        if (adminVO.getIsSetGoogle() == 1) {
            loginAdmin.setIsFirstLogin(true);
        }
        if (StringUtils.isEmpty(adminVO.getGoogleAuthKey())) {
            //如果密钥为空，则手动生成密钥
            loginAdmin.setIsFirstLogin(true);
            loginAdmin.setGoogleAuthKey(GoogleAuthUtil.generateSecretKey());
        }


        loginAdmin.setIsSuperAdmin(adminVO.getIsSuperAdmin().equals(YesOrNoEnum.YES.getCode()));
        return loginAdmin;
    }


    public Boolean checkGoogleCode(String authKey, String verifyCode) {
        Integer googleAuthCode = Integer.parseInt(verifyCode);
        return GoogleAuthUtil.checkCode(authKey, googleAuthCode);
    }

    @PostMapping("updatePassword")
    @Schema(description = "修改密码")
    public ResponseVO updatePassword(@Valid @RequestBody AdminPasswordEditVO passwordEditVO) {
        BusinessAdminVO adminVO = businessAdminApi.getBusinessAdminById(passwordEditVO.getId());
        if (adminVO == null) {
            throw new BaowangDefaultException(ResultCode.ADMIN_NAME_NOT_EXIST);
        }

        if (!passwordEditVO.getNewPassword().equals(passwordEditVO.getConfirmPassword())) {
            throw new BaowangDefaultException(ResultCode.PASSWORDS_ENTERED_TWICE_ARE_INCONSISTENT);
        }

        if (SecurityUtils.matchesPassword(passwordEditVO.getNewPassword(), adminVO.getPassword())) {
            throw new BaowangDefaultException(ResultCode.PASSWORD_SAME);
        }

        String password = SecurityUtils.encryptPassword(passwordEditVO.getNewPassword());
        BusinessAdminResetPasswordVO passwordVO = new BusinessAdminResetPasswordVO();
        passwordVO.setId(adminVO.getId());
        passwordVO.setPassword(password);
        Integer count = businessAdminApi.resetPassword(passwordVO);
        return ResponseVO.success();
    }


    @PostMapping("adminLogout")
    @Operation(summary = "中控后台退出")
    public ResponseVO logout(HttpServletRequest request) {
        String token = SecurityUtils.getToken(request);
        if (StringUtils.isNotEmpty(token)) {
            // 删除用户缓存记录
            AuthUtil.logoutByToken(token);
        }
        return ResponseVO.success();
    }

    public void incrGoogleAuthTimeLimit(String userId) {
        String authKey = String.format(RedisConstants.KEY_GOOGLE_AUTH_VERIFY_LIMIT, CurrReqUtils.getSiteCode(), userId);

        RedisUtil.incr(authKey, CommonConstant.business_one);
        Integer authValue = RedisUtil.getValue(authKey);
        if (authValue != null && authValue >= CommonConstant.business_five) {
            resetLoginErrorTimeLimit(userId,true);
            throw new BaowangDefaultException(ResultCode.VERIFY_CODE_LIMIT_HOUR);
            //锁住
        }


    }

    public void resetGoogleAuthTimeLimit(String userId) {
        String authKey = String.format(RedisConstants.KEY_GOOGLE_AUTH_VERIFY_LIMIT, CurrReqUtils.getSiteCode(), userId);
        RedisUtil.deleteKey(authKey);
    }



    //查询是否锁定
    public void checkLoginErrorTimeLimit(String userAccount) {
        String lockKey = String.format(RedisConstants.KEY_GOOGLE_AUTH_FIVE_TIMES_LIMIT,CurrReqUtils.getSiteCode(), userAccount);
        if (RedisUtil.isKeyExist(lockKey)) {
            Long limitSeconds = RedisUtil.getRemainExpireTime(lockKey);
            SystemDictConfigRespVO configValue = systemDictConfigApi.getByCode(GOOGLE_VERIFY_MAX_TIMES.getCode(),CurrReqUtils.getSiteCode()).getData();
            if (limitSeconds > 0) {
                throw new BaowangDefaultException(configValue.getHintInfo());
            }
        }
    }
    public void resetLoginErrorTimeLimit(String userId,boolean lock) {
        String siteCode = CurrReqUtils.getSiteCode();
        String limitKey = String.format(RedisConstants.KEY_GOOGLE_AUTH_FIVE_TIMES_LIMIT, siteCode, userId);
        if (lock){
            //锁定
            SystemDictConfigRespVO configValue = systemDictConfigApi.getByCode(GOOGLE_VERIFY_MAX_TIMES.getCode(), siteCode).getData();
            long lockSeconds = Long.parseLong(configValue.getConfigParam())*60L;
            if (lockSeconds>0){
                RedisUtil.setValue(limitKey,limitKey,lockSeconds);
                String authKey = String.format(RedisConstants.KEY_GOOGLE_AUTH_VERIFY_LIMIT, CurrReqUtils.getSiteCode(), userId);
                RedisUtil.deleteKey(authKey);
                RedisUtil.setValue(authKey,authKey,lockSeconds);
            }
        }else {
            //解锁
            RedisUtil.deleteKey(limitKey);
        }
        resetGoogleAuthTimeLimit(userId);
    }

}

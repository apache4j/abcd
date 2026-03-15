package com.cloud.baowang.site.controller.login;

import cn.hutool.core.collection.CollectionUtil;
import com.cloud.baowang.common.auth.util.SiteAuthUtil;
import com.cloud.baowang.common.auth.util.UserAuthUtil;
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
import com.cloud.baowang.site.service.LoginInfoService;
import com.cloud.baowang.site.service.PasswordService;
import com.cloud.baowang.site.service.SiteTokenService;
import com.cloud.baowang.site.utils.auth.SiteSecurityUtils;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.operations.DomainInfoApi;
import com.cloud.baowang.system.api.api.site.SiteAdminApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.adminLogin.AdminLoginParamVO;
import com.cloud.baowang.system.api.vo.adminLogin.AdminLoginResultVO;
import com.cloud.baowang.system.api.vo.adminLogin.AdminLoginVO;
import com.cloud.baowang.system.api.vo.adminLogin.AdminUpdateVO;
import com.cloud.baowang.system.api.vo.adminLogin.GoogleKeyVO;
import com.cloud.baowang.system.api.vo.adminLogin.GoogleSubmitVO;
import com.cloud.baowang.system.api.vo.adminLogin.LoginAdmin;
import com.cloud.baowang.system.api.vo.adminLogin.PasswordEditVO;
import com.cloud.baowang.system.api.vo.adminLogin.UserNameVO;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.api.vo.member.BusinessLoginInfoAddVO;
import com.cloud.baowang.system.api.vo.operations.DomainVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteAdminVO;
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

import static com.cloud.baowang.common.core.constants.CommonConstant.X_CUSTOM;
import static com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums.GOOGLE_VERIFY_MAX_TIMES;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/site_admin_login/api")
@Tag(name = "站点后台登录")
public class    SiteAdminLoginController {

    private final SiteTokenService siteTokenService;

    private final SiteAdminApi siteAdminApi;

    private final PasswordService passwordService;

    private final DomainInfoApi domainInfoApi;

    private final LoginInfoService loginInfoService;

    private final SiteApi siteApi;

    private final SystemDictConfigApi systemDictConfigApi;


    @PostMapping("checkAccount")
    @Operation(summary = "检查用户是否已设置google密钥, true 已设置  false 未设置")
    public ResponseVO<Boolean> checkAccount(@RequestBody UserNameVO userNameVO) {
        SiteAdminVO adminVO = siteAdminApi.getAdminByUserNameAndSite(userNameVO.getUserName(), userNameVO.getSiteCode());

        if (null == adminVO || null == adminVO.getId()) {
            throw new BaowangDefaultException(ResultCode.ADMIN_NAME_NOT_EXIST);
        }

        Boolean isSetGoogle = true;
        if (StringUtils.isEmpty(adminVO.getGoogleAuthKey()) || adminVO.getIsSetGoogle() == 1) {
            isSetGoogle = false;
        }

        return ResponseVO.success(isSetGoogle);

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

    @PostMapping("login")
    @Operation(summary = "站点后台登录")
    public ResponseVO<AdminLoginResultVO> login(@Valid @RequestBody AdminLoginParamVO adminLoginVO, HttpServletRequest request) {
        AdminLoginVO adminLoginParamVO = new AdminLoginVO();
        BeanUtils.copyProperties(adminLoginVO, adminLoginParamVO);
        String ip = CurrReqUtils.getReqIp();
        adminLoginParamVO.setLoginIp(ip);
        String siteCode=adminLoginVO.getSiteCode();

        String domain = request.getHeader(X_CUSTOM);
        log.info("站点后台登录获取到的域名：{}, IP：{}", domain, ip);

        String ipAddress = getIpAddress(adminLoginParamVO.getLoginIp());
        adminLoginParamVO.setLoginLocation(ipAddress);
        adminLoginParamVO.setDomain(domain);
        AdminLoginResultVO adminLoginResultVO = new AdminLoginResultVO();
        LoginAdmin loginAdmin = adminLogin(adminLoginParamVO);
        String authKey = "";
        adminLoginResultVO.setUserName(loginAdmin.getUserName());
        adminLoginResultVO.setUserId(loginAdmin.getUserId());
        adminLoginResultVO.setNickName(loginAdmin.getNickName());
        adminLoginResultVO.setId(loginAdmin.getId());
        adminLoginResultVO.setNeedGoogle(false);
        adminLoginResultVO.setDataDesensitization(loginAdmin.getDataDesensitization());
        authKey = loginAdmin.getGoogleAuthKey();
        if (loginAdmin.getIsFirstLogin()) {
            adminLoginResultVO.setNeedGoogle(true);
            adminLoginResultVO.setGoogleAuthKey(authKey);
            return ResponseVO.success(adminLoginResultVO);
        }

        if (!StringUtils.isNumeric(adminLoginParamVO.getVerifyCode())) {
            throw new BaowangDefaultException(ResultCode.GOOGLE_AUTH_NO_PASS);
        }


        checkLoginErrorTimeLimit(loginAdmin.getUserId(),adminLoginVO.getSiteCode());

        //校验谷歌验证码
        boolean googleAuth = checkGoogleCode(authKey, adminLoginParamVO.getVerifyCode());
        if (!googleAuth) {
            incrGoogleAuthTimeLimit(loginAdmin.getUserId(),adminLoginVO.getSiteCode());
            log.info("站点登录,谷歌校验不通过，校验码{}", adminLoginParamVO.getVerifyCode());
            loginInfoService.recordLoginInfoRecord(buildInsertAddLogInfoParam(adminLoginParamVO.getUserName(), CommonConstant.business_one, ResultCode.GOOGLE_AUTH_NO_PASS.getMessageCode(),
                    adminLoginParamVO.getLoginIp(), adminLoginParamVO.getLoginLocation(), loginAdmin.getUserId(), loginAdmin.getSiteCode()));

            throw new BaowangDefaultException(ResultCode.GOOGLE_AUTH_NO_PASS);
        }


        
        resetLoginErrorTimeLimit(loginAdmin.getUserId(),false,adminLoginVO.getSiteCode());

        //校验登录ip白名单
        SiteAdminVO adminVO = siteAdminApi.getAdminByUserNameAndSite(adminLoginVO.getUserName(), adminLoginVO.getSiteCode());
        if (!StringUtils.isBlank(adminVO.getAllowIps())) {
            String[] whiteLists = adminVO.getAllowIps().split(",");
            boolean isAllow = false;
            for (String whiteIp : whiteLists) {
                if (whiteIp.trim().equals(ip)) {
                    isAllow = true;
                    break;
                }
            }
            if (!isAllow) {
                log.info("当前登录ip不在白名单中,当前登录人账号:{},登录ip:{},加白IP:{}",loginAdmin.getUserName(),ip,whiteLists);
                return ResponseVO.fail(ResultCode.IP_NOT_ALLOW);
            }
        }

        //判断之前是否有登录，有登录的话要清空之前的token信息
        String jwtKey=SiteAuthUtil.getJwtKey(siteCode,loginAdmin.getUserId());
        String oldToken = RedisUtil.getValue(jwtKey);
        if (!ObjectUtils.isEmpty(oldToken)) {
            siteTokenService.delLoginUser(siteCode,oldToken);
        }

        loginInfoService.recordLoginInfoRecord(buildInsertAddLogInfoParam(adminLoginParamVO.getUserName(),
                CommonConstant.business_zero, ResultCode.SUCCESS.getMessageCode(), adminLoginParamVO.getLoginIp(),
                adminLoginParamVO.getLoginLocation(), loginAdmin.getUserId(), loginAdmin.getSiteCode()));


        loginAdmin.setId(adminLoginResultVO.getId());
        loginAdmin.setUserName(adminLoginParamVO.getUserName());
        String token = siteTokenService.createToken(loginAdmin, loginAdmin.getIsSuperAdmin());
        adminLoginResultVO.setToken(token);

        //ResponseVO<SiteVO> responseVO = siteApi.getSiteInfo(adminLoginVO.getSiteCode());
       // SiteVO siteVo = responseVO.getData();
        //adminLoginResultVO.setSiteModel(siteVo.getSiteModel());

        AdminUpdateVO updateVO = new AdminUpdateVO();
        updateVO.setSiteCode(adminLoginVO.getSiteCode());
        updateVO.setUserName(adminLoginVO.getUserName());
        updateVO.setLastLoginTime(System.currentTimeMillis());
        siteAdminApi.update(updateVO);

        adminLoginResultVO.setRoleNames(CollectionUtil.join(loginAdmin.getRoleNames(), ","));
        adminLoginResultVO.setRoleIds(CollectionUtil.join(loginAdmin.getRoleIds(),","));

        return ResponseVO.success(adminLoginResultVO);
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
        String siteCode = adminLoginVO.getSiteCode();
        if (adminLoginVO.getSiteCode() == null) {
            DomainVO domainVO = domainInfoApi.getDomainByAddress(adminLoginVO.getDomain());
            if (domainVO == null || domainVO.getSiteCode() == null) {
                throw new BaowangDefaultException(ResultCode.ADMIN_NAME_NOT_EXIST);
            }
            siteCode = domainVO.getSiteCode();
        }


        /*if (!siteCode.equals(adminLoginVO.getSiteCode())) {
            throw new BaowangDefaultException(ResultCode.ADMIN_NAME_NOT_EXIST);
        }*/

        SiteAdminVO adminVO = siteAdminApi.getAdminByUserNameAndSite(adminLoginVO.getUserName(), siteCode);
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
    @Schema(description = "首次修改密码")
    public ResponseVO updatePassword(@Valid @RequestBody PasswordEditVO passwordEditVO) {
        SiteAdminVO siteAdminVO = siteAdminApi.getAdminByUserNameAndSite(passwordEditVO.getUserName(), passwordEditVO.getSiteCode());
        if (siteAdminVO == null || siteAdminVO.getSiteCode() == null) {
            throw new BaowangDefaultException(ResultCode.ADMIN_NAME_NOT_EXIST);
        }

        if (!passwordEditVO.getNewPassword().equals(passwordEditVO.getConfirmPassword())) {
            throw new BaowangDefaultException(ResultCode.PASSWORDS_ENTERED_TWICE_ARE_INCONSISTENT);
        }

        if (SiteSecurityUtils.matchesPassword(passwordEditVO.getNewPassword(), siteAdminVO.getPassword())) {
            throw new BaowangDefaultException(ResultCode.PASSWORD_SAME);
        }
        String password = SiteSecurityUtils.encryptPassword(passwordEditVO.getNewPassword());
        passwordEditVO.setPassword(password);
        return siteAdminApi.updatePassword(passwordEditVO);
    }

    /* @PostMapping("getGoogleCode")
     @Schema(description = "生成Google密钥")
     public ResponseVO<String> getGoogleCode() {
         return ResponseVO.success(GoogleAuthUtil.generateSecretKey());
     }
 */
    @PostMapping("submitGoogleCode")
    @Schema(description = "提交google验证码")
    public ResponseVO<AdminLoginResultVO> submitGoogleCode(@Valid @RequestBody GoogleSubmitVO adminLoginVO, HttpServletRequest request) {
        if (!StringUtils.isNumeric(adminLoginVO.getVerifyCode()) || adminLoginVO.getVerifyCode().length() != 6) {
            throw new BaowangDefaultException(ResultCode.GOOGLE_AUTH_NO_PASS);
        }
        AdminLoginVO adminLoginParamVO = new AdminLoginVO();
        BeanUtils.copyProperties(adminLoginVO, adminLoginParamVO);
        String ip = CurrReqUtils.getReqIp();
        String siteCode=CurrReqUtils.getSiteCode();
        adminLoginParamVO.setLoginIp(ip);
        if (!StringUtils.isNumeric(adminLoginParamVO.getVerifyCode())) {
            throw new BaowangDefaultException(ResultCode.GOOGLE_AUTH_NO_PASS);
        }

        String domain = request.getHeader(X_CUSTOM);
        log.info("站点后台登录获取到的域名：{},ip:{}", domain,ip);

        String ipAddress = getIpAddress(adminLoginParamVO.getLoginIp());
        adminLoginParamVO.setLoginLocation(ipAddress);
        adminLoginParamVO.setDomain(domain);
        AdminLoginResultVO adminLoginResultVO = new AdminLoginResultVO();
        LoginAdmin loginAdmin = adminLogin(adminLoginParamVO);
        String authKey = adminLoginVO.getGoogleAuthKey();
        adminLoginResultVO.setUserName(loginAdmin.getUserName());
        adminLoginResultVO.setUserId(loginAdmin.getUserId());
        adminLoginResultVO.setNickName(loginAdmin.getNickName());
        adminLoginResultVO.setDataDesensitization(loginAdmin.getDataDesensitization());
        adminLoginResultVO.setId(loginAdmin.getId());

        //判断之前是否有登录，有登录的话要清空之前的token信息
        String oldToken = RedisUtil.getValue(UserAuthUtil.getJwtKey(siteCode,loginAdmin.getUserId()));
        if (!ObjectUtils.isEmpty(oldToken)) {
            siteTokenService.delLoginUser(siteCode,oldToken);
        }
        checkLoginErrorTimeLimit(loginAdmin.getUserId(),adminLoginVO.getSiteCode());
        //校验谷歌验证码
        boolean googleAuth = checkGoogleCode(authKey, adminLoginParamVO.getVerifyCode());
        if (!googleAuth) {
            incrGoogleAuthTimeLimit(loginAdmin.getUserId(),adminLoginVO.getSiteCode());
            log.info("谷歌校验不通过，校验码{}", adminLoginParamVO.getVerifyCode());
            throw new BaowangDefaultException(ResultCode.GOOGLE_AUTH_NO_PASS);
        }
        //reset单日google验证错误次数
        resetLoginErrorTimeLimit(loginAdmin.getUserId(),false,adminLoginVO.getSiteCode());

        //校验登录ip白名单
        SiteAdminVO adminVO = siteAdminApi.getAdminByUserNameAndSite(adminLoginVO.getUserName(), adminLoginVO.getSiteCode());

        if (StringUtils.isNotBlank(adminVO.getAllowIps())) {
            String[] whiteLists = adminVO.getAllowIps().split(",");
            boolean isAllow = false;
            for (String whiteIp : whiteLists) {
                if (whiteIp.trim().equals(ip)) {
                    isAllow = true;
                    break;
                }
            }
            if (!isAllow) {
                log.info("当前登录ip不在白名单中,当前登录人账号:{},登录ip:{},加白IP:{}",loginAdmin.getUserName(),ip,whiteLists);
                return ResponseVO.fail(ResultCode.IP_NOT_ALLOW);
            }
        }

        loginInfoService.recordLoginInfoRecord(buildInsertAddLogInfoParam(loginAdmin.getUserName(),
                CommonConstant.business_zero, ResultCode.SUCCESS.getMessageCode(), adminLoginParamVO.getLoginIp(),
                adminLoginParamVO.getLoginLocation(), loginAdmin.getUserId(), loginAdmin.getSiteCode()));

        loginAdmin.setId(adminLoginResultVO.getId());
        loginAdmin.setUserName(adminLoginParamVO.getUserName());
        String token = siteTokenService.createToken(loginAdmin, loginAdmin.getIsSuperAdmin());
        adminLoginResultVO.setToken(token);

       // ResponseVO<SiteVO> responseVO = siteApi.getSiteInfo(adminLoginVO.getSiteCode());
       // SiteVO siteVo = responseVO.getData();
        //adminLoginResultVO.setSiteModel(siteVo.getSiteModel());

        //更新google密钥
        GoogleKeyVO googleKeyVO = new GoogleKeyVO();
        googleKeyVO.setUserName(adminLoginParamVO.getUserName());
        googleKeyVO.setSiteCode(adminLoginParamVO.getSiteCode());
        googleKeyVO.setGoogleAuthKey(adminLoginVO.getGoogleAuthKey());
        siteAdminApi.updateGoogleKey(googleKeyVO);

        AdminUpdateVO updateVO = new AdminUpdateVO();
        updateVO.setSiteCode(adminLoginVO.getSiteCode());
        updateVO.setUserName(adminLoginVO.getUserName());
        updateVO.setLastLoginTime(System.currentTimeMillis());
        updateVO.setIsSetGoogle(0);
        siteAdminApi.update(updateVO);

        return ResponseVO.success(adminLoginResultVO);
    }

    @PostMapping("adminLogout")
    @Operation(summary = "站点后台退出")
    public ResponseVO logout(HttpServletRequest request) {
        String siteCode=CurrReqUtils.getSiteCode();
        String userId=CurrReqUtils.getSiteCode();
        String token = RedisUtil.getValue(UserAuthUtil.getJwtKey(siteCode,userId));
        if (StringUtils.isNotEmpty(token)) {
            // 删除用户缓存记录
            siteTokenService.delLoginUser(siteCode,token);
        }
        return ResponseVO.success();
    }

    private BusinessLoginInfoAddVO buildInsertAddLogInfoParam(String userName, Integer i, String errMsg, String ip,
                                                              String loginLocation, String userId, String siteCode) {
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

    public void incrGoogleAuthTimeLimit(String userId,String siteCode) {
        String authKey = String.format(RedisConstants.KEY_GOOGLE_AUTH_VERIFY_LIMIT, siteCode, userId);

        RedisUtil.incr(authKey, CommonConstant.business_one);
        Integer authValue = RedisUtil.getValue(authKey);
        if (authValue != null && authValue >= CommonConstant.business_five) {
            //锁住
            resetLoginErrorTimeLimit(userId,true,siteCode);
        }


    }

    public void resetGoogleAuthTimeLimit(String userId,String siteCode) {
        String authKey = String.format(RedisConstants.KEY_GOOGLE_AUTH_VERIFY_LIMIT, siteCode, userId);
        RedisUtil.deleteKey(authKey);
    }



    //查询是否锁定
    public void checkLoginErrorTimeLimit(String userAccount,String siteCode) {
        String lockKey = String.format(RedisConstants.KEY_GOOGLE_AUTH_FIVE_TIMES_LIMIT,siteCode, userAccount);
        if (RedisUtil.isKeyExist(lockKey)) {
            Long limitSeconds = RedisUtil.getRemainExpireTime(lockKey);
            SystemDictConfigRespVO configValue = systemDictConfigApi.getByCode(GOOGLE_VERIFY_MAX_TIMES.getCode(),siteCode).getData();
            if (limitSeconds > 0) {
                throw new BaowangDefaultException(configValue.getHintInfo());
            }
        }
    }
    public void resetLoginErrorTimeLimit(String userId,boolean lock,String siteCode) {

        String limitKey = String.format(RedisConstants.KEY_GOOGLE_AUTH_FIVE_TIMES_LIMIT, siteCode, userId);
        if (lock){
            //锁定
            SystemDictConfigRespVO configValue = systemDictConfigApi.getByCode(GOOGLE_VERIFY_MAX_TIMES.getCode(), siteCode).getData();
            long lockSeconds = Long.parseLong(configValue.getConfigParam())*60L;
            RedisUtil.setValue(limitKey,limitKey,lockSeconds);


            String authKey = String.format(RedisConstants.KEY_GOOGLE_AUTH_VERIFY_LIMIT, siteCode, userId);
            RedisUtil.deleteKey(authKey);
            RedisUtil.setValue(authKey,authKey,lockSeconds);
        }else {
            //解锁
            RedisUtil.deleteKey(limitKey);
        }
        resetGoogleAuthTimeLimit(userId,siteCode);
    }

}

package com.cloud.baowang.admin.service;

import com.cloud.baowang.admin.utils.auth.SecurityUtils;
import com.cloud.baowang.common.auth.util.AdminAuthUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.SecurityConstants;
import com.cloud.baowang.common.core.utils.JwtUtils;
import com.cloud.baowang.common.core.utils.ServletUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.adminLogin.LoginAdmin;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * token验证处理
 *
 * @author qiqi
 */
@Component
public class TokenService {

    protected static final long MILLIS_SECOND = 1000;
    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;
    private final static Long MILLIS_MINUTE_TEN = 10 * MILLIS_MINUTE;
    private final static long expireTime = 60L;
    private final SystemDictConfigApi systemDictConfigApi;

    public TokenService(final SystemDictConfigApi systemDictConfigApi) {
        this.systemDictConfigApi = systemDictConfigApi;
    }

    /**
     * 创建令牌
     */
    public Map<String, Object> createToken(LoginAdmin loginAdmin, Boolean isSuperAdmin) {
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        loginAdmin.setToken(token);
        String id = loginAdmin.getId();
        String userName = loginAdmin.getUserName();
        loginAdmin.setId(loginAdmin.getId());
        loginAdmin.setUserName(loginAdmin.getUserName());

        // Jwt存储信息
        Map<String, Object> claimsMap = new HashMap<String, Object>(3);
        claimsMap.put(SecurityConstants.ADMIN_KEY, token);
        claimsMap.put(SecurityConstants.DETAILS_ADMIN_ID, id);
        claimsMap.put(SecurityConstants.DETAILS_USERID, loginAdmin.getUserId());
        claimsMap.put(SecurityConstants.DETAILS_USERNAME, userName);
        claimsMap.put(SecurityConstants.SUPER_ADMIN, isSuperAdmin);
        claimsMap.put(SecurityConstants.DATA_DESENSITY, loginAdmin.getDataDesensitization() == null || loginAdmin.getDataDesensitization() ? "1" : "0");
        claimsMap.put(SecurityConstants.ADMIN_ROLE_LIST, loginAdmin.getRoleIds());

        // 接口返回信息
        Map<String, Object> rspMap = new HashMap<String, Object>(2);
        String accessToken = JwtUtils.createToken(claimsMap);

        Long logoutTime = expireTime;
        rspMap.put("access_token", accessToken);
        rspMap.put("expires_in", expireTime);
        //从参数字典获取超时时间
        ResponseVO<SystemDictConfigRespVO> responseVO =  systemDictConfigApi.getByCode(DictCodeConfigEnums.BACKEND_LOGOUT_TIMEOUT.getCode(), CommonConstant.ADMIN_CENTER_SITE_CODE);
        if (responseVO != null && responseVO.getData() != null && responseVO.isOk()) {
            SystemDictConfigRespVO systemDictConfigRespVO = responseVO.getData();
            String param = systemDictConfigRespVO.getConfigParam();
            logoutTime = Long.valueOf(param);
        }
        loginAdmin.setExpireTime(System.currentTimeMillis() + logoutTime * MILLIS_MINUTE);
        RedisUtil.setValue(AdminAuthUtil.getJwtKey(loginAdmin.getUserId()) , accessToken, logoutTime, TimeUnit.MINUTES);
        RedisUtil.setValue(AdminAuthUtil.getTokenKey(loginAdmin.getToken()), loginAdmin, logoutTime, TimeUnit.MINUTES);

        return rspMap;
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginAdmin getLoginUser() {
        return getLoginUser(ServletUtil.getRequest());
    }

    /**
     * 设置用户身份信息
     */
    public void setLoginUser(LoginAdmin loginAdmin) {
        if (null != loginAdmin && StringUtils.isNotEmpty(loginAdmin.getToken())) {
            refreshToken(loginAdmin);
        }
    }


    /**
     * 获取用户token
     * @param userId 用户ID
     * @return token
     */
    public String getTokenByUserId(String userId) {
        return RedisUtil.getValue(AdminAuthUtil.getJwtKey(userId));
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginAdmin getLoginUser(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = SecurityUtils.getToken(request);
        return getLoginUser(token);
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginAdmin getLoginUser(String token) {
        LoginAdmin user = null;
        try {
            if (StringUtils.isNotEmpty(token)) {
                String userKey = JwtUtils.getUserKey(token);
                user = RedisUtil.getValue(AdminAuthUtil.getTokenKey(userKey));
                return user;
            }
        } catch (Exception e) {
        }
        return user;
    }

    /**
     * 删除用户缓存信息
     */
    public void delLoginUser(String token) {
        if (StringUtils.isNotEmpty(token)) {
            String userKey = JwtUtils.getUserKey(token);
            String userId = JwtUtils.getUserId(token);
            RedisUtil.deleteKey(AdminAuthUtil.getTokenKey(userKey));
            RedisUtil.deleteKey(AdminAuthUtil.getJwtKey(userId));
        }
    }

    /**
     * 验证令牌有效期，相差不足120分钟，自动刷新缓存
     *
     * @param loginAdmin
     */
    public void verifyToken(LoginAdmin loginAdmin) {
        refreshToken(loginAdmin);
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginAdmin 登录信息
     */
    public void refreshToken(LoginAdmin loginAdmin) {
        loginAdmin.setLoginTime(System.currentTimeMillis());
        //从参数字典获取超时时间
        Long logoutTime = expireTime;
        ResponseVO<SystemDictConfigRespVO> responseVO =  systemDictConfigApi.getByCode(DictCodeConfigEnums.BACKEND_LOGOUT_TIMEOUT.getCode(), CommonConstant.ADMIN_CENTER_SITE_CODE);
        if (responseVO != null && responseVO.getData() != null && responseVO.isOk()) {
            SystemDictConfigRespVO systemDictConfigRespVO = responseVO.getData();
            String param = systemDictConfigRespVO.getConfigParam();
            logoutTime = Long.valueOf(param);
        }
        loginAdmin.setExpireTime(loginAdmin.getLoginTime() + logoutTime * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = AdminAuthUtil.getTokenKey(loginAdmin.getToken());
        RedisUtil.setValue(userKey, loginAdmin, logoutTime, TimeUnit.MINUTES);
        RedisUtil.setValue(AdminAuthUtil.getJwtKey(loginAdmin.getUserId()), loginAdmin.getAccessToken(), logoutTime, TimeUnit.MINUTES);
    }


}

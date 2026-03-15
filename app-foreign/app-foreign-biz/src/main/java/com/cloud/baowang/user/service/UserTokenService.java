package com.cloud.baowang.user.service;

import com.cloud.baowang.common.auth.util.UserAuthUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.TokenConstants;
import com.cloud.baowang.common.core.utils.JwtUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.adminLogin.LoginAdmin;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.user.api.vo.user.UserTokenVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * token验证处理
 */
@Component
public class UserTokenService {

    private final SystemDictConfigApi systemDictConfigApi;

    public UserTokenService(final SystemDictConfigApi systemDictConfigApi) {
        this.systemDictConfigApi = systemDictConfigApi;
    }

    /**
     * 创建令牌
     */
    public String createToken(UserTokenVO loginUser) {
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        loginUser.setToken(token);
        String tokenVal = buildToken(loginUser);
        cacheToken(loginUser, tokenVal);
        return tokenVal;
    }

    private String buildToken(UserTokenVO loginUser) {
        return generateToken(loginUser, null);
    }

    public String generateToken(UserTokenVO loginUser,
                                Map<String, String> payloads) {
        Map<String, Object> claims = buildClaims(loginUser, payloads);

        return JwtUtil.createToken(claims);
    }

    private Map<String, Object> buildClaims(UserTokenVO loginUser, Map<String, String> payloads) {
        int payloadSizes = payloads == null ? 0 : payloads.size();

        Map<String, Object> claims = new HashMap<>();
        claims.put(TokenConstants.USER_KEY, loginUser.getToken());
        claims.put(TokenConstants.DETAILS_USER_ACCOUNT, loginUser.getUserAccount());
        claims.put(TokenConstants.SITE_CODE, loginUser.getSiteCode());
        claims.put(TokenConstants.ID, loginUser.getId().toString());
        claims.put(TokenConstants.DETAILS_USER_ID, loginUser.getUserId());
        claims.put(TokenConstants.DETAILS_ACCOUNT_TYPE, loginUser.getAccountType());
        if (payloadSizes > 0) {
            claims.putAll(payloads);
        }

        return claims;
    }

    private void cacheToken(UserTokenVO loginUser, String tokenVal) {
        loginUser.setLoginTime(new Date());

        //从参数字典获取超时时间
        Long expireTime = TokenConstants.TOKEN_EXPIRE_TIME;
        ResponseVO<SystemDictConfigRespVO> responseVO =  systemDictConfigApi.getByCode(DictCodeConfigEnums.AUTO_LOGOUT_INACTIVITY_TIME.getCode(), loginUser.getSiteCode());
        if (responseVO != null && responseVO.getData() != null && responseVO.isOk()) {
            SystemDictConfigRespVO systemDictConfigRespVO = responseVO.getData();
            String param = systemDictConfigRespVO.getConfigParam();
            expireTime = Long.valueOf(param);
        }
        loginUser.setExpireTime(loginUser.getLoginTime().getTime() + expireTime*60*1000);
        RedisUtil.setValue(UserAuthUtil.getJwtKey(loginUser.getSiteCode(),loginUser.getUserId()), tokenVal, expireTime, TimeUnit.MINUTES);
        RedisUtil.setValue(getTokenKey(loginUser.getSiteCode(),loginUser.getToken()), loginUser, expireTime, TimeUnit.MINUTES);
 }


    public String getTokenByUserId(String siteCode,String userId) {
        return RedisUtil.getValue(UserAuthUtil.getJwtKey(siteCode,userId));
    }

    /**
     * 删除用户缓存信息
     */
    public boolean delLoginUser(String token) {
        if (!ObjectUtils.isEmpty(token)) {
            String userKey = JwtUtil.getUserKey(token);
            String userId = JwtUtil.getUserId(token);
            String siteCode = JwtUtil.getSiteCode(token);
            RedisUtil.deleteKey(getTokenKey(siteCode,userKey));
            RedisUtil.deleteKey(UserAuthUtil.getJwtKey(siteCode,userId));
        }

        return true;/**/
    }

    public UserTokenVO getLoginUser(String token) {
        UserTokenVO user = null;
        try {
            if (StringUtils.isNotEmpty(token)) {
                String siteCode = JwtUtil.getSiteCode(token);
                String userKey = JwtUtil.getUserKey(token);
                user = RedisUtil.getValue(getTokenKey(siteCode,userKey));
                return user;
            }
        } catch (Exception e) {
        }
        return user;
    }

    private String getTokenKey(String siteCode,String tokenUUId) {
        return UserAuthUtil.getTokenKey(siteCode,tokenUUId);
    }

    /**
     * 刷新令牌有效期
     */
    public void refreshToken(UserTokenVO userTokenVO) {
        //从参数字典获取超时时间
        Long logoutTime = TokenConstants.TOKEN_EXPIRE_TIME;
        ResponseVO<SystemDictConfigRespVO> responseVO =  systemDictConfigApi.getByCode(DictCodeConfigEnums.AUTO_LOGOUT_INACTIVITY_TIME.getCode(), userTokenVO.getSiteCode());
        if (responseVO != null && responseVO.getData() != null && responseVO.isOk()) {
            SystemDictConfigRespVO systemDictConfigRespVO = responseVO.getData();
            String param = systemDictConfigRespVO.getConfigParam();
            logoutTime = Long.valueOf(param);
        }

        // 根据uuid将loginUser缓存
        String token = getTokenByUserId(userTokenVO.getSiteCode(),userTokenVO.getUserId());
        userTokenVO.setLoginTime(new Date());
        userTokenVO.setExpireTime(userTokenVO.getLoginTime().getTime() + logoutTime);
        RedisUtil.setValue(UserAuthUtil.getJwtKey(userTokenVO.getSiteCode(),userTokenVO.getUserId()), token, logoutTime, TimeUnit.MINUTES);
        RedisUtil.setValue(getTokenKey(userTokenVO.getSiteCode(),userTokenVO.getToken()), userTokenVO, logoutTime, TimeUnit.MINUTES);
    }


}
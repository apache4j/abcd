package com.cloud.baowang.site.service;

import com.cloud.baowang.agent.api.vo.agentreview.info.AgentMerchantResultVO;
import com.cloud.baowang.common.auth.util.BusinessAuthUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.SecurityConstants;
import com.cloud.baowang.common.core.constants.TokenConstants;
import com.cloud.baowang.common.core.utils.JwtUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.adminLogin.LoginAdmin;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
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
public class BussinessTokenService {

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;
    private final static long expireTime = 60L;

    private final SystemDictConfigApi systemDictConfigApi;

    public BussinessTokenService(final SystemDictConfigApi systemDictConfigApi) {
        this.systemDictConfigApi = systemDictConfigApi;
    }

    /**
     * 创建令牌
     */
    public String  createToken(AgentMerchantResultVO resultVO) {
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        resultVO.setToken(token);
        String userName = resultVO.getMerchantName();
        resultVO.setMerchantName(userName);

        // Jwt存储信息
        Map<String, Object> claimsMap = new HashMap<String, Object>(3);
        claimsMap.put(TokenConstants.USER_KEY,token);
        claimsMap.put(SecurityConstants.ADMIN_KEY, token);
        claimsMap.put(SecurityConstants.SITE_CODE, resultVO.getSiteCode());
        claimsMap.put(SecurityConstants.USER_ACCOUNT,resultVO.getMerchantAccount());
        claimsMap.put(TokenConstants.DETAILS_USER_ID,resultVO.getMerchantId());
        claimsMap.put(SecurityConstants.ID,resultVO.getId());
        claimsMap.put(SecurityConstants.LANGUAGE, resultVO.getLanguage());


        // 接口返回信息
        Map<String, Object> rspMap = new HashMap<String, Object>(2);
        String accessToken = JwtUtils.createToken(claimsMap);

        //从参数字典获取超时时间
        Long logoutTime = expireTime;
        ResponseVO<SystemDictConfigRespVO> responseVO =  systemDictConfigApi.getByCode(DictCodeConfigEnums.BACKEND_LOGOUT_TIMEOUT.getCode(), CommonConstant.ADMIN_CENTER_SITE_CODE);
        if (responseVO != null && responseVO.getData() != null && responseVO.isOk()) {
            SystemDictConfigRespVO systemDictConfigRespVO = responseVO.getData();
            String param = systemDictConfigRespVO.getConfigParam();
            logoutTime = Long.valueOf(param);
        }

       // rspMap.put("access_token", accessToken);
        //rspMap.put("expires_in", logoutTime);
        resultVO.setExpireTime(System.currentTimeMillis() + logoutTime * MILLIS_MINUTE);
        RedisUtil.setValue(BusinessAuthUtil.getJwtKey(resultVO.getSiteCode(),resultVO.getMerchantId()) , accessToken, logoutTime, TimeUnit.MINUTES);
        RedisUtil.setValue(getTokenKey(resultVO.getSiteCode(),resultVO.getToken()), resultVO, logoutTime, TimeUnit.MINUTES);
        return accessToken;
    }


    /**
     * 删除用户缓存信息
     */
    public void delLoginUser(String siteCode,String token) {
        if (StringUtils.isNotEmpty(token)) {
            String userKey = JwtUtils.getUserKey(token);
            String userAccount = JwtUtils.getUserAccountKey(token);
            RedisUtil.deleteKey(getTokenKey(siteCode,userKey));
            RedisUtil.deleteKey(BusinessAuthUtil.getJwtKey(siteCode,userAccount));
        }
    }


    public String getTokenKey(String siteCode,String token) {
        return BusinessAuthUtil.getTokenKey(siteCode,token);
    }
}

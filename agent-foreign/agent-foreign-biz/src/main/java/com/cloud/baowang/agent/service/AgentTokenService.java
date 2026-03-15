package com.cloud.baowang.agent.service;

import com.cloud.baowang.agent.api.vo.agentLogin.AgentAccountVO;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentTokenVO;
import com.cloud.baowang.common.auth.util.AgentAuthUtil;
import com.cloud.baowang.common.core.constants.TokenConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.JwtUtil;
import com.cloud.baowang.common.core.utils.ServletUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * token验证处理
 */
public class AgentTokenService {
    /**
     * 创建令牌
     */
    public static String createToken(AgentTokenVO loginUser) {
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        loginUser.setToken(token);
        String tokenVal = buildToken(loginUser);
        cacheToken(loginUser, tokenVal);
        return tokenVal;
    }

    private static String buildToken(AgentTokenVO loginUser) {
        return  generateToken(loginUser, null);
    }

    public static String generateToken(AgentTokenVO loginUser,
                                Map<String, String> payloads) {
        Map<String, Object> claims = buildClaims(loginUser, payloads);

        return JwtUtil.createToken(claims);
    }

    private static Map<String, Object> buildClaims(AgentTokenVO loginUser, Map<String, String> payloads) {
        int payloadSizes = payloads == null ? 0 : payloads.size();
        Map<String, Object> claims = new HashMap<>();
        claims.put(TokenConstants.USER_KEY, loginUser.getToken());
        claims.put(TokenConstants.DETAILS_USER_ACCOUNT, loginUser.getAgentAccount());
        claims.put(TokenConstants.ID, loginUser.getId());
        claims.put(TokenConstants.DETAILS_AGENT_ID, loginUser.getAgentId());
        claims.put(TokenConstants.SITE_CODE, loginUser.getSiteCode());
        claims.put(TokenConstants.DETAILS_ACCOUNT_TYPE, loginUser.getAgentType().toString());
        if (payloadSizes > 0) {
            claims.putAll(payloads);
        }

        return claims;
    }

    private static void cacheToken(AgentTokenVO loginUser, String tokenVal) {
        loginUser.setLoginTime(new Date());
        loginUser.setExpireTime(loginUser.getLoginTime().getTime() + TokenConstants.TOKEN_EXPIRE_TIME);
        RedisUtil.setValue(getJwtKey(loginUser.getSiteCode(), loginUser.getAgentId()), tokenVal, TokenConstants.TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);
        RedisUtil.setValue(getTokenKey(loginUser.getSiteCode(),loginUser.getToken()), loginUser, TokenConstants.TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);
    }


    public static String getTokenByAgentId(String siteCode,String agentId) {
        return RedisUtil.getValue(getJwtKey(siteCode,agentId));
    }

    /**
     * 删除用户缓存信息
     */
    public static boolean delLoginUser(String token) {
        if (!ObjectUtils.isEmpty(token)) {
            String userKey = JwtUtil.getUserKey(token);
            String agentId = JwtUtil.getAgentId(token);
            String siteCode = JwtUtil.getSiteCode(token);
            RedisUtil.deleteKey(getTokenKey(siteCode,userKey));
            RedisUtil.deleteKey(getJwtKey(siteCode,agentId));
        }
        return true;
    }

    private static String getTokenKey(String siteCode,String tokenVal) {
        return AgentAuthUtil.getTokenKey(siteCode,tokenVal);
    }

    private static String getJwtKey(String siteCode,String agentId) {
        return AgentAuthUtil.getJwtKey(siteCode,agentId);
    }

    public static String getCurrentAgentAccount() {
        HttpServletRequest request = ServletUtil.getRequest();
        if (request == null) return null;
        String token = ServletUtil.getRequest().getHeader(TokenConstants.SIGN);
        if (ObjectUtils.isEmpty(token)) return null;
        return  JwtUtil.getUserAccount(token);
    }

    public static AgentAccountVO getCurrentAgent() {
        HttpServletRequest request = ServletUtil.getRequest();
        if (request == null) return null;
        String token = ServletUtil.getRequest().getHeader(TokenConstants.SIGN);
        if (ObjectUtils.isEmpty(token)) return null;
        String agentId = JwtUtil.getAgentId(token);
        String agentAccount = JwtUtil.getUserAccount(token);
        String siteCode = JwtUtil.getSiteCode(token);
        String oldToken = RedisUtil.getValue(getJwtKey(siteCode,agentId));
        if (oldToken == null) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }
        String id = JwtUtil.getId(token);
        String accountType = JwtUtil.getValue(token, TokenConstants.DETAILS_ACCOUNT_TYPE);
        return AgentAccountVO.builder().agentAccount(agentAccount).agentId(agentId).id(id).agentType(Integer.valueOf(accountType)).siteCode(siteCode).build();
    }
}
package com.cloud.baowang.common.auth.util;

import com.cloud.baowang.common.auth.vo.AgentTokenVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.DomainInfoTypeEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.JwtUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

/**
 * @author: fangfei
 * @createTime: 2024/08/10 10:19
 * @description: 代理客户端token工具校验类
 */
@Slf4j
public class AgentAuthUtil {

    public static String getTokenKey(String siteCode,String tokenVal){
        return CommonUtil.getTokenKey(siteCode, DomainInfoTypeEnum.AGENT_BACKEND,tokenVal);
    }
    public static String getJwtKey(String siteCode,String userId){
        return CommonUtil.getJwtKey(siteCode, DomainInfoTypeEnum.AGENT_BACKEND,userId);
    }


    /**
     * 获取token的md5值
     */
    public static String getTokenMd5(String token) {
        try{
            if(ObjectUtils.isEmpty(token)) {
                return "";
            }
            if (ObjectUtils.isEmpty(token)) {
               return "";
            }
            Claims claims = JwtUtil.parseToken(token);
            if(ObjectUtils.isEmpty(claims)){
                return "";
            }
            return JwtUtil.getUserKey(claims);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 客户端token校验
     * @param token
     */
   public static AgentTokenVO agentAuth(String token) {
       AgentTokenVO agentTokenVO = new AgentTokenVO();
       if (ObjectUtils.isEmpty(token)) {
           throw new BaowangDefaultException(ResultCode.TOKEN_MISSION);
       }
       Claims claims = null;
       try {
           claims = JwtUtil.parseToken(token);
       } catch (Exception e) {
           log.warn("代理后台 jwt 解密发生异常, 异常token:{}", token);
       }
       if (claims == null) {
           throw new BaowangDefaultException(ResultCode.TOKEN_INVALID);
       }

       String id = JwtUtil.getId(token);
       String siteCode = JwtUtil.getSiteCode(token);
       String agentAccount = JwtUtil.getUserAccount(token);
       String agentId = JwtUtil.getAgentId(token);

       String userKey = JwtUtil.getUserKey(claims);
       boolean isLogin = RedisUtil.isKeyExist(getTokenKey(siteCode,userKey));
       if (!isLogin) {
           throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
       }

       if (ObjectUtils.isEmpty(agentAccount)) {
           throw new BaowangDefaultException(ResultCode.TOKEN_INVALID);
       }

       agentTokenVO.setSiteCode(siteCode);
       agentTokenVO.setAgentAccount(agentAccount);
       agentTokenVO.setId(id);
       agentTokenVO.setAgentId(agentId);

       return agentTokenVO;
   }
}

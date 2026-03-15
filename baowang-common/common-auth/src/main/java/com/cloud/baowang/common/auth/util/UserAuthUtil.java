package com.cloud.baowang.common.auth.util;

import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.common.auth.vo.UserTokenVO;
import com.cloud.baowang.common.core.constants.TokenConstants;
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
 * @description: 会员客户端token工具校验类
 */
@Slf4j
public class UserAuthUtil {

    public static String getTokenKey(String siteCode,String tokenVal){
        return CommonUtil.getTokenKey(siteCode, DomainInfoTypeEnum.WEB_PORTAL,tokenVal);
    }
    public static String getJwtKey(String siteCode,String userId){
        return CommonUtil.getJwtKey(siteCode, DomainInfoTypeEnum.WEB_PORTAL,userId);
    }

    /**
     * 客户端token校验
     * @param token
     */
   public static UserTokenVO userAuth(String token) {
       if (StrUtil.isBlank(token)){
           throw new BaowangDefaultException(ResultCode.TOKEN_MISSION);
       }
       Claims claims = null;
       try {
           claims = JwtUtil.parseToken(token);
       } catch (Exception e) {
           log.warn("客户端 jwt 解密发生异常, 异常token:{}", token);
       }
       if (claims == null) {
           throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
       }

       String userKey = JwtUtil.getUserKey(claims);
       String siteCode = JwtUtil.getSiteCode(token);
       boolean isLogin = RedisUtil.isKeyExist(getTokenKey(siteCode, userKey));
       if (!isLogin) {
           //判断是否有新的token存在，存在则说明被挤出了
           String userId = JwtUtil.getUserId(token);
           String newToken = RedisUtil.getValue(getJwtKey(siteCode,userId));
           if (newToken != null) {
               throw new BaowangDefaultException(ResultCode.LOGIN_ERROR_OTHER_AREA);
           } else {
               throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
           }

       }
       String userAccount = JwtUtil.getUserAccount(token);
       if (ObjectUtils.isEmpty(userAccount)) {
           throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
       }


       String userId = JwtUtil.getUserId(token);

       return UserTokenVO.builder().userAccount(userAccount).userId(userId).siteCode(siteCode).build();
   }
}

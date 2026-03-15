package com.cloud.baowang.common.auth.util;

import com.cloud.baowang.common.auth.vo.BusinessTokenVO;
import com.cloud.baowang.common.core.constants.SecurityConstants;
import com.cloud.baowang.common.core.enums.DomainInfoTypeEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.JwtUtil;
import com.cloud.baowang.common.core.utils.JwtUtils;
import com.cloud.baowang.common.redis.config.RedisUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;


/**
 * 商务后台-权限获取工具类
 *
 * @author qiqi
 */
@Slf4j
public class BusinessAuthUtil {


    public static String getTokenKey(String siteCode,String tokenVal){
        return CommonUtil.getTokenKey(siteCode, DomainInfoTypeEnum.AGENT_MERCHANT,tokenVal);
    }
    public static String getJwtKey(String siteCode,String userId){
        return CommonUtil.getJwtKey(siteCode, DomainInfoTypeEnum.AGENT_MERCHANT,userId);
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

    public static BusinessTokenVO agentAuth(String token) {
        BusinessTokenVO businessTokenVO = new BusinessTokenVO();
        if (ObjectUtils.isEmpty(token)) {
            throw new BaowangDefaultException(ResultCode.TOKEN_MISSION);
        }
        Claims claims = null;
        try {
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("商务后台 jwt 解密发生异常, 异常token:{}", token);
        }
        if (claims == null) {
            throw new BaowangDefaultException(ResultCode.TOKEN_INVALID);
        }

        String id = JwtUtil.getId(token);
        String siteCode = JwtUtil.getSiteCode(token);
        String merchantAccount = JwtUtil.getUserAccount(token);
        String merchantId = JwtUtil.getUserId(token);
        String language = JwtUtil.getValue(token,"language");
        String userKey = JwtUtils.getUserKey(claims);

        boolean isLogin = RedisUtil.isKeyExist(CommonUtil.getTokenKey(siteCode, DomainInfoTypeEnum.AGENT_MERCHANT,userKey));
        if (!isLogin) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }

        if (ObjectUtils.isEmpty(merchantAccount)) {
            throw new BaowangDefaultException(ResultCode.TOKEN_INVALID);
        }
        businessTokenVO.setSiteCode(siteCode);
        businessTokenVO.setMerchantAccount(merchantAccount);
        businessTokenVO.setId(id);
        businessTokenVO.setMerchantId(merchantId);
        businessTokenVO.setLanguage(language);
        return businessTokenVO;
    }
}

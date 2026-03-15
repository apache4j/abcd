package com.cloud.baowang.common.auth.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.cloud.baowang.common.auth.vo.SiteTokenVO;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.constants.SecurityConstants;
import com.cloud.baowang.common.core.enums.DomainInfoTypeEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.JwtUtil;
import com.cloud.baowang.common.core.utils.JwtUtils;
import com.cloud.baowang.common.redis.config.RedisUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * @author: fangfei
 * @createTime: 2024/08/11 22:58
 * @description: 站点后台token校验工具类
 */
@Slf4j
public class SiteAuthUtil {
    public static SiteTokenVO siteAuth(String token) {
        return siteAuth(token, null, false);
    }


    public static String getTokenKey(String siteCode,String tokenVal){
        return CommonUtil.getTokenKey(siteCode, DomainInfoTypeEnum.SITE_BACKEND,tokenVal);
    }
    public static String getJwtKey(String siteCode,String userId){
        return CommonUtil.getJwtKey(siteCode, DomainInfoTypeEnum.SITE_BACKEND,userId);
    }

    /**
     * 删除用户缓存信息
     */
    public static void delLoginUser(String siteCode,String token) {
        if (StringUtils.isNotEmpty(token)) {
            String userKey = JwtUtils.getUserKey(token);
            String userId = JwtUtils.getUserId(token);
            RedisUtil.deleteKey(getTokenKey(siteCode,userKey));
            RedisUtil.deleteKey(getJwtKey(siteCode,userId));
        }
    }


    public static SiteTokenVO siteAuth(String token, String url, Boolean authEnable) {
        SiteTokenVO siteTokenVO = new SiteTokenVO();
        Claims claims = null;
        try {
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("中控后台 jwt 解密发生异常, 异常token:{}", token);
        }
        if (claims == null) {
            throw new BaowangDefaultException(ResultCode.TOKEN_INVALID);
        }
        String userKey = JwtUtils.getUserKey(claims);
        String siteCode = JwtUtils.getSiteCode(token);
        String tokenKey=SiteAuthUtil.getTokenKey(siteCode,userKey);
       // log.info("tokenKey:{}",tokenKey);
        boolean isLogin = RedisUtil.isKeyExist(tokenKey);
        if (!isLogin) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }
        String adminId = JwtUtils.getAdminId(token);
        String userName = JwtUtils.getUserName(claims);

        if (ObjectUtils.isEmpty(adminId) || ObjectUtils.isEmpty(userName)) {
            throw new BaowangDefaultException(ResultCode.TOKEN_INVALID);
        }
        List<String> userRoleIds = JwtUtils.getUserRoleIds(claims);


        Boolean isSuperAdmin = Boolean.parseBoolean(JwtUtils.getSuperAdmin(token));
        // 鉴权
        if (authEnable && !isSuperAdmin){
            if (CollUtil.isNotEmpty(userRoleIds)) {
                checkUserRole(userRoleIds, url);
            }else{
                throw new BaowangDefaultException(ResultCode.NOT_API_PERMISSIONS);
            }
        }

        String desenion = JwtUtils.getValue(claims, SecurityConstants.DATA_DESENSITY);

        siteTokenVO.setAdminId(adminId);
        siteTokenVO.setUserName(userName);
        siteTokenVO.setUserRoleIds(userRoleIds);
        siteTokenVO.setSiteCode(siteCode);
        siteTokenVO.setDataDesensitization("1".equals(desenion));

        return siteTokenVO;
    }

    private static void checkUserRole(List<String> userRoleIds, String url) {
        Boolean notApiPermissions = true;
        for (String roleId : userRoleIds) {
            Map<String, Object> localCachedMap = (Map<String, Object>) RedisUtil.getLocalCachedMap(CacheConstants.KEY_ADMIN_AUTH_INFO_KEY, roleId);
            if (MapUtil.isNotEmpty(localCachedMap) && localCachedMap.containsKey(url)) {
                notApiPermissions = false;
                break;
            }
        }
        // 严禁注释
        if(notApiPermissions){
            throw new BaowangDefaultException(ResultCode.NOT_API_PERMISSIONS);
        }

    }
}

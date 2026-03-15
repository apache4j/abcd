package com.cloud.baowang.common.auth.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.cloud.baowang.common.auth.vo.AdminTokenVO;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.SecurityConstants;
import com.cloud.baowang.common.core.constants.TokenConstants;
import com.cloud.baowang.common.core.enums.DomainInfoTypeEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.JwtUtil;
import com.cloud.baowang.common.core.utils.JwtUtils;
import com.cloud.baowang.common.redis.config.RedisUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * @author: fangfei
 * @createTime: 2024/08/11 22:58
 * @description: 总控后台token校验工具类
 */
@Slf4j
public class AdminAuthUtil {

    public static AdminTokenVO adminAuth(String token) {
        return adminAuth(token, null, false);
    }


    public static String getTokenKey(String tokenVal){
       return CommonUtil.getTokenKey(CommonConstant.ADMIN_CENTER_SITE_CODE, DomainInfoTypeEnum.BACKEND,tokenVal);
    }
    public static String getJwtKey(String userId){
        return CommonUtil.getJwtKey(CommonConstant.ADMIN_CENTER_SITE_CODE, DomainInfoTypeEnum.BACKEND,userId);
    }

    public static AdminTokenVO adminAuth(String token, String url, Boolean authEnable) {
        AdminTokenVO adminTokenVO = new AdminTokenVO();
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
        boolean isLogin = RedisUtil.isKeyExist(getTokenKey(userKey));
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
        adminTokenVO.setAdminId(adminId);
        adminTokenVO.setUserName(userName);
        adminTokenVO.setUserRoleIds(userRoleIds);
        adminTokenVO.setDataDesensitization("1".equals(desenion));

        return adminTokenVO;
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

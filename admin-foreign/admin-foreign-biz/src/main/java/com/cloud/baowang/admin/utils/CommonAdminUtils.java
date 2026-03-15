package com.cloud.baowang.admin.utils;

import com.cloud.baowang.admin.utils.auth.SecurityUtils;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.constants.TokenConstants;
import com.cloud.baowang.common.core.utils.JwtUtil;
import com.cloud.baowang.common.core.utils.JwtUtils;
import com.cloud.baowang.common.core.utils.ServletUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.vo.adminLogin.LoginAdmin;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取当前登录用户权限信息
 *
 * @author qiqi
 */
public class CommonAdminUtils {

    /**
     * 获取登录用户对象
     */
    public static LoginAdmin getLoginAdmin() {
        LoginAdmin loginAdmin = SecurityUtils.getLoginAdmin();
        return loginAdmin;
    }

    /**
     * 获取登录用户Api权限集合
     */
    public static List<String> getAdminApis() {
        HttpServletRequest request = ServletUtil.getRequest();
        if (request == null) return null;
        String token = ServletUtil.getRequest().getHeader(TokenConstants.SIGN);
        List<String> roleIds = JwtUtils.getUserRoleIds(JwtUtil.parseToken(token));
        List<String> apis = new ArrayList<>();
        for (String roleId : roleIds) {
            Map<String, Object> localCachedMap = (Map<String, Object>) RedisUtil.getLocalCachedMap(CacheConstants.KEY_ADMIN_AUTH_INFO_KEY, roleId);
            List<String> urls = localCachedMap.keySet().stream().toList();
            apis.addAll(urls);
        }

        return apis;

    }

    /**
     * 获取登录用户角色id集合
     */
    public static List<String> getRoleIds() {
        LoginAdmin loginAdmin = SecurityUtils.getLoginAdmin();
        return loginAdmin.getRoleIds();
    }

}

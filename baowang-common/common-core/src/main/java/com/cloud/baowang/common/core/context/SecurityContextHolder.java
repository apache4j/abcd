package com.cloud.baowang.common.core.context;

import cn.hutool.core.convert.Convert;
import com.alibaba.nacos.api.utils.StringUtils;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.SecurityConstants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 获取当前线程变量中的 用户id、用户名称、Token等信息
 * 注意： 必须在网关通过请求头的方法传入，同时在HeaderInterceptor拦截器设置值。 否则这里无法获取
 *
 * @author qiqi
 */
public class SecurityContextHolder {
    private static final TransmittableThreadLocal<Map<String, Object>> THREAD_LOCAL = new TransmittableThreadLocal<>();

    public static void set(String key, Object value) {
        Map<String, Object> map = getLocalMap();
        map.put(key, value == null ? StringUtils.EMPTY : value);
    }

    public static String get(String key) {
        Map<String, Object> map = getLocalMap();
        return Convert.toStr(map.getOrDefault(key, StringUtils.EMPTY));
    }

    public static <T> T get(String key, Class<T> clazz) {
        Map<String, Object> map = getLocalMap();
        return (T) (map.getOrDefault(key, null));
    }

    public static Map<String, Object> getLocalMap() {
        Map<String, Object> map = THREAD_LOCAL.get();
        if (map == null) {
            map = new ConcurrentHashMap<String, Object>();
            THREAD_LOCAL.set(map);
        }
        return map;
    }

    public static void setLocalMap(Map<String, Object> threadLocalMap) {
        THREAD_LOCAL.set(threadLocalMap);
    }

    public static String getAdminId() {
        return get(SecurityConstants.DETAILS_ADMIN_ID);
    }

    public static void setUserId(String account) {
        set(SecurityConstants.DETAILS_ADMIN_ID, account);
    }

    public static String getUserName() {
        return get(SecurityConstants.DETAILS_USERNAME);
    }

    public static void setUserName(String username) {
        set(SecurityConstants.DETAILS_USERNAME, username);
    }

    public static String getUserKey() {
        return get(SecurityConstants.ADMIN_KEY);
    }

    public static void setUserKey(String userKey) {
        set(SecurityConstants.ADMIN_KEY, userKey);
    }

    public static Boolean getSuperAdmin() {
        return Boolean.parseBoolean(get(SecurityConstants.SUPER_ADMIN));
    }

    public static void setSuperAdmin(Boolean isSuperAdmin) {
        set(SecurityConstants.SUPER_ADMIN, isSuperAdmin);
    }

    public static String getPermission() {
        return get(SecurityConstants.ROLE_PERMISSION);
    }

    public static void setPermission(String permissions) {
        set(SecurityConstants.ROLE_PERMISSION, permissions);
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }
}

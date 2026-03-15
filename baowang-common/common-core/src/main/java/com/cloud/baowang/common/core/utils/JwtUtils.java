package com.cloud.baowang.common.core.utils;

import cn.hutool.core.convert.Convert;
import com.cloud.baowang.common.core.constants.SecurityConstants;
import com.cloud.baowang.common.core.constants.TokenConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * Jwt工具类
 *
 * @author qiqi
 */
public class JwtUtils {
    public static String secret = TokenConstants.SECRET;

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    public static String createToken(Map<String, Object> claims) {
        String token = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    public static Claims parseToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    /**
     * 根据令牌获取用户标识
     *
     * @param token 令牌
     * @return 用户ID
     */
    public static String getUserKey(String token) {
        Claims claims = parseToken(token);
        return getValue(claims, SecurityConstants.ADMIN_KEY);
    }

    /**
     * 根据令牌获取用户标识
     *
     * @param claims 身份信息
     * @return 用户ID
     */
    public static String getUserKey(Claims claims) {
        return getValue(claims, SecurityConstants.ADMIN_KEY);
    }

    /**
     * 根据令牌获取用户ID
     *
     * @param token 令牌
     * @return 用户ID
     */
    public static String getAdminId(String token) {
        Claims claims = parseToken(token);
        return getValue(claims, SecurityConstants.DETAILS_ADMIN_ID);
    }

    /**
     * 根据身份信息获取用户ID
     *
     * @param claims 身份信息
     * @return 用户ID
     */
    public static String getAdminId(Claims claims) {
        return getValue(claims, SecurityConstants.DETAILS_ADMIN_ID);
    }

    /**
     * 根据令牌获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public static String getUserName(String token) {
        Claims claims = parseToken(token);
        return getValue(claims, SecurityConstants.DETAILS_USERNAME);
    }

    public static String getUserId(String token) {
        Claims claims = parseToken(token);
        return getValue(claims, SecurityConstants.DETAILS_USERID);
    }

    public static String getSuperAdmin(String token) {
        Claims claims = parseToken(token);
        return getValue(claims, SecurityConstants.SUPER_ADMIN);
    }

    /**
     * 根据身份信息获取用户角色id信息
     *
     * @param claims 身份信息
     * @return 用户名
     */
    public static List<String> getUserRoleIds(Claims claims) {
        return getList(claims, SecurityConstants.ADMIN_ROLE_LIST);
    }

    /**
     * 根据Claims获取list结构
     *
     * @param claims 身份信息
     * @return 用户名
     */
    public static List<String> getList(Claims claims, String key) {
        return Convert.toList(String.class, claims.get(key));
    }

    /**
     * 根据身份信息获取用户名
     *
     * @param claims 身份信息
     * @return 用户名
     */
    public static String getUserName(Claims claims) {
        return getValue(claims, SecurityConstants.DETAILS_USERNAME);
    }

    /**
     * 根据身份信息获取键值
     *
     * @param claims 身份信息
     * @param key    键
     * @return 值
     */
    public static String getValue(Claims claims, String key) {
        return Convert.toStr(claims.get(key), "");
    }

    /**
     * 查找指定字符串是否匹配指定字符串列表中的任意一个字符串
     *
     * @param str  指定字符串
     * @param strs 需要检查的字符串数组
     * @return 是否匹配
     */
    public static boolean matches(String str, List<String> strs) {
        if (ObjectUtils.isEmpty(str) || ObjectUtils.isEmpty(strs)) {
            return false;
        }
        for (String pattern : strs) {
            if (isMatch(pattern, str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断url是否与规则配置:
     * ? 表示单个字符;
     * * 表示一层路径内的任意字符串，不可跨层级;
     * ** 表示任意层路径;
     *
     * @param pattern 匹配规则
     * @param url     需要匹配的url
     * @return
     */
    public static boolean isMatch(String pattern, String url) {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, url);
    }

    public static String getSiteCode(String token) {
        Claims claims = parseToken(token);
        return getValue(claims, SecurityConstants.SITE_CODE);
    }

    public static String getUserAccountKey(String token) {
        Claims claims = parseToken(token);
        return getValue(claims, SecurityConstants.USER_ACCOUNT);
    }
}

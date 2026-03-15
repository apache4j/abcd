package com.cloud.baowang.common.core.utils;


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
 */
public class JwtUtil {
    public static String secret = TokenConstants.SECRET;

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    public static String createToken(Map<String, Object> claims) {
        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }

    /**
     * 从数据声明生成令牌, with expiration
     *
     * @param claims      数据声明
     * @param expiredTime
     * @return 令牌
     */
    public static String createToken(Map<String, Object> claims, long expiredTime) {
        claims.put(Claims.EXPIRATION, System.currentTimeMillis() + expiredTime); // TokenConstants.ADMIN_TOKEN_EXPIRE_TIME
        String token = Jwts.builder()
                .setClaims(claims)
                // 不要用 setExpiration, 因為 過期後取值會直接 throw ExpiredJwtException
                .signWith(SignatureAlgorithm.HS512, secret).compact();
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
        return getValue(claims, TokenConstants.USER_KEY);
    }


    /**
     * 根据令牌获取用户标识
     *
     * @param claims 身份信息
     * @return 用户ID
     */
    public static String getUserKey(Claims claims) {
        return getValue(claims, TokenConstants.USER_KEY);
    }

    /**
     * 根据令牌获取用户ID
     *
     * @param token 令牌
     * @return 用户ID
     */
    public static String getUserAccount(String token) {
        Claims claims = parseToken(token);
        return getValue(claims, TokenConstants.DETAILS_USER_ACCOUNT);
    }

    public static String getAccountType(String token) {
        Claims claims = parseToken(token);
        return getValue(claims, TokenConstants.DETAILS_ACCOUNT_TYPE);
    }

    public static String getId(String token) {
        Claims claims = parseToken(token);
        return getValue(claims, TokenConstants.ID);
    }

    /**
     * 根据令牌获取user_id
     *
     * @param token 令牌
     * @return 用户名
     */
    public static String getUserId(String token) {
        Claims claims = parseToken(token);
        return getValue(claims, TokenConstants.DETAILS_USER_ID);
    }

    /**
     * 根据令牌获取user_id
     *
     * @param token 令牌
     * @return 用户名
     */
    public static String getAgentId(String token) {
        Claims claims = parseToken(token);
        return getValue(claims, TokenConstants.DETAILS_AGENT_ID);
    }

    /**
     * 根据身份信息获取用户名
     *
     * @param claims 身份信息
     * @return 用户名
     */
    public static String getUserName(Claims claims) {
        return getValue(claims, TokenConstants.DETAILS_USERNAME);
    }

    /**
     * 根据身份信息获取键值
     *
     * @param claims 身份信息
     * @param key    键
     * @return 值
     */
    public static String getValue(Claims claims, String key) {
        return (String) claims.get(key);
    }

    /**
     * 根据身份信息获取键值
     *
     * @param token 身份信息
     * @param key   键
     * @return 值
     */
    public static String getValue(String token, String key) {
        Claims claims = parseToken(token);
        return (String) claims.get(key);
    }

    private static Long getLong(Claims claims, String key) {
        Object obj = claims.get(key);
        if (obj instanceof Integer) {
            return ((Integer) obj).longValue();
        } else if (obj instanceof Long) {
            return (Long) obj;
        }
        return Long.parseLong((String) obj);
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
        return getValue(claims, TokenConstants.SITE_CODE);
    }


}

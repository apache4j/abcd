package com.cloud.baowang.common.auth.util;

import com.cloud.baowang.common.core.constants.TokenConstants;
import com.cloud.baowang.common.core.enums.DomainInfoTypeEnum;

/**
 * @author: fangfei
 * @createTime: 2024/08/12 10:14
 * @description:
 */
public class CommonUtil {

    /**
     * 获取登录Token Key
     * @param siteCode 站点
     * @param domainInfoTypeEnum 域名类型
     * @param token token值
     * @return key 存储 权限信息
     */
    public static String getTokenKey(String siteCode, DomainInfoTypeEnum domainInfoTypeEnum,String token) {
        return String.format(TokenConstants.LOGIN_TOKEN_KEY,siteCode,domainInfoTypeEnum.getType(),token);
    }

    /**
     * 获取登录 Jwt Key
     * @param siteCode 站点
     * @param domainInfoTypeEnum 域名类型
     * @param userId 登录用户Id
     * @return key 存储 登录token
     */
    public static String getJwtKey(String siteCode, DomainInfoTypeEnum domainInfoTypeEnum, String userId) {
        return String.format(TokenConstants.JWT_CACHE_KEY,siteCode,domainInfoTypeEnum.getType(),userId);
    }
}

package com.cloud.baowang.user.util;

import com.cloud.baowang.common.auth.util.UserAuthUtil;
import com.cloud.baowang.common.core.constants.TokenConstants;
import com.cloud.baowang.common.core.utils.JwtUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import org.springframework.util.ObjectUtils;

/**
 * @author: fangfei
 * @createTime: 2024/11/14 16:05
 * @description:
 */
public class TokenUtil {

    public static boolean delLoginUser(String siteCode,String userId) {
        String token = RedisUtil.getValue(UserAuthUtil.getJwtKey(siteCode,userId));
        if (!ObjectUtils.isEmpty(token)) {
            String userKey = JwtUtil.getUserKey(token);
            RedisUtil.deleteKey(getTokenKey(siteCode,userKey));
            RedisUtil.deleteKey(UserAuthUtil.getJwtKey(siteCode,userId));
        }
        return true;/**/
    }
    private static String getTokenKey(String siteCode,String tokenUUId) {
        return UserAuthUtil.getTokenKey(siteCode,tokenUUId);
    }

}

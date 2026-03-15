package com.cloud.baowang.system.service.member;

import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.redis.config.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 总台-判断某个职员是否被锁定
 */
@Service
public class AdminLogLockStatusService {

    /**
     * 判断当前职员是否已被锁定
     *
     * @param siteCode    站点code,总台为0
     * @param userAccount 用户账号
     * @return true已被锁定, false没有
     */
    public Boolean checkAdminIsLock(String siteCode, String userAccount) {
        String redisKey = String.format(RedisConstants.LOGIN_ERROR_USER, siteCode, userAccount);
        String lockUser = RedisUtil.getValue(redisKey);
        return StringUtils.isNotBlank(lockUser);
    }

    public void removeAdminLockStatus(String siteCode, String userAccount) {
        String redisKey = String.format(RedisConstants.LOGIN_ERROR_USER, siteCode, userAccount);
        RedisUtil.deleteKey(redisKey);
    }

    public void addAdminLockStatus(String siteCode, String userAccount, Long timeStamp) {
        String redisKey = String.format(RedisConstants.LOGIN_ERROR_USER, siteCode, userAccount);
        RedisUtil.setValue(redisKey, userAccount, timeStamp, TimeUnit.MINUTES);
    }
}

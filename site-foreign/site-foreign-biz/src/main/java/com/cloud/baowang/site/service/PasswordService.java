package com.cloud.baowang.site.service;

import com.cloud.baowang.common.core.constants.TokenConstants;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.site.utils.auth.SiteSecurityUtils;
import com.cloud.baowang.system.api.api.AdminLogLockStatusApi;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.site.SiteAdminApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteAdminVO;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 登录密码方法
 *
 * @author qiqi
 */
@Service
@AllArgsConstructor
public class PasswordService {
    private static final Logger log = LoggerFactory.getLogger(PasswordService.class);
    private final SystemDictConfigApi configApi;
    private final SiteAdminApi siteAdminService;

    //private final LoginInfoService loginInfoService;
    private final Long lockTime = TokenConstants.PASSWORD_LOCK_TIME;
    private final AdminLogLockStatusApi statusApi;


    /**
     * 登录账户密码错误次数缓存键名
     *
     * @param username 用户名
     * @return 缓存键key
     */
    private String getCacheKey(String username) {
        return TokenConstants.PWD_ERR_CNT_KEY + username;
    }

    public void validate(SiteAdminVO adminVO, String password, String ip, String loginLocation) {
        String userName = adminVO.getUserName();


        if (statusApi.checkAdminIsLock(adminVO.getSiteCode(), userName)) {
            ResponseVO<SystemDictConfigRespVO> resp = configApi.getByCode(DictCodeConfigEnums.LOGIN_ERROR_LOCK_TIME.getCode(), adminVO.getSiteCode());
            if (resp.isOk()) {
                SystemDictConfigRespVO dictVO = resp.getData();
                //抛出配置的异常
                log.warn("站点:{},帐号被锁定:{}",adminVO.getSiteCode(),userName);
                throw new BaowangDefaultException(dictVO.getHintInfo());
            }
        }

        Integer retryCount = RedisUtil.getValue(getCacheKey(userName));
        if (retryCount == null) {
            retryCount = 0;
        }

        int maxRetryCount = 5;
        if (retryCount >= maxRetryCount) {
            String errMsg = String.format("密码输入错误%s次，帐户锁定", maxRetryCount);
            //siteAdminService.lockAdmin(userName, adminVO.getSiteCode());  //fixme
            //不再去数据库中锁定会员了,改为缓存中设置锁定
            ResponseVO<SystemDictConfigRespVO> resp = configApi.getByCode(DictCodeConfigEnums.LOGIN_ERROR_LOCK_TIME.getCode(), adminVO.getSiteCode());
            if (resp.isOk()) {
                SystemDictConfigRespVO dictVO = resp.getData();
                String configParam = dictVO.getConfigParam();
                if (StringUtils.isNotBlank(configParam)) {
                    //获取锁定时间配置(单位是分钟)
                    Long lockTime = Long.parseLong(configParam);
                    //清空一下统计计数器
                    RedisUtil.deleteKey(getCacheKey(userName));
                    statusApi.addAdminLockStatus(adminVO.getSiteCode(), userName, lockTime);
                    //抛出配置的异常
                    throw new BaowangDefaultException(dictVO.getHintInfo());
                }
            }
            //loginInfoService.recordLoginInfo(userName, 1, errMsg, ip, loginLocation);
            throw new BaowangDefaultException(errMsg);
        }

        if (!matches(adminVO, password)) {
            retryCount = retryCount + 1;
            //loginInfoService.recordLoginInfo(adminVO.getUserName(), 1, "密码错误", ip, loginLocation);
            RedisUtil.setValue(getCacheKey(userName), retryCount);
            throw new BaowangDefaultException("用户名或密码错误");
        } /*else {
            clearLoginRecordCache(userName);
        }*/
    }

    public boolean matches(SiteAdminVO adminVO, String rawPassword) {
        return SiteSecurityUtils.matchesPassword(rawPassword, adminVO.getPassword());
    }

    public void clearLoginRecordCache(String loginName) {
        if (RedisUtil.isKeyExist(getCacheKey(loginName))) {
            RedisUtil.deleteKey(getCacheKey(loginName));
        }
    }
}

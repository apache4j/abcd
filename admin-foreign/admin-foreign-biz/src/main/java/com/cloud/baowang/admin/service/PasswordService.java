package com.cloud.baowang.admin.service;

import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.constants.TokenConstants;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.AdminLogLockStatusApi;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.member.BusinessAdminApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.api.vo.member.BusinessAdminVO;
import com.cloud.baowang.admin.utils.auth.SecurityUtils;
import com.cloud.baowang.system.api.vo.member.BusinessLoginInfoAddVO;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 登录密码方法
 *
 * @author qiqi
 */
@Service
@AllArgsConstructor
public class PasswordService {

//    private int maxRetryCount = TokenConstants.PASSWORD_MAX_RETRY_COUNT;

//    private final SystemBusinessFeignResource systemBusinessFeignResource;
//    private final MemberFeignResource memberFeignResource;

    private final BusinessAdminApi businessAdminService;
    private final SystemDictConfigApi configApi;
    private final AdminLogLockStatusApi statusApi;


    private final LoginInfoService loginInfoService;
    private final Long lockTime = TokenConstants.PASSWORD_LOCK_TIME;


    /**
     * 登录账户密码错误次数缓存键名
     *
     * @param username 用户名
     * @return 缓存键key
     */
    private String getCacheKey(String username) {
        return TokenConstants.PWD_ERR_CNT_KEY + username;
    }

    public void validate(BusinessAdminVO adminVO, String password, String ip, String loginLocation) {
        String userName = adminVO.getUserName();

        if (statusApi.checkAdminIsLock(adminVO.getSiteCode(), userName)) {
            ResponseVO<SystemDictConfigRespVO> resp = configApi.getByCode(DictCodeConfigEnums.LOGIN_ERROR_LOCK_TIME.getCode(), adminVO.getSiteCode());
            if (resp.isOk()) {
                SystemDictConfigRespVO dictVO = resp.getData();
                //抛出配置的异常
                throw new BaowangDefaultException(dictVO.getHintInfo());
            }
        }

        Integer retryCount = RedisUtil.getValue(getCacheKey(userName));

        if (retryCount == null) {
            retryCount = 0;
        }
        // todo update
        /*ResponseVO<List<SystemParamVO>> responseVO = systemBusinessFeignResource.getSystemParamByType(CommonConstant.PASSWORD_MAX_RETRY_COUNT);
        List<SystemParamVO> list = responseVO.getData();
        int maxRetryCount = Integer.valueOf(list.get(0).getCode());*/
        int maxRetryCount = 5;
        if (retryCount >= maxRetryCount) {
            String errMsg = String.format("密码输入错误%s次，帐户锁定", maxRetryCount);
            //businessAdminService.lockAdmin(userName);
            //buildInsertAddLogInfoParam(userName, 1, errMsg, ip, loginLocation,adminVO.getUserId(),adminVO.getSiteCode());
            loginInfoService.recordLoginInfoRecord(buildInsertAddLogInfoParam(userName, 1, errMsg, ip, loginLocation, adminVO.getUserId(), adminVO.getSiteCode()));

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
                    //锁定职员
                    statusApi.addAdminLockStatus(adminVO.getSiteCode(),adminVO.getUserName(),lockTime);

                    //抛出配置的异常
                    throw new BaowangDefaultException(dictVO.getHintInfo());
                }
            }
            throw new BaowangDefaultException(errMsg);
        }

        if (!matches(adminVO, password)) {
            retryCount = retryCount + 1;
            //loginInfoService.recordLoginInfo(adminVO.getUserName(), 1, "密码错误", ip, loginLocation);
            loginInfoService.recordLoginInfoRecord(buildInsertAddLogInfoParam(adminVO.getUserName(), 1, "密码错误", ip, loginLocation, adminVO.getUserId(), adminVO.getSiteCode()));
            RedisUtil.setValue(getCacheKey(userName), retryCount);
            throw new BaowangDefaultException("用户名或密码错误");
        }/* else {
            clearLoginRecordCache(userName);
        }*/
    }

    private BusinessLoginInfoAddVO buildInsertAddLogInfoParam(String userName, int i, String errMsg, String ip, String loginLocation, String userId, String siteCode) {
        BusinessLoginInfoAddVO businessLoginInfoAddVO = new BusinessLoginInfoAddVO();
        businessLoginInfoAddVO.setUserName(userName);
        businessLoginInfoAddVO.setStatus(i);
        businessLoginInfoAddVO.setMsg(errMsg);
        businessLoginInfoAddVO.setIpaddr(ip);
        businessLoginInfoAddVO.setLoginLocation(loginLocation);
        businessLoginInfoAddVO.setUserId(userId);
        businessLoginInfoAddVO.setSiteCode(siteCode);
        return businessLoginInfoAddVO;
    }

    public boolean matches(BusinessAdminVO adminVO, String rawPassword) {
        return SecurityUtils.matchesPassword(rawPassword, adminVO.getPassword());
    }

    public void clearLoginRecordCache(String loginName) {
        if (RedisUtil.isKeyExist(getCacheKey(loginName))) {
            RedisUtil.deleteKey(getCacheKey(loginName));
        }
    }
}

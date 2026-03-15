package com.cloud.baowang.user.service;

import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.common.auth.util.UserAuthUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.core.vo.IPRespVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.user.reponse.UserIpVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.utils.IpAPICoUtils;
import com.cloud.baowang.play.api.api.member.CasinoMemberApi;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.operations.DomainInfoApi;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.api.UserLoginApi;
import com.cloud.baowang.user.api.vo.UserGuideVO;
import com.cloud.baowang.user.api.vo.user.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums.*;

/**
 * 通用服务方法
 */
@Slf4j
@Service
@AllArgsConstructor
public class LoginService {

    private final UserLoginApi userLoginApi;
    private final UserTokenService userTokenService;
    private final DomainInfoApi domainInfoApi;
    private final UserInfoApi userInfoApi;
    private final CasinoMemberApi casinoMemberApi;
    private final SystemDictConfigApi systemDictConfigApi;
    private final AgentInfoApi agentInfoApi;


    public ResponseVO<UserLoginRspVO> userLogin(UserLoginVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        // 获取当前登录IP信息
        String ip = CurrReqUtils.getReqIp();
        IPRespVO ipApiVO = IpAPICoUtils.getIp(ip);
        vo.setIp(ip);
        vo.setSiteCode(siteCode);
        String key = String.format(RedisConstants.USER_PASSWORD_LOGIN_FAIL_COUNT, siteCode, vo.getUserAccount());
        String verifyKey = String.format(RedisConstants.USER_VERIFY_CODE_LOGIN, siteCode, vo.getUserAccount());
        //验证三分钟内登录次数
        checkUserLoginTimesLimit(vo.getUserAccount());
        //错误次数达到指定时间上限
        checkLoginErrorTimeLimit(vo.getUserAccount());

        vo.setIpApiVO(ipApiVO);
        ResponseVO<UserInfoVO> responseVO = userLoginApi.userLogin(vo);
        if (responseVO.getCode() != ResultCode.SUCCESS.getCode()) {
            //登录次数总计
            incrLoginErrorTimeLimit(vo.getUserAccount(),siteCode);
            throw new BaowangDefaultException(responseVO.getMessage());
        }else {
            incrUserLoginTimesLimit(vo.getUserAccount());
            resetLoginErrorTimeLimit(vo.getUserAccount(),false);
        }

        UserInfoVO userInfoVO =  responseVO.getData();

        String oldToken = RedisUtil.getValue(UserAuthUtil.getJwtKey(siteCode,userInfoVO.getUserId()));
        if (!ObjectUtils.isEmpty(oldToken)) {
            userTokenService.delLoginUser(oldToken);
        }

        UserTokenVO userTokenVO = UserTokenVO.builder()
                .id(userInfoVO.getId())
                .userAccount(userInfoVO.getUserAccount())
                .userId(userInfoVO.getUserId())
                .siteCode(userInfoVO.getSiteCode())
                .accountType(userInfoVO.getAccountType()).build();
        String tokenVal = userTokenService.createToken(userTokenVO);

        UserLoginRspVO userLoginRspVO = new UserLoginRspVO();
        userLoginRspVO.setUserAccount(userInfoVO.getUserAccount());
        userLoginRspVO.setUserId(userInfoVO.getUserId());
        userLoginRspVO.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
        userLoginRspVO.setAccountStatus(userInfoVO.getAccountStatus());
        userLoginRspVO.setToken(tokenVal);
        userLoginRspVO.setMainCurrency(userInfoVO.getMainCurrency());
        userLoginRspVO.setFirstLogin(userInfoVO.getFirstLogin());
        userLoginRspVO.setStep(userInfoVO.getStep());
        userLoginRspVO.setReceiveStatus(userInfoVO.getReceiveStatus());

        UserIpVO userIpVO = UserIpVO.builder().countryCode(ipApiVO.getCountryCode()).city(ipApiVO.getCityName())
                .region(ipApiVO.getProvinceName()).ip(ip).build();
        userLoginRspVO.setUserIpVO(userIpVO);

        RedisUtil.deleteKey(key);
        RedisUtil.deleteKey(verifyKey);
        RedisUtil.setValue("verifyCode::" + vo.getCertifyId(), "0", 5L, TimeUnit.MILLISECONDS);
        RedisUtil.deleteKey("verifyCode::" + vo.getCertifyId());

        //登出三方游戏
        try {
            casinoMemberApi.userLogOut(userInfoVO.getUserId());
        } catch (Exception e) {
            log.info("退出三方异常：{}", e.getMessage());
        }

        AgentOtherVO voData=new AgentOtherVO();
        AgentInfoVO agentInfoVO = agentInfoApi.getAgentBenefit(userLoginRspVO.getUserId());;
        if (agentInfoVO != null) {
            BeanUtils.copyProperties(agentInfoVO,voData);
        }
        userLoginRspVO.setAgentOtherVO(voData);
        return ResponseVO.success(ResultCode.LOGIN_SUCCESS.getMessageCode(), userLoginRspVO);

    }

    public ResponseVO<UserLoginRspVO> submitRegister(UserRegisterVO vo) {
        ResponseVO responseVO = userLoginApi.userRegister(vo);
        if (responseVO.isOk()) {
            Map<String, Object> userInfoVO = (Map<String, Object>) responseVO.getData();
            UserLoginVO loginVO = new UserLoginVO();
            loginVO.setPassword(vo.getPassword());
            loginVO.setIp(userInfoVO.get("registerIp").toString());
            loginVO.setUserAccount(vo.getUserAccount());
            loginVO.setDeviceNo(vo.getDeviceNo());
            loginVO.setSubmitKey(true);
            loginVO.setIsRegister(true);
            loginVO.setCertifyId(vo.getCertifyId());
            loginVO.setLoginAddress(vo.getLoginAddress());
            // 注册就是登录，则是第一次登录
            loginVO.setFirstLogin(true);
            loginVO.setVersion(vo.getVersion());
            return userLogin(loginVO);
        }

        return responseVO;
    }

    public void logout( UserInfoVO userInfoVO) {
        UserInfoEditVO editVO = new UserInfoEditVO();
        editVO.setLastLoginTime(System.currentTimeMillis());
        editVO.setId(userInfoVO.getId());
        userInfoApi.updateUserInfoById(editVO);
        String token = userTokenService.getTokenByUserId(userInfoVO.getSiteCode(),userInfoVO.getUserId());
        userTokenService.delLoginUser(token);
    }

    /**
     * 三分钟登录限制
     * @param userAccount
     */

    public void checkUserLoginTimesLimit(String userAccount) {

        //三分钟登录次数限制
        String limitKey = String.format(RedisConstants.KEY_LOGIN_TIMES_THREE_MINUTES_LIMIT,CurrReqUtils.getSiteCode(),userAccount);
        Integer limitValue = RedisUtil.getValue(limitKey);
        SystemDictConfigRespVO configValue = systemDictConfigApi.getByCode(MAX_LOGIN_ATTEMPTS_IN_3_MINUTES.getCode(),CurrReqUtils.getSiteCode()).getData();
        if (limitValue != null && limitValue >= Integer.parseInt(configValue.getConfigParam())) {
            throw new BaowangDefaultException(configValue.getHintInfo());
        }

    }

    /**
     * 用户登录次数++
     * @param userAccount
     */
    public void incrUserLoginTimesLimit(String userAccount) {

        String limitKey = String.format(RedisConstants.KEY_LOGIN_TIMES_THREE_MINUTES_LIMIT,CurrReqUtils.getSiteCode(), userAccount);
        RedisUtil.incrAndExpirationFirst(limitKey,CommonConstant.business_one,CommonConstant.THREE_MINUTES_SECONDS);
    }

    //查询是否锁定
    public void checkLoginErrorTimeLimit(String userAccount) {
        String lockKey = String.format(RedisConstants.KEY_LOGIN_ERROR_FIVE_TIMES_LIMIT,CurrReqUtils.getSiteCode(), userAccount);
        if (RedisUtil.isKeyExist(lockKey)) {
            Long limitSeconds = RedisUtil.getRemainExpireTime(lockKey);
            SystemDictConfigRespVO configValue = systemDictConfigApi.getByCode(LOCKED_FOR_5_FAILED_ATTEMPTS.getCode(),CurrReqUtils.getSiteCode()).getData();
            if (limitSeconds > 0) {
                throw new BaowangDefaultException(configValue.getHintInfo());
            }
        }
    }
    public void resetLoginErrorTimeLimit(String userAccount,boolean lock) {
        String siteCode = CurrReqUtils.getSiteCode();
        String limitKey = String.format(RedisConstants.KEY_LOGIN_ERROR_FIVE_TIMES_LIMIT, siteCode, userAccount);
        if (lock){
            //锁定
            SystemDictConfigRespVO configValue = systemDictConfigApi.getByCode(LOCKED_FOR_5_FAILED_ATTEMPTS.getCode(), siteCode).getData();
            long lockSeconds = Long.parseLong(configValue.getConfigParam())*60L;
            RedisUtil.setValue(limitKey,limitKey,lockSeconds);
        }else {
            //解锁
            RedisUtil.deleteKey(limitKey);
            String loginKey = String.format(RedisConstants.USER_PASSWORD_LOGIN_FAIL_COUNT, CurrReqUtils.getSiteCode(), userAccount);
            RedisUtil.deleteKey(loginKey);
        }
        String key = String.format(RedisConstants.USER_PASSWORD_LOGIN_FAIL_COUNT, siteCode, userAccount);
        RedisUtil.deleteKey(key);

    }
    public void incrLoginErrorTimeLimit(String userAccount,String siteCode) {
        String key = String.format(RedisConstants.USER_PASSWORD_LOGIN_FAIL_COUNT, siteCode, userAccount);
        SystemDictConfigRespVO configValue = systemDictConfigApi.getByCode(LOCKED_FOR_5_FAILED_ATTEMPTS.getCode(),CurrReqUtils.getSiteCode()).getData();
        long lockSeconds = Long.parseLong(configValue.getConfigParam())*60L;
        RedisUtil.incrAndExpirationFirst(key,1, lockSeconds);
        Integer count = RedisUtil.getValue(key);
        if(count != null && count >= 5) {
            resetLoginErrorTimeLimit(userAccount,true);
        }
    }
    //判断是否已经错误5次
    public void checkLoginErrorFiveLimit(String userAccount, String siteCode) {
        String lockKey = String.format(RedisConstants.KEY_LOCKED_FOR_5_FAILED_ATTEMPTS, siteCode, userAccount);
        if (RedisUtil.isKeyExist(lockKey)) {
            SystemDictConfigRespVO configValue = systemDictConfigApi.getByCode(LOCKED_FOR_5_FAILED_ATTEMPTS.getCode(),CurrReqUtils.getSiteCode()).getData();
            if (Integer.parseInt(configValue.getConfigParam()) > 0) {
                throw new BaowangDefaultException(configValue.getHintInfo());
            }
        }
    }

    public ResponseVO<?> setNewUserGuide(UserGuideVO vo) {
       return userLoginApi.setNewUserGuide(vo);
    }
}

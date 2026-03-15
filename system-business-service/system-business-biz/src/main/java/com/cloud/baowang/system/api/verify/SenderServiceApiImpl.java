package com.cloud.baowang.system.api.verify;

import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.api.verify.SenderServiceApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.system.api.vo.verify.VerifyCodeSendVO;
import com.cloud.baowang.system.service.verify.vendor.SenderService;
import com.cloud.baowang.user.api.constants.UserConstant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author: fangfei
 * @createTime: 2024/10/15 19:13
 * @description:
 */
@RestController
@Validated
@Slf4j
public class SenderServiceApiImpl implements SenderServiceApi {
    @Autowired
    private  SenderService senderService;
    @Autowired
    private  SiteApi siteApi;
    @Autowired
    private  SystemDictConfigApi systemDictConfigApi;

    @Value("${spring.profiles.active}")
    private String currentEnv;

    @Override
    public ResponseVO sendMail(VerifyCodeSendVO verifyCodeSendVO) {


        //如果验证码时效还没有过去，则继续用原来的验证码
        String code = RedisUtil.getValue(String.format(RedisConstants.VERIFY_CODE_CACHE, verifyCodeSendVO.getSiteCode(), verifyCodeSendVO.getUserAccount()));
        if (code != null) {
            verifyCodeSendVO.setVerifyCode(code);
        } else {
            verifyCodeSendVO.setVerifyCode(OrderUtil.createNumber(6));
        }
        if("dev".equalsIgnoreCase(currentEnv)){
            verifyCodeSendVO.setVerifyCode(UserConstant.DEFAULT_CODE);
        }

        //不管发送结果如何， 先设置缓存
        //验证码时效
        Long timeLimit = 5 * 60L; //时效默认5分钟
        ResponseVO<SystemDictConfigRespVO> timeConfigRes =  systemDictConfigApi.getByCode(DictCodeConfigEnums.BIND_EMAIL_PHONE_CODE_EXPIRY_TIME.getCode(), verifyCodeSendVO.getSiteCode());
        if (timeConfigRes != null && timeConfigRes.getData() != null && timeConfigRes.isOk()) {
            SystemDictConfigRespVO systemDictConfigRespVO = timeConfigRes.getData();
            String param = systemDictConfigRespVO.getConfigParam();
            timeLimit = Long.parseLong(param);
        }
        RedisUtil.setValue(String.format(RedisConstants.VERIFY_CODE_CACHE, verifyCodeSendVO.getSiteCode(), verifyCodeSendVO.getUserAccount()), verifyCodeSendVO.getVerifyCode(), timeLimit*60);


        //发送前判断次数是否达到上限
        //从参数字典获取超时时间
        int dayLimit = 100;  //每日上限，默认给100
        int hourLimit = 100; //每小时上限，默认给100
        ResponseVO<SystemDictConfigRespVO> dayConfigResp =  systemDictConfigApi.getByCode(DictCodeConfigEnums.EMAIL_CODE_DAILY_LIMIT.getCode(), verifyCodeSendVO.getSiteCode());
        if (dayConfigResp != null && dayConfigResp.getData() != null && dayConfigResp.isOk()) {
            SystemDictConfigRespVO systemDictConfigRespVO = dayConfigResp.getData();
            String param = systemDictConfigRespVO.getConfigParam();
            dayLimit = Integer.parseInt(param);
        }
        ResponseVO<SystemDictConfigRespVO> hourConfigResp =  systemDictConfigApi.getByCode(DictCodeConfigEnums.EMAIL_CODE_HOURLY_LIMIT.getCode(), verifyCodeSendVO.getSiteCode());
        if (hourConfigResp != null && hourConfigResp.getData() != null && hourConfigResp.isOk()) {
            SystemDictConfigRespVO systemDictConfigRespVO = hourConfigResp.getData();
            String param = systemDictConfigRespVO.getConfigParam();
            hourLimit = Integer.parseInt(param);
        }
        SiteVO siteVO = siteApi.getSiteInfoByCode(verifyCodeSendVO.getSiteCode());
        Long startTime = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), siteVO.getTimezone());
        String dayMapKey = String.format(RedisConstants.KEY_SEND_VERIFY_COUNT_MAIL, verifyCodeSendVO.getSiteCode(), verifyCodeSendVO.getUserAccount(), startTime);
        String hourMapKey = String.format(RedisConstants.KEY_SEND_VERIFY_COUNT_MAIL, verifyCodeSendVO.getSiteCode(), verifyCodeSendVO.getUserAccount(), "hour");
        //判断每天上限
        Integer dayCount = RedisUtil.getMapCacheCount(dayMapKey);
        if (dayCount >= dayLimit) {
            return ResponseVO.fail(ResultCode.VERIFY_CODE_LIMIT_DAY);
        }
        //判断小时上限
        Integer hourCount = RedisUtil.getMapCacheCount(hourMapKey);
        if (hourCount >= hourLimit) {
            return ResponseVO.fail(ResultCode.VERIFY_CODE_LIMIT_HOUR);
        }

        ResponseVO responseVO = senderService.sendMail(verifyCodeSendVO);
        if (responseVO.isOk()) {
            RedisUtil.setMapCacheOfExpireTime(dayMapKey, verifyCodeSendVO.getUserAccount() + OrderUtil.createCharacter(6), System.currentTimeMillis(), 24L, TimeUnit.HOURS);
            RedisUtil.setMapCacheOfExpireTime(hourMapKey, verifyCodeSendVO.getUserAccount() + OrderUtil.createCharacter(6), System.currentTimeMillis(), 1L, TimeUnit.HOURS);
            RedisUtil.expireMapCache(dayMapKey, System.currentTimeMillis()/24 + 24*60*60);
            RedisUtil.expireMapCache(hourMapKey, System.currentTimeMillis()/24 + 24*60*60);

        }
        return responseVO;

    }

    @Override
    public ResponseVO sendSms(VerifyCodeSendVO verifyCodeSendVO) {
        //如果验证码时效还没有过去，则继续用原来的验证码
        String code = RedisUtil.getValue(String.format(RedisConstants.VERIFY_CODE_CACHE, verifyCodeSendVO.getSiteCode(), verifyCodeSendVO.getUserAccount()));
        if (code != null) {
            verifyCodeSendVO.setVerifyCode(code);
        } else {
            verifyCodeSendVO.setVerifyCode(OrderUtil.createNumber(6));
        }

        if("dev".equalsIgnoreCase(currentEnv)){
            verifyCodeSendVO.setVerifyCode(UserConstant.DEFAULT_CODE);
        }

        //不管发送结果如何， 先设置缓存
        //验证码时效
        Long timeLimit = 5 * 60L; //时效默认5分钟
        ResponseVO<SystemDictConfigRespVO> timeConfigRes =  systemDictConfigApi.getByCode(DictCodeConfigEnums.BIND_EMAIL_PHONE_CODE_EXPIRY_TIME.getCode(), verifyCodeSendVO.getSiteCode());
        if (timeConfigRes != null && timeConfigRes.getData() != null && timeConfigRes.isOk()) {
            SystemDictConfigRespVO systemDictConfigRespVO = timeConfigRes.getData();
            String param = systemDictConfigRespVO.getConfigParam();
            timeLimit = Long.parseLong(param);
        }
        RedisUtil.setValue(String.format(RedisConstants.VERIFY_CODE_CACHE, verifyCodeSendVO.getSiteCode(), verifyCodeSendVO.getUserAccount()), verifyCodeSendVO.getVerifyCode(), timeLimit*60);

        //发送前判断次数是否达到上限
        //从参数字典获取超时时间
        int dayLimit = 100;  //每日上限，默认给100
        int hourLimit = 100; //每小时上限，默认给100
        ResponseVO<SystemDictConfigRespVO> dayConfigResp =  systemDictConfigApi.getByCode(DictCodeConfigEnums.MOBILE_CODE_DAILY_LIMIT.getCode(), verifyCodeSendVO.getSiteCode());
        if (dayConfigResp != null && dayConfigResp.getData() != null && dayConfigResp.isOk()) {
            SystemDictConfigRespVO systemDictConfigRespVO = dayConfigResp.getData();
            String param = systemDictConfigRespVO.getConfigParam();
            dayLimit = Integer.parseInt(param);
        }
        ResponseVO<SystemDictConfigRespVO> hourConfigResp =  systemDictConfigApi.getByCode(DictCodeConfigEnums.MOBILE_CODE_HOURLY_LIMIT.getCode(), verifyCodeSendVO.getSiteCode());
        if (hourConfigResp != null && hourConfigResp.getData() != null && hourConfigResp.isOk()) {
            SystemDictConfigRespVO systemDictConfigRespVO = hourConfigResp.getData();
            String param = systemDictConfigRespVO.getConfigParam();
            hourLimit = Integer.parseInt(param);
        }
        SiteVO siteVO = siteApi.getSiteInfoByCode(verifyCodeSendVO.getSiteCode());
        Long startTime = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), siteVO.getTimezone());
        String dayMapKey = String.format(RedisConstants.KEY_SEND_VERIFY_COUNT_SMS, verifyCodeSendVO.getSiteCode(), verifyCodeSendVO.getUserAccount(), startTime);
        String hourMapKey = String.format(RedisConstants.KEY_SEND_VERIFY_COUNT_SMS, verifyCodeSendVO.getSiteCode(), verifyCodeSendVO.getUserAccount(), "hour");
        //判断每天上限
        Integer dayCount = RedisUtil.getMapCacheCount(dayMapKey);
        if (dayCount >= dayLimit) {
            return ResponseVO.fail(ResultCode.VERIFY_CODE_LIMIT_DAY);
        }
        //判断小时上限
        Integer hourCount = RedisUtil.getMapCacheCount(hourMapKey);
        if (hourCount >= hourLimit) {
            return ResponseVO.fail(ResultCode.VERIFY_CODE_LIMIT_HOUR);
        }
        ResponseVO responseVO = senderService.sendSms(verifyCodeSendVO);

        if (responseVO.isOk()) {
            RedisUtil.setMapCacheOfExpireTime(dayMapKey, verifyCodeSendVO.getUserAccount() + OrderUtil.createCharacter(6), System.currentTimeMillis(), 24L, TimeUnit.HOURS);
            RedisUtil.setMapCacheOfExpireTime(hourMapKey, verifyCodeSendVO.getUserAccount() + OrderUtil.createCharacter(6), System.currentTimeMillis(), 1L, TimeUnit.HOURS);
            RedisUtil.expireMapCache(dayMapKey, System.currentTimeMillis()/24 + 24*60*60);
            RedisUtil.expireMapCache(hourMapKey, System.currentTimeMillis()/24 + 24*60*60);

        }

        return responseVO;
    }
}

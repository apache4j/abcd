package com.cloud.baowang.user.service;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums.AVATAR_REPLACEMENT_DAILY_LIMIT;
import static com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums.MAX_PASSWORD_CHANGE_DAILY_LIMIT;

/**
 * 通用服务方法
 */
@Slf4j
@Service
@AllArgsConstructor
public class MemberService {
    private final SystemDictConfigApi systemDictConfigApi;

    /**
     * 检查当日头像次数
     * @param userId
     */
    public void checkAvatarChangeTimeLimit(String userId) {
        Integer limitValue = RedisUtil.getValue( String.format(RedisConstants.KEY_AVATAR_CHANGE_TIMES_LIMIT, TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), CurrReqUtils.getTimezone()), userId));
        SystemDictConfigRespVO configValue = systemDictConfigApi.getByCode(AVATAR_REPLACEMENT_DAILY_LIMIT.getCode(),CurrReqUtils.getSiteCode()).getData();
        if (limitValue != null && limitValue >= Integer.parseInt(configValue.getConfigParam())) {
            throw new BaowangDefaultException(configValue.getHintInfo());
        }
    }

    /**
     * 头像更改次数++
     * @param userId
     */
    public void incrAvatarChangeTimeLimit(String userId) {
        String key = String.format(RedisConstants.KEY_AVATAR_CHANGE_TIMES_LIMIT,TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), CurrReqUtils.getTimezone()), userId);
        RedisUtil.incrAndExpirationFirst(key,CommonConstant.business_one,CommonConstant.ONE_DAY_SECONDS);
    }


    /**
     * 检查当日密码修改次数
     * @param userId
     */
    public void checkPwChangeTimeLimit(String userId) {
        Integer limitValue = RedisUtil.getValue( String.format(RedisConstants.KEY_PW_CHANGE_TIMES_LIMIT, TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), CurrReqUtils.getTimezone()), userId));
        SystemDictConfigRespVO configValue = systemDictConfigApi.getByCode(MAX_PASSWORD_CHANGE_DAILY_LIMIT.getCode(),CurrReqUtils.getSiteCode()).getData();
        if (limitValue != null && limitValue >= Integer.parseInt(configValue.getConfigParam())) {
            throw new BaowangDefaultException(configValue.getHintInfo());
        }
    }

    /**
     * 当日密码修改次数++
     * @param userId
     */
    public void incrPwChangeTimeLimit(String userId) {
        String key = String.format(RedisConstants.KEY_PW_CHANGE_TIMES_LIMIT,TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), CurrReqUtils.getTimezone()), userId);
        RedisUtil.incrAndExpirationFirst(key,CommonConstant.business_one,CommonConstant.ONE_DAY_SECONDS);
    }

}

package com.cloud.baowang.activity.service;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.api.UserActivityTypingAmountApi;
import com.cloud.baowang.wallet.api.vo.activity.UserActivityTypingAmountResp;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserActivityTypingAmountVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@AllArgsConstructor
@Slf4j
public class ActivityTypingAmountService {

    private final UserActivityTypingAmountApi userActivityTypingAmountApi;

    public String getUserActivityTypingConfig(UserInfoVO userInfoVO){
        // 首先判断是否配置过，如果配置过，就用配置的，如果没有配置，则走自动派发
        UserActivityTypingAmountResp userActivityTypingAmountLimit = userActivityTypingAmountApi.getUserActivityTypingLimit(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        if (ObjectUtil.isNotEmpty(userActivityTypingAmountLimit)) {
            return userActivityTypingAmountLimit.getLimitGameType();
        }else {
            return null;
        }
    }


    /**
     * @param venueType  活动配置的游戏大类,自动派发的游戏大类
     * @param userInfoVO 用户信息
     */
    public String initUserActivityTypingAmountLimit(String venueType, UserInfoVO userInfoVO) {
        // 首先判断是否配置过，如果配置过，就用配置的，如果没有配置，则走自动派发
        UserActivityTypingAmountResp userActivityTypingAmountLimit = userActivityTypingAmountApi.getUserActivityTypingLimit(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        if (ObjectUtil.isNotEmpty(userActivityTypingAmountLimit)) {
            return userActivityTypingAmountLimit.getLimitGameType();
        }
        // 判断活动是否自动派发，如果是自动派发，则添加游戏大类是电子，如果没有电子，则随机一个游戏大类
        String venueTypeSelect = VenueTypeEnum.ELECTRONICS.getCode().toString();
        if (venueType.contains(VenueTypeEnum.ELECTRONICS.getCode().toString())) {
            venueTypeSelect = VenueTypeEnum.ELECTRONICS.getCode().toString();
        } else {
            String valueCode = venueType.split(CommonConstant.COMMA)[0];
            if (ObjectUtil.isNotEmpty(valueCode)) {
                venueTypeSelect = valueCode;
            }
        }
        UserActivityTypingAmountVO userActivityTypingAmountVO = UserActivityTypingAmountVO.builder()
                .userId(userInfoVO.getUserId())
                .userAccount(userInfoVO.getUserAccount())
                .siteCode(userInfoVO.getSiteCode())
                .limitGameType(venueTypeSelect)
                .currency(userInfoVO.getMainCurrency())
                .typingAmount(BigDecimal.ZERO)
                .build();
        // 直接进行初始化了
        userActivityTypingAmountApi.initUserActivityTypingAmountLimit(userActivityTypingAmountVO);
        log.info("活动配置的游戏大类,初始化插入流水限制{}", JSONObject.toJSONString(userActivityTypingAmountVO));
        return venueTypeSelect;
    }


}

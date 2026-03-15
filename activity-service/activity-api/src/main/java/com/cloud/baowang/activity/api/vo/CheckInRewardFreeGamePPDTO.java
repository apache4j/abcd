package com.cloud.baowang.activity.api.vo;

import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @className: CheckInRewardFreeGamePPDTO
 * @author: wade
 * @description: 签到奖励免费游戏参数
 * @date: 21/8/25 18:20
 */
@Data
public class CheckInRewardFreeGamePPDTO {
    /**
     * 免费游戏次数
     */

    private Integer acquireNum;
    /**
     * 游戏场馆
     */
    private String venueCode;

    /**
     * pp游戏code
     */
    private String accessParameters;

    /**
     * 限注金额
     */
    private BigDecimal betLimitAmount;


}

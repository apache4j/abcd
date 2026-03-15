package com.cloud.baowang.activity.api.vo;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.activity.api.enums.ActivityWeekDayEnum;
import com.cloud.baowang.activity.api.enums.CheckInRewardTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "周奖励，月奖励配置")
@Data
@Slf4j
public class CheckInRewardConfigVO implements Serializable {

    //==========固定金额时传递==============
    /**
     * {@link CheckInRewardTypeEnum}
     * system_param 中 checkIn_reward_type
     */
    @Schema(description = "奖励方式")
    private String rewardType;

    /**
     * {@link ActivityWeekDayEnum}
     */
    @Schema(description = "周奖励第几天，1-7星期一到星期七，8-满周，月奖励第几月，1-12表示1月到12月 累计奖励配置次数")
    private Integer code;

    /**
     * {@link ActivityWeekDayEnum}
     */
    @Schema(description = "累计奖励配置次数-输入天数")
    private Integer dayLimit;


    @Schema(description = "赠送免费旋转次数")
    private Integer acquireNum;

    @Schema(description = "赠送金额")
    private BigDecimal acquireAmount;

    /**
     * 游戏场馆
     */
    @Schema(description = "游戏场馆")
    private String venueCode;

    /**
     * pp游戏code
     */
    @Schema(description = "pp游戏code")
    private String accessParameters;

    /**
     * 限注金额
     */
    @Schema(description = "限注金额")
    private BigDecimal betLimitAmount;



    @Schema(description = "币种")
    private String currencyCode;

    public boolean validate() {
        if (ObjectUtil.isNull(rewardType)) {
            log.info("参数 rewardType 不能为空");
            return false;
        }

        if (ObjectUtil.isNull(code)) {
            log.info("参数 code 不能为空");
            return false;
        }

       /* if (StrUtil.isBlank(currencyCode)) {
            log.info("参数 currencyCode 不能为空");
           // return false;
        }*/

        if (acquireNum == null && acquireAmount == null) {
            log.info("acquireNum 和 acquireAmount 不能同时为 null");
            return false;
        }

        if (acquireNum != null && acquireNum < 0) {
            log.info("acquireNum 不能为负数");
            return false;
        }

        if (acquireAmount != null && acquireAmount.compareTo(BigDecimal.ZERO) < 0) {
            log.info("acquireAmount 不能为负数");
            return false;
        }
        // 如果是免费旋转，判断这三个参数
        if (CheckInRewardTypeEnum.FREE_WHEEL.getType().equals(rewardType)) {
            if (StrUtil.isBlank(venueCode)) {
                log.info("参数 venueCode 不能为空");
                return false;
            }
            if (StrUtil.isBlank(accessParameters)) {
                log.info("参数 accessParameters 不能为空");
                return false;
            }
            if (betLimitAmount == null || betLimitAmount.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("参数 betLimitAmount 不能为空");
                return false;
            }
        }

        return true;
    }

}

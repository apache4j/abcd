package com.cloud.baowang.activity.api.vo.v2;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.activity.api.enums.ActivityWeekDayEnum;
import com.cloud.baowang.activity.api.enums.CheckInRewardTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "周奖励，月奖励配置-返回给前端")
@Data
@Slf4j
public class CheckInRewardConfigV2RespVO implements Serializable {

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



    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "1=周奖励,2=月奖励第几月，3-累计奖励配置次数")
    private Integer rewardFrom;

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

        return true;
    }

}

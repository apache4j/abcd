package com.cloud.baowang.user.api.vo.medal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/29 09:52
 * @Version: V1.0
 **/
@Data
@Schema(description = "勋章奖励配置修改")
public class MedalRewardConfigUpdateReqVO {

    /**
     * 奖励编号
     */
    @Schema(description = "奖励编号")
    private Integer rewardNo;

    /**
     * 解锁勋章数
     */
    @Schema(description = "解锁勋章数")
    private Integer unlockMedalNum;

    /**
     * 奖励金额
     */
    @Schema(description = "奖励金额")
    private BigDecimal rewardAmount;

    /**
     * 打码倍数
     */
    @Schema(description = "打码倍数")
    private BigDecimal typingMultiple;

}

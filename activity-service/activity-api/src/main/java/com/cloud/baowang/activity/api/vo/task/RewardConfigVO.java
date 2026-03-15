package com.cloud.baowang.activity.api.vo.task;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @className: SiteTaskConfigPO
 * @author: wade
 * @description: 任务配置
 * @date: 18/9/24 14:38
 */
@Schema(description = "任务保存-存款与邀请-dto奖励-入参")
@Data
@Builder
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class RewardConfigVO implements Serializable {

    /**
     * 配置台阶
     */
    @Schema(title = "配置台阶 1,2,3")
    private Integer step;


    /**
     * 最小配置金额
     */
    @Schema(title = "最小配置金额")
    private BigDecimal minBetAmount;


    /**
     * 彩金奖励
     */
    @Schema(title = "彩金奖励")
    //@NotNull(message = "彩金奖励不能为空")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal rewardAmount;


}

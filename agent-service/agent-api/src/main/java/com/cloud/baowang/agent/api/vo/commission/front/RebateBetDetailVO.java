package com.cloud.baowang.agent.api.vo.commission.front;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/11/07 12:13
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
@Schema(title = "返点数据", description = "返点数据")
@Builder
public class RebateBetDetailVO {
    @Schema(description = "代理id")
    private String agentId;
    @Schema(title = "场馆类型")
    private Integer venueType;
    @Schema(description = "有效流水")
    private BigDecimal validAmount;
    @Schema(description = "有效流水返点")
    private BigDecimal commissionAmount = BigDecimal.ZERO;
    @Schema(description = "人头费-审核界面调整金额")
    private BigDecimal adjustAmount=BigDecimal.ZERO;
    @Schema(description = "有效流水返点-审核界面调整金额")
    private BigDecimal rebateAdjustAmount=BigDecimal.ZERO;

}

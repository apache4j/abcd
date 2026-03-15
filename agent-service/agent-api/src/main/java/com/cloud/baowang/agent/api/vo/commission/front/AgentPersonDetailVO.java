package com.cloud.baowang.agent.api.vo.commission.front;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/11/09 23:30
 * @description: 人头费
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
@Schema(title = "人头费", description = "人头费")
public class AgentPersonDetailVO {
    @Schema(description = "代理Id")
    private String agentId;
    @Schema(description = "人头费")
    private BigDecimal commissionAmount = new BigDecimal("0.0000");
    @Schema(description = "有效新增")
    private Integer newActiveNumber = 0;
    @Schema(description = "人头费调整金额-审核界面")
    private BigDecimal reviewAdjustAmount = new BigDecimal("0.0000");

}

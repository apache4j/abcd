package com.cloud.baowang.agent.api.vo.commission;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/11/09 23:30
 * @description: 人头费
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
@Schema(title = "人头费详情", description = "人头费详情")
public class CommissionPersonDetailVO {
    @Schema(description = "代理Id")
    private String agentId;
    @Schema(description = "货币")
    private String currency;
    @Schema(description = "本期人头费合计")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal commissionAmount = BigDecimal.ZERO;
    @Schema(description = "有效新增人头费/人")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal newUserAmount = BigDecimal.ZERO;
    @Schema(description = "有效新增人数")
    private Integer newActiveNumber = 0;
    @Schema(title = "会员列表", description = "会员列表")
    private Page<String> userAccountPage;

    @Schema(description = "人头费-佣金调整金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal personAdjustAmount = BigDecimal.ZERO;

    @Schema(description = "人头费-总返点金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal personTotalAmount = BigDecimal.ZERO;

}

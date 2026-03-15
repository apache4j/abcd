package com.cloud.baowang.agent.api.vo.commission.front;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/11/07 12:31
 * @description: 人头费未发放佣金信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
@Schema(title = "人头费已发放佣金信息", description = "人头费已发放佣金信息")
public class PersonSettledReportVO {
    @Schema(description = "已发放佣金预估合计")
    private BigDecimal commissionTotal = new BigDecimal("0.0000");
    @Schema(description = "佣金调整金额")
    private BigDecimal reviewAdjustAmount;
    @Schema(description = "人头费")
    private PersonGeneralVO personGeneralVO;
}

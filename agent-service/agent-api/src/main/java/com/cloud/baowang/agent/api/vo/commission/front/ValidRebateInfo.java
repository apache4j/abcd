package com.cloud.baowang.agent.api.vo.commission.front;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: fangfei
 * @createTime: 2024/11/07 12:52
 * @description: 有效流水返点类型信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
@Schema(title = "有效流水返点类型信息", description = "有效流水返点类型信息")
public class ValidRebateInfo {
    @Schema(description = "未发放佣金信息")
    private RebateNotSettleReportVO notSettleReportVO;
    @Schema(description = "已发放佣金信息")
    private RebateSettledReportVO settledReportVO;
    @Schema(description = "子代佣金信息，只有子代有值")
    private SubCommissionGeneralVO subCommissionGeneralVO;
}

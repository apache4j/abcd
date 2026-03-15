package com.cloud.baowang.agent.api.vo.commission.front;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: fangfei
 * @createTime: 2024/11/07 12:52
 * @description: 负盈利类型信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
@Schema(title = "负盈利类型信息", description = "负盈利类型信息")
public class NegProfitInfo {
    @Schema(description = "未发放佣金信息")
    private NegNotSettleReportVO notSettleReportVO;
    @Schema(description = "已发放佣金信息")
    private NegSettledReportVO settledReportVO;
    @Schema(description = "子代负盈利佣金信息，只有子代有值")
    private SubCommissionGeneralVO subCommissionGeneralVO;
}

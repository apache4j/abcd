package com.cloud.baowang.agent.api.vo.commission.front;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: fangfei
 * @createTime: 2024/11/07 12:52
 * @description: 人头费类型信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
@Schema(title = "人头费类型信息", description = "人头费类型信息")
public class PersonProfitInfo {
    @Schema(description = "未发放佣金信息")
    private PersonNotSettleReportVO notSettleReportVO;
    @Schema(description = "已发放佣金信息")
    private PersonSettledReportVO settledReportVO;
    @Schema(description = "子代负盈利佣金信息，只有子代有值")
    private SubCommissionGeneralVO subCommissionGeneralVO;
}

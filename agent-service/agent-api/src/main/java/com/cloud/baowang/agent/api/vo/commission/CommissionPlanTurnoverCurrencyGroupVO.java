package com.cloud.baowang.agent.api.vo.commission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 有效流水方案配币种分组VO
 *
 * @author remo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
//@I18nClass
@Schema(title = "有效流水方案配币种分组VO", description = "有效流水方案配币种分组VO")
public class CommissionPlanTurnoverCurrencyGroupVO implements Serializable {
    @Schema(title = "币种")
    private String currency;

    @Schema(title = "配置明细（有效投注 + 返佣比例）")
    private List<CommissionPlanTurnoverConfigItemVO> configs;
}

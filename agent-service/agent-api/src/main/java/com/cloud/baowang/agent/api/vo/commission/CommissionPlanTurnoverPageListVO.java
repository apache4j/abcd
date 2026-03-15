package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 有效流水佣金方案配置分页VO
 *
 * @author remo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
//@I18nClass
@Schema(title = "有效流水佣金方案配置分页VO", description = "代理佣金方案配置分页VO")
public class CommissionPlanTurnoverPageListVO extends BaseVO implements Serializable {
    @Schema(title = "方案编码")
    private String planCode;

    @Schema(title = "方案名称")
    private String planName;

    @Schema(title = "使用代理人数")
    private Long agentCount;

    @Schema(title = "备注")
    private String remark;
}

package com.cloud.baowang.agent.api.vo.agentreview.info;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 代理编辑信息
 * </p>
 *
 * @author kimi
 * @since 2023-10-10
 */
@Data
@Schema(description = "代理编辑信息")
public class AgentInfoModifyVO {

    @Schema(description = "代理id")
    private String id;

    @Schema(description = "风控层级id")
    private String riskLevelId;


}

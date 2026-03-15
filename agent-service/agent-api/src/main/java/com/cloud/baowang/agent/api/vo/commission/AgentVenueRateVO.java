package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/11/05 22:54
 * @description:
 */
@Data
@Schema(title = "代理佣金场馆费率表VO", description = "代理佣金场馆费率表VO")
public class AgentVenueRateVO extends BaseVO {
    @Schema(title = "佣金方案ID")
    private String planId;
    @Schema(title = "场馆code")
    private String venueCode;
    @Schema(title = "场馆费率")
    private String rate;
    @Schema(title = "有效流水费率")
    private String validRate;
}

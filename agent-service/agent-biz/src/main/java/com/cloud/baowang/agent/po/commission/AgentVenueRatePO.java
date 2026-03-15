package com.cloud.baowang.agent.po.commission;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/11/05 22:54
 * @description:
 */
@Data
@TableName("agent_venue_rate")
@Schema(title = "代理佣金场馆费率表", description = "代理佣金场馆费率表")
public class AgentVenueRatePO extends BasePO {
    @Schema(title = "佣金方案ID")
    private String planId;
    @Schema(title = "场馆code")
    private String venueCode;
    @Schema(title = "场馆费率")
    private String rate;
    @Schema(title = "有效流水费率")
    private String validRate;
}

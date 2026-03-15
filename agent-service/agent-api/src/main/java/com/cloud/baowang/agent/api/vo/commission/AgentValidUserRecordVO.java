package com.cloud.baowang.agent.api.vo.commission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/12/19 20:39
 * @description:
 */
@Data
@Schema(title = "有效新增会员记录表", description = "有效新增会员记录表")
public class AgentValidUserRecordVO {
    @Schema(title = "负盈利佣金或者返点表的主键id")
    private String reportId;

    @Schema(title = "会员ID")
    private String userId;

    @Schema(title = "代理ID")
    private String agentId;

    @Schema(title = "佣金类型")
    private String commissionType;

    @Schema(title = "方案code")
    private String planCode;

    @Schema(title = "是否已统计为有效新增  0 否  1 是")
    private Integer isSettle;

    @Schema(description = "周期开始时间")
    private Long startTime;

    @Schema(description = "周期结束时间")
    private Long endTime;

}

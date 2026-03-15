package com.cloud.baowang.agent.api.vo.agentreview.info;


import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jdk.jfr.Description;
import lombok.Data;

@Data
@Schema(description = "代理信息变更记录分页查询条件入参")
public class AgentInfoChangeRecordQueryVO extends PageVO {
    @Schema(description = "siteCode")
    private String siteCode;

    @Schema(description = "操作开始时间")
    private Long operatorStartTime;
    @Schema(description = "操作结束时间")
    private Long operatorEndTime;
    @Schema(description = "代理账号")
    private String agentAccount;
    @Schema(description = "代理类型")
    private Integer agentType;
    @Schema(description = "变更类型")
    private String changeType;
    @Schema(description = "操作人")
    private String operator;
    /**
     * 数据脱敏 true 需要脱敏 false 不需要脱敏
     */
    private Boolean dataDesensitization=false;
}

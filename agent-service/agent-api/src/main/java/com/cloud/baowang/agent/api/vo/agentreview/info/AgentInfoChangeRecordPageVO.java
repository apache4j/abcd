package com.cloud.baowang.agent.api.vo.agentreview.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "代理信息变更记录分页查询返回实体")
public class AgentInfoChangeRecordPageVO {
    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "代理类型")
    private Integer agentType;

    @Schema(description = "代理类型 文本")
    private String agentTypeText;

    @Schema(description = "变更类型")
    private Integer changeType;

    @Schema(description = "变更类型 文本")
    private String changeTypeText;

    @Schema(description = "变更前内容")
    private String changeBefore;

    @Schema(description = "变更后内容")
    private String changeAfter;

    @Schema(description = "操作人")
    private String operator;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "操作时间")
    private Long operatorTime;

}

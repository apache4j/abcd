package com.cloud.baowang.agent.api.vo.agentLogin;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;


/**
 * 代理token携带的信息
 */
@Data
@Builder
public class AgentAccountVO {
    private String id;
    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "代理编号")
    private String agentId;

    @Schema(description = "siteCode")
    private String siteCode;

    @Schema(description = "代理类型 1正式 2测试 3合作")
    private Integer agentType;

}

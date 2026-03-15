package com.cloud.baowang.agent.api.vo.agentinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption: 代理信息查询
 * @Author: Ford
 * @Date: 2024/11/6 17:36
 * @Version: V1.0
 **/
@Data
@Schema(description = "代理信息查询条件")
public class AgentInfoCondVo {
    @Schema(title = "注册日期-开始时间")
    private Long startRegisterDay;
    @Schema(title = "注册日期-结束时间")
    private Long endRegisterDay;
    @Schema(title = "代理账号")
    private String agentAccount;
    @Schema(title = "直属上级账号")
    private String superAgentAccount;
    @Schema(title = "代理类型 字典类型:agent_type")
    private String agentType;
    @Schema(title = "代理级别")
    private String agentLevel;
    @Schema(title = "代理类别 字典类型:agent_category")
    private String agentCategory;
    @Schema(title = "站点编号")
    private String siteCode;
    @Schema(title = "邀请码")
    private String inviteCode;
}

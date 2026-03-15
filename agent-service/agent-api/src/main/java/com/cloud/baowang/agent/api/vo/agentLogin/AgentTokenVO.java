package com.cloud.baowang.agent.api.vo.agentLogin;

import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentTokenVO  implements Serializable {

    @Schema(title = "Id")
    private String id;

    @Schema(description = "代理编号")
    private String agentId;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "代理类型 1正式 2商务 3置换")
    private Integer agentType;

    @Schema(description = "账号状态 1正常 2登录锁定 3充提锁定(状态多选,用逗号分开)")
    private String status;

    @Schema(description = "风控层级id")
    private Long riskLevelId;

    private String token;

    private Date loginTime;

    private Long expireTime;

    private String siteCode;

}

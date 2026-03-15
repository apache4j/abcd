package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("agent_security")
@Schema(description = "代理密保对象")
public class AgentSecurityPO extends SiteBasePO {

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "密保问题ID")
    private String securityQuestionId;

    @Schema(description = "密保问题")
    private String securityQuestion;

    @Schema(description = "密保答案")
    private String securityAnswer;


}

package com.cloud.baowang.agent.api.vo.manualup;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author: kimi
 */
@Data
@Schema(description = "代理人工加额审核详情 返回")
@I18nClass
public class AgentUpReviewDetailsVO {

    @Schema(description = "代理注册信息")
    private GetAgentRegisterInfoVO registerInfo;

    @Schema(description = "代理账号信息")
    private GetByAgentInfoVO agentInfo;

    @Schema(description = "账号风控层级")
    private AgentRiskControlVO riskControl;

    @Schema(description = "审核详情")
    private AgentReviewDetailVO reviewDetail;

    @Schema(description = "审核信息")
    private List<ReviewInfoVO> reviewInfos;
}

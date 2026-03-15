package com.cloud.baowang.agent.api.vo.depositWithdraw;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author: qiqi
 */
@Data
@I18nClass
@Schema(title = "代理提款审核详情返回对象")
public class AgentWithdrawReviewDetailsVO {

    @Schema(description= "代理注册信息")
    private AgentRegisterInfoVO registerInfo;

    @Schema(description= "代理账号信息")
    private DepositWithdrawAgentInfoVO agentInfo;

    @Schema(description="账号风控层级")
    private AgentRiskControlVO riskControl;


    @Schema(description="近期提款信息")
    private AgentRecentlyDepositWithdrawVO recentlyDepositWithdrawVO;

    @Schema(description="本次提款信息")
    private AgentWithdrawReviewDetailVO withdrawReviewDetailVO;

    @Schema(description="审核信息")
    private List<AgentWithdrawReviewInfoVO> reviewInfos;
}

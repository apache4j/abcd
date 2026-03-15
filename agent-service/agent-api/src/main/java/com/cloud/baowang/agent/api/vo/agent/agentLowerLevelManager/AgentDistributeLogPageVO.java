package com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description ="代理分配日志分页返回vo")
public class AgentDistributeLogPageVO {

    @Schema(description ="分配用户名")
    private String account;

    @Schema(description ="转账用户名")
    private String transferAccount;

    @Schema(description ="收款用户名")
    private String collectionAccount;

    @Schema(description ="账号类型")
    private Integer accountType;

    @Schema(description ="账号类型--文本")
    private String accountTypeText;

    @Schema(description ="分配时间")
    private Long distributeTime;

    @Schema(description ="分配前额度")
    private String beforeDistributionAmount;

    @Schema(description ="分配金额")
    private String distributionAmount;

    @Schema(description ="分配后额度")
    private String afterDistributionAmount;

    @Schema(description ="流水倍数")
    private String turnoverMultiple;

    @Schema(description ="钱包类型")
    private Integer walletType;

    @Schema(description ="转出钱包")
    private String transferOutCoin;

    @Schema(description ="转入钱包")
    private String transferInCoin;

    @Schema(description ="备注")
    private String remark;

}

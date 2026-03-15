package com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/6/17 15:18
 * @Version: V1.0
 **/
@Data
@Schema(description = "代理下级管理分页查询vo")
public class AgentLowerLevelManagerPageVO {

    @Schema(description = "代理账号")
    private String agentAccount;
    @Schema(description = "下线用户数")
    private Integer subLineUserNum = 0;
    //    @Schema(description ="活跃用户数")
//    private Integer activeUserNum = 0;
    @Schema(description = "有效活跃用户数")
    private Integer validActiveUserNum = 0;
    @Schema(description = "用户首存数")
    private Integer userFirstDepositNum = 0;
    @Schema(description = "有效新增")
    private Integer validAddUserNum = 0;
    @Schema(description = "用户首存金额")
    private BigDecimal userFirstDepositAmount = BigDecimal.ZERO;
    @Schema(description = "用户存款金额")
    private BigDecimal userDepositAmount = BigDecimal.ZERO;
    @Schema(description = "用户输赢金额")
    private BigDecimal userWinLoseAmount = BigDecimal.ZERO;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "注册时间")
    private Long registerTime;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "流水")
    private BigDecimal validBetAmount = BigDecimal.ZERO;

}


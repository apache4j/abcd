package com.cloud.baowang.agent.api.vo.agentCoin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author : 小智
 * @Date : 23/10/23 5:51 PM
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description ="代理转账记录查询返回分页对象")
public class AgentTransferPageRecordVO implements Serializable {

    /* 代理id */
    @Schema(description ="代理id")
    private String agentId;

    /* 订单号 */
    @Schema(description ="订单号")
    private String orderNo;

    /* 代理账号 */
    @Schema(description ="代理账号")
    private String agentAccount;

    /* 转账时间 */
    @Schema(description ="转账时间")
    private Long transferTime;

    @Schema(description ="转账分割时间(mm-dd)")
    private Long transferGroupTime;

    /* 转账类型 */
    @Schema(description ="转账类型code")
    private String transferType;

    @Schema(description ="转账类型名称")
    private String transferTypeName;

    /* 转账金额 */
    @Schema(description ="转账金额")
    private BigDecimal transferAmount;

    /* 转账账号id */
    @Schema(description ="目的转账代理Id")
    private String transferAgentId;

    /* 转账账号 */
    @Schema(description ="目的转账代理账号")
    private String transferAccount;

    /* 备注 */
    @Schema(description ="备注")
    private String remark;

    /* 转账状态 */
    @Schema(description ="转账状态code")
    private Integer transferStatus;

    /* 转账状态名称 */
    @Schema(description ="转账状态名称")
    private String transferStatusName;

    @Schema(description ="转账方式")
    private String direction;

    @Schema(description ="转账方式名称")
    private String directionName;
}

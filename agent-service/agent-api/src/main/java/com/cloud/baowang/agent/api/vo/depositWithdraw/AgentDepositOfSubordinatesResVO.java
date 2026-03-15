package com.cloud.baowang.agent.api.vo.depositWithdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description ="代理代会员存款详细返回对象")
public class AgentDepositOfSubordinatesResVO implements Serializable {
    @Schema(description ="代理账号")
    private String agentId;
    @Schema(description ="代理账号")
    private String agentAccount;

    @Schema(description ="代存类型（1 佣金代存 2额度代存）")
    private String depositSubordinatesType;

    @Schema(description ="代存类型名称")
    private String depositSubordinatesTypeName;
    @Schema(description ="代存会员账号Id")
    private String userId;
    @Schema(description ="代存会员账号")
    private String userAccount;

    @Schema(description ="会员名称")
    private String userName;

    @Schema(description ="代存金额")
    private BigDecimal amount;

    @Schema(description ="订单号")
    private String orderNo;

    @Schema(description ="代存时间")
    private Long depositTime;

    @Schema(description ="状态名称")
    private String statusName;

    @Schema(description ="备注")
    private String remark;

    @Schema(description ="注册时间")
    private Long registerTime;

    @Schema(description = "币种")
    private String currencyCode;


    /**
     * 平台币金额
     * platform_amount
     */
    @Schema(description = "平台币金额")
    private BigDecimal platformAmount;

    /**
     * 汇率 transfer_rate
     */
    private BigDecimal transferRate;

    @Schema(description = "流水倍数")
    private BigDecimal runningWaterMultiple;

}

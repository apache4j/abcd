package com.cloud.baowang.agent.api.vo.agentCoin;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author : 小智
 * @Date : 26/10/23 6:59 PM
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description ="代理转账记录查询返回分页请求对象")
public class AgentTransferRecordPageParam extends PageVO implements Serializable {

    @Schema(description ="订单号")
    private String orderNo;

    @Schema(description ="转出代理账号")
    private String fromAgentAccount;

    @Schema(description ="转入代理账号")
    private String toAgentAccount;

    @Schema(description ="转出钱包")
    private String walletType;

    @Schema(description ="订单状态")
    private Integer orderStatus;

    @Schema(description ="转账初始金额")
    private BigDecimal transferStartAmount;

    @Schema(description ="转账结束金额")
    private BigDecimal transferEndAmount;

    @Schema(description ="转账开始时间")
    private Long startTime;

    @Schema(description ="转账结束时间")
    private Long endTime;

    //站点编号
    @Schema(description ="站点编号",hidden = true)
    private String siteCode;
}

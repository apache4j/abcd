package com.cloud.baowang.agent.api.vo.agentCoin;

import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.core.vo.base.SitePageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "代理账变记录请求VO")
public class AgentCoinRecordRequestVO extends SitePageVO implements Serializable {

    @Schema(description="账变开始时间")
    private Long dateTimeBegin;

    @Schema(description="账变结束时间")
    private Long dateTimeEnd;

    @Schema(description="订单号")
    private String orderNo;


    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "代理名称")
    private String agentName;

    @Schema(description = "账号状态 字典code: agent_status")
    private String accountStatus;


    @Schema(description = "风控层级")
    private String riskLevel;

    @Schema(description = "钱包类型 字典code: agent_wallet_type")
    private String walletType;

    @Schema(description = "账变业务类型 ")
    private String businessCoinType;

    @Schema(description="业务类型列表")
    private List<String> businessCoinTypeList;

    @Schema(description = "账变类型")
    private String coinType;

    @Schema(description="账变类型，多选")
    private List<String> coinTypeList;

    @Schema(description = "客户端账变类型")
    private String customerCoinType;

    @Schema(description = "收支类型 字典code: coin_balance_type")
    private String balanceType;

    @Schema(description="账变金额最小值")
    private BigDecimal coinAmountMin;

    @Schema(description="账变金额最大值")
    private BigDecimal coinAmountMax;

    private String timeZone;

}

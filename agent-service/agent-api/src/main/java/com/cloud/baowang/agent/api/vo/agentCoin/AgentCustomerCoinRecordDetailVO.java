package com.cloud.baowang.agent.api.vo.agentCoin;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@I18nClass
@Schema(title = "代理客户端账变明细详情返回对象")
public class AgentCustomerCoinRecordDetailVO {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "时间")
    private String coinTime;

    @Schema(description = "金额")
    private BigDecimal coinAmount;

    @Schema(description = "账变后余额")
    private BigDecimal coinToAmount;

    @Schema(description = "代理客户端类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_CUSTOMER_SHOW_TYPE)
    private String agentCustomerShowType;

    @Schema(description = "代理客户端类型名称")
    private String agentCustomerShowTypeText;

    @Schema(description = "状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_COIN_TYPE_STATUS)
    private String status;

    @Schema(description = "状态名称")
    private String statusText;

    @Schema(description = "取款账号")
    private String depositWithdrawAddress;

    @Schema(description = "转出账户")
    private String transferOutAccount;

    @Schema(description = "转入账户")
    private String transferInAccount;

    @Schema(description = "备注")
    private String remark;


    private String walletType;

    private String coinType;

    private String businessCoinType;

    private String balanceType;
}

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
@Schema(title = "代理客户端账变明细返回对象")
public class AgentCustomerCoinRecordVO {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "时间")
    private String coinTime;


    @Schema(description = "代理客户端类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_CUSTOMER_SHOW_TYPE)
    private String agentCustomerShowType;

    @Schema(description = "代理客户端类型名称")
    private String agentCustomerShowTypeText;

    @Schema(description = "账变前金额")
    private BigDecimal coinFrom;

    @Schema(description = "账变金额")
    private BigDecimal coinAmount;


    @Schema(description = "账变后金额")
    private BigDecimal coinTo;

    @Schema(description = "状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_COIN_TYPE_STATUS)
    private String status;

    @Schema(description = "状态名称")
    private String statusText;

    private String walletType;

    @Schema(description = "账变类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_COIN_TYPE)
    private String coinType;

    @Schema(description = "账变类型文本")
    private String coinTypeText;

    private String businessCoinType;

    private String balanceType;

    @Schema(description = "取款账号")
    private String depositWithdrawAddress;

    @Schema(description = "转出账户")
    private String transferOutAccount;

    @Schema(description = "转入账户")
    private String transferInAccount;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "充值提款类型CODE bank_card银行卡 electronic_wallet电子钱包 crypto_currency加密货币 ")
    private String depositWithdrawTypeCode;

}

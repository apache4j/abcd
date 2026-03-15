package com.cloud.baowang.agent.api.vo.virtualCurrency;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@I18nClass
@Schema(title = "代理虚拟币分页返回对象")
public class AgentVirtualCurrencyResVO {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "虚拟币账号地址")
    private String virtualCurrencyAddress;

    @Schema(description = "虚拟币账号地址-别名")
    private String virtualCurrencyAddressAlias;

    @Schema(description = "虚拟币种类")
    private String virtualCurrencyType;

    @Schema(description = "虚拟币协议")
    private String virtualCurrencyProtocol;

    @Schema(description = "风控层级id")
    private Long riskControlLevelId;

    @Schema(description = "风控层级")
    private String riskControlLevel;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "代理类型 1正式 2商务 3置换")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_TYPE)
    private String agentType;

    @Schema(description = "代理类型名称")
    private String agentTypeText;

    @Schema(description = "提款时间")
    private Long lastWithdrawTime;

    @Schema(description = "最近操作人")
    private String lastOperator;
}

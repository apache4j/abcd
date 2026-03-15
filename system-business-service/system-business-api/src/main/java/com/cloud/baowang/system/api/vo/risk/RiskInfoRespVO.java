package com.cloud.baowang.system.api.vo.risk;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
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
@Schema(description = "查询风险信息级接参对象")
@I18nClass
public class RiskInfoRespVO implements Serializable {
    //风控会员基本信息
    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "账号类型 1测试 2正式")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private String accountType;

    @Schema(description = "账号类型名称")
    private String accountTypeText;

    @Schema(description = "姓名")
    private String userName;

    @Schema(description = "离线天数")
    private Integer offlineDays;

    //风控代理基本信息
    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "代理类型 1正式 2测试 3合作")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_TYPE)
    private Integer agentType;

    @Schema(description = "代理类型 文本")
    private String agentTypeText;

    @Schema(description = "被风控会员总个数")
    private Long riskUserCountTotal;

    @Schema(description = "风控会员个数点击详情")
    private List<RiskUserCountVo> userCountVos;

    //风控银行卡信息
    @Schema(description = "银行卡号")
    private String bankCardNo;

    @Schema(description = "银行名称")
    private String bankName;
    @Schema(description = "银行卡提款金额")
    private BigDecimal bankWithdrawAmount;

    @Schema(description = "黑名单状态 0禁用 1启用")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.BLACK_STATUS)
    private Integer blackStatus;
    @Schema(description = "黑名单状态-Name")
    private String blackStatusText;

    @Schema(description = "绑定状态 0未绑定 1绑定中")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.BINDING_STATUS)
    private Integer bindingStatus;
    @Schema(description = "绑定状态-Name")
    private String bindingStatusText;

    @Schema(description = "绑定账号数量")
    private Integer bindingAccountTimes;

    @Schema(description = "会员提款总金额")
    private BigDecimal userWithdrawSumAmount;

    @Schema(description = "代理提款总金额")
    private BigDecimal agentWithdrawSumAmount;

    // 虚拟币
    @Schema(description = "虚拟币地址")
    private String virtualCurrencyAddress;

    @Schema(description = "虚拟币种类")
    private String virtualCurrencyType;

    @Schema(description = "虚拟币协议")
    private String cryptoProtocol;

    @Schema(description = "虚拟币提款金额")
    private BigDecimal virtualAmount;

    @Schema(description = "ip")
    private String ip;

    @Schema(description = "IP归属地")
    private String address;

    @Schema(description = "终端设备号")
    private String deviceNo;

    @Schema(description = "电子钱包地址")
    private String walletAccount;

    @Schema(description = "电子钱包名称")
    private String walletName;

    @Schema(description = "电子钱包总提款金额")
    private BigDecimal totalAmount;

    @Schema(description = "通道名称")
    private String channelName;

    @Schema(description = "风控层级")
    private String riskLevel;

    @Schema(description = "风控描述")
    private String riskDesc;

    @Schema(description = "风控类型 code 1 风险会员 2 风险代理 3 风险银行卡 4 风险虚拟币 5 风险IP 6 风险终端设备号")
    private String riskControlTypeCode;

    @Schema(description = "风控账号")
    private String riskControlAccount;

    @Schema(description = "商务账号")
    private String merchantAccount;

    @Schema(description = "商务名称")
    private String merchantName;

    @Schema(description = "总代人数")
    private Long agentCount;
}

package com.cloud.baowang.agent.api.vo.manualup;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 代理账号信息
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Accessors(chain = true)
@Schema(description = "代理账号信息")
@I18nClass
public class GetByAgentInfoVO {

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "代理姓名")
    private String agentName;

    @Schema(description = "代理层级")
    private Integer level;

    @Schema(description = "上级代理id")
    private String parentId;

    @Schema(description = "上级代理")
    private String parentName;

    @Schema(description = "账号状态 1正常 2登录锁定 3充提锁定(状态多选,用逗号分开)")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_STATUS)
    private String status;

    @Schema(description = "代理账号状态")
    private String statusText;

    @Schema(description = "代理标签id")
    private String agentLabelId;
    @Schema(description = "代理标签")
    private String agentLabel;

    @Schema(description = "账号备注")
    private String remark;

    @Schema(description = "绑定银行卡数量")
    private Integer bindingBankCardNumber;

    @Schema(description = "虚拟币账号数量")
    private Integer virtualCurrencyNumber;

    @Schema(description = "累计存款次数")
    private Long totalDepositNum;
    @Schema(description = "累计提款次数")
    private Long totalWithdrawNum;

    @Schema(description = "累计存款金额")
    private BigDecimal totalDepositAmt;

    @Schema(description = "累计提款金额")
    private BigDecimal totalWithDrawAmt;

    @Schema(description = "累计总提存款金额")
    private BigDecimal totalDiffAmt;

}

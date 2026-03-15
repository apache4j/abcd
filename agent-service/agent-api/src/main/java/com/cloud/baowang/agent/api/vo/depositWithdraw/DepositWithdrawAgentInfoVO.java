package com.cloud.baowang.agent.api.vo.depositWithdraw;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@Schema(title = "充提代理账号信息")
@I18nClass
public class DepositWithdrawAgentInfoVO {


    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "姓名")
    private String agentName;

    @Schema(description = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.USER_ACCOUNT_STATUS)
    private String accountStatus;

    private String accountStatusText;


    @Schema(description = "代理标签id")
    private String agentLabelId;
    @Schema(description = "代理标签")
    private String agentLabel;

    @Schema(description = "账号备注")
    private String acountRemark;

    @Schema(description = "绑定银行卡数量")
    private Integer bindingBankCardNumber;

    @Schema(description = "虚拟币账号数量")
    private Integer virtualCurrencyNumber;

    @Schema(description = "累计总存款金额")
    private BigDecimal totalDepositAmount;

    @Schema(description = "累计总存款次数")
    private Integer totalDepositNum;

    @Schema(description = "累计总提款金额")
    private BigDecimal totalWithdrawAmount;

    @Schema(description = "累计总提款次数")
    private Integer totalWithdrawNum;

    @Schema(description = "累计总存提款差额")
    private BigDecimal totalDepositWithdrawDifference;

}

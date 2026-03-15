package com.cloud.baowang.agent.api.vo.agentCoin;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description ="代理端-下级信息-代理转账记录返回对象")
@I18nClass
public class AgentTransferRecordResponseVO implements Serializable {

    @Schema(description ="账号")
    private String account;

    @Schema(description ="账号类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.OWNER_USER_TYPE)
    private String accountType;

    @Schema(description ="账号类型文本")
    private String accountTypeText;

    @Schema(description ="类型 转出")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DIRECTION)
    private String type;

    @Schema(description ="类型名称")
    private String typeText;

    @Schema(description ="分配时间")
    private Long distributeTime;

    @Schema(description ="货币类型")
    private String currencyCode;

    @Schema(description ="分配前额度")
    private BigDecimal distributeBeforeAmount;

    @Schema(description ="分配金额")
    private BigDecimal distributeAmount;

    @Schema(description ="分配后额度")
    private BigDecimal distributeAfterAmount;

    @Schema(description ="流水倍数")
    private BigDecimal crashFlow;

    @Schema(description ="转出钱包")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.TRANSFER_WALLET_TYPE)
    private String transferOut;

    @Schema(description ="转出钱包")
    private String transferOutText;

    @Schema(description ="转入钱包")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.TRANSFER_WALLET_TYPE)
    private String transferIn;

    @Schema(description ="转入钱包")
    private String transferInText;

    @Schema(title = "状态：1：成功；2：失败")
    @I18nField(type= I18nFieldTypeConstants.DICT,value = CommonConstant.TRANSFER_STATUS)
    private Integer status;

    @Schema(title = "状态文本")
    private String statusText;

    @Schema(description ="备注")
    private String remark;


}

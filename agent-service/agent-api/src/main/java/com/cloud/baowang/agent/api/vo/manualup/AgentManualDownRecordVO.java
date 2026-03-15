package com.cloud.baowang.agent.api.vo.manualup;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Schema(description = "代理人工扣除记录")
@I18nClass
public class AgentManualDownRecordVO implements Serializable {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "代理姓名")
    private String agentName;

    @Schema(description = "调整方式")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.MANUAL_ADJUST_WAY)
    private Integer adjustWay;

    @Schema(description = "调整方式-名称")
    private String adjustWayText;

    @Schema(description = "订单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_STATUS)
    private Integer orderStatus;

    @Schema(description = "订单状态-名称")
    private String orderStatusText;

    @Schema(description = "调整类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_MANUAL_ADJUST_DOWN_TYPE)
    private Integer adjustType;

    @Schema(description = "调整类型-名称")
    private String adjustTypeText;

    @Schema(description = "调整金额")
    private BigDecimal adjustAmount;

    public BigDecimal getAdjustAmount() {
        if (ObjectUtil.isEmpty(adjustAmount)) {
            adjustAmount = BigDecimal.valueOf(0.0);
        }
        return adjustAmount;
    }

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "申请备注")
    private String applyReason;

    @Schema(description = "申请人")
    private String applicant;

    @Schema(description = "申请时间")
    private Long applyTime;

}

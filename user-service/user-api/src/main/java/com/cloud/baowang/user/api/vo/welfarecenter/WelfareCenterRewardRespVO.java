package com.cloud.baowang.user.api.vo.welfarecenter;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "app-会员福利中心vo")
@Data
@I18nClass
public class WelfareCenterRewardRespVO {
    @Schema(description = "主键")
    private String id;
    /**
     * {@link com.cloud.baowang.user.api.enums.WelfareCenterRewardType}
     */
    @Schema(description = "订单号")
    private String orderNo;
    @Schema(description = "福利类型")
    @I18nField(value = CommonConstant.WELFARE_CENTER_REWARD_TYPE, type = I18nFieldTypeConstants.DICT)
    private Integer welfareCenterRewardType;

    @Schema(description = "福利类型")
    private String welfareCenterRewardTypeText;
    /**
     * 领取状态
     * {@link com.cloud.baowang.user.api.enums.ActivityReceiveStatusEnum}
     */
    @Schema(description = "领取状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ACTIVITY_RECEIVE_STATUS)
    private Integer receiveStatus;

    @Schema(description = "领取状态")
    private String receiveStatusText;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "奖励金额")
    private BigDecimal amount;

    @Schema(description = "具体类型")
    @I18nField
    private String detailType;

    @Schema(description = "派发时间")
    private Long pfTime;

    @Schema(description = "可领取时间-为null代表长期")
    private Long pfEndTime;

    @Schema(description = "是否长期有效 0.否,1.是 为1时 下面时间差没有值")
    private Integer isPermanentValidity;

    @Schema(description = "计算距离过期时间时间差,单位毫秒")
    private Long expiryTimeRemaining;


}

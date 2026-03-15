package com.cloud.baowang.activity.api.vo.v2;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 会员活动记录
 */
@Data
@Builder
@I18nClass
@AllArgsConstructor
@NoArgsConstructor
public class ActivityOrderRecordV2RespVO {

    @Schema(description = "订单号")
    private String id;

    @Schema(description = "订单ID")
    private String orderNo;

    @Schema(description = "活动ID")
    private String activityId;

    @Schema(description = "活动ID")
    private String activityNo;

    @I18nField
    @Schema(description = "活动名称")
    private String activityNameI18nCode;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "会员id")
    private String userId;

    @Schema(description = "活动奖励")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal activityAmount;

    @Schema(description = "币种")
    private String currencyCode;

    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ACTIVITY_RECEIVE_STATUS)
    @Schema(description = "领取状态 字典CODE：activity_receive_status")
    private Integer receiveStatus;

    private String receiveStatusText;

    @Schema(description = "活动模板")
    private String activityTemplate;

    @Schema(description = "领取时间")
    private Long receiveTime;

    @Schema(description = "发放时间")
    private Long createdTime;

}

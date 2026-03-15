package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Schema(title = "福利中心-活动礼包记录")
@I18nClass
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityOrderRecordDetailPartRespVO {

    private String id;

    @Schema(description = "活动模板")
    private String activityTemplate;

    @Schema(description = "领取状态，0:未领取,1:已领取,2:已过期")
    private Integer receiveStatus;

    @Schema(description = "活动赠送金额")
    private BigDecimal activityAmount;

    @Schema(description = "币种")
    private String currencyCode;

    @I18nField
    @Schema(description = "活动名称")
    private String activityNameI18nCode;

    @Schema(description = "发放时间")
    private Long createdTime;


}

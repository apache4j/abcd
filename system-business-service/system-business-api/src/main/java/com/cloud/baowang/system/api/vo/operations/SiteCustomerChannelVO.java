package com.cloud.baowang.system.api.vo.operations;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "客服通道列表返回对象")
@I18nClass
public class SiteCustomerChannelVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "通道代码")
    private String channelCode;

    @Schema(description = "通道名称")
    private String channelName;

    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.CUSTOMER_TYPE)
    @Schema(description = "客服类型;1-在线客服")
    private Integer customerType;

    @Schema(description = "客服类型名称")
    private String customerTypeText;

    @Schema(description = "通道地址")
    private String channelAddr;

    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    @Schema(description = "状态;1-启用,0-禁用")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusText;

    @Schema(description = "操作人")
    private String updaterName;

    @Schema(description = "操作时间")
    private Long updatedTime;
}

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

/**
 * @Author : 小智
 * @Date : 2024/7/31 11:19
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="新增站点客服授权返回分页对象")
@I18nClass
public class SiteCustomerChannelPageVO {

    @Schema(description = "通道id")
    private String id;

    @Schema(description = "通道代码")
    private String channelCode;

    @Schema(description = "通道名称")
    private String channelName;

    @Schema(description = "客服类型;1-在线客服")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.CUSTOMER_TYPE)
    private Integer customerType;

    @Schema(description = "客服类型 国际化")
    private String customerTypeText;

    @Schema(description = "通道地址")
    private String channelAddr;

    @Schema(description = "状态;1-启用,2-禁用")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    private Integer status;

    @Schema(description = "状态;1-启用,2-禁用")
    private String statusText;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "更新时间")
    private Long updatedTime;

    @Schema(description = "更新人")
    private String updaterName;

    @Schema(description = "选中状态(0:未选中,1:选中)")
    private Integer chooseFlag;
}

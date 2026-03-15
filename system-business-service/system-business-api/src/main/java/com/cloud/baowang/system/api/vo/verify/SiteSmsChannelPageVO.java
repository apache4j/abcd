package com.cloud.baowang.system.api.vo.verify;

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
 * @Date : 2024/7/31 16:27
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "新增站点短信返回分页对象")
@I18nClass
public class SiteSmsChannelPageVO {

    @Schema(description = "使用地区")
    private String address;
    @Schema(description = "使用地区code")
    private String  addressCode;
    @Schema(description = "区号")
    private String areaCode;
    @Schema(description = "通道ID")
    private String channelId;
    @Schema(description = "通道名称")
    private String channelName;
    @Schema(description = "通道代码")
    private String channelCode;
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    @Schema(description = "状态")
    private Integer status;
    @Schema(description = "状态名称")
    private String statusText;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "是否选中(0:未选中,1:选中)")
    private String chooseFlag;
    @Schema(description = "操作人")
    private String operator;
    @Schema(description = "操作时间")
    private Long operatorTime;
}

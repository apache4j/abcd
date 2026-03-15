package com.cloud.baowang.system.api.vo.verify;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: fangfei
 * @createTime: 2024/07/31 15:21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@Schema(title ="短信通道配置分页对象")
public class SmsChannelConfigPageVO extends BaseVO {
    @Schema(title = "使用地区")
    private String address;
    @Schema(title = "使用地区code")
    private String  addressCode;
    @Schema(title = "区号")
    private String areaCode;
    @Schema(title = "通道ID")
    private String channelId;
    @Schema(title = "通道名称")
    private String channelName;
    @Schema(title = "通道代码")
    private String channelCode;
    @Schema(title = "授权数量")
    private Integer authCount;
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    @Schema(title = "状态")
    private Integer status;
    @Schema(title = "状态名称")
    private String statusText;
    @Schema(title = "备注")
    private String remark;

}

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
@Schema(title ="站点后台邮箱通道返回分页对象")
@I18nClass
public class SiteBackEmailChannelPageVO extends BaseVO {
    @Schema(description = "通道ID")
    private String channelId;
    @Schema(description = "通道名称")
    private String channelName;
    @Schema(description = "通道代码")
    private String channelCode;
    @Schema(description = "通道地址")
    private String host;
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    @Schema(description = "状态")
    private Integer status;
    @Schema(description = "状态名称")
    private String statusText;
    @Schema(description = "操作人")
    private String operator;
    @Schema(description = "操作时间")
    private Long operatorTime;

}

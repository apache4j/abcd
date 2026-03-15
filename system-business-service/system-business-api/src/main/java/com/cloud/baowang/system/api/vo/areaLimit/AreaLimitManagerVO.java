package com.cloud.baowang.system.api.vo.areaLimit;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@I18nClass
@Schema(description = "区域限制分页返回vo")
public class AreaLimitManagerVO  {
    @Schema(description = "id")
    private String id;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "国家code")
    private String areaCode;
    @Schema(description = "类型 1:ip 2:国家")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AREA_LIMIT_TYPE)
    private Integer type;
    @Schema(description = "类型文本")
    private String typeText;
    @Schema(description = "生效状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SWITCH_STATUS)
    private Integer status;
    @Schema(description = "生效状态文本")
    private String statusText;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "创建人")
    private String creator;
    @Schema(description = "创建人名称")
    private String creatorName;
    @Schema(description = "创建时间")
    private Long createdTime;
    @Schema(description = "操作人")
    private String operator;
    @Schema(description = "操作人名称")
    private String operatorName;
    @Schema(description = "操作时间")
    private Long operatorTime;
}

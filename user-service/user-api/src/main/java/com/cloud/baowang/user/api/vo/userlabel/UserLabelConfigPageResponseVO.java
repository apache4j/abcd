package com.cloud.baowang.user.api.vo.userlabel;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 标签管理 分页响应VO
 */
@Data
@Schema(description = "标签管理分页 响应VO")
@I18nClass
public class UserLabelConfigPageResponseVO {
    @Schema(description = "id")
    private String id;

    @Schema(description = "标签ID")
    private String labelId;

    @Schema(description = "标签名称")
    private String labelName;

    @Schema(description = "颜色")
    private String color;

    @Schema(description = "标签描述")
    private String labelDescribe;

    @Schema(description = "标签人数")
    private String labelCount;

    @Schema(description = "状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    private Integer status;

    @Schema(description = "状态名称")
    private String statusText;

    @Schema(description = "创建人")
    private String creatorName;

    @Schema(description = "创建人ID")
    private String creator;

    @Schema(description = "创建时间")
    private long createdTime;

    @Schema(description = "最近操作人")
    private String updaterName;

    @Schema(description = "最近操作时间")
    private long updatedTime;

    @Schema(description = "标签状态 0:非定制，1定制")
    private Integer customizeStatus;
}

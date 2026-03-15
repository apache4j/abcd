package com.cloud.baowang.user.api.vo.user;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
@Schema(description = "意见反馈列表")
public class SiteUserFeedbackAppResVO implements Serializable {

    @Schema(description = "id")
    private String id;

    @Schema(description = "问题类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.FEEDBACK_QUESTION_TYPE)
    private  Integer type;

    @Schema(description = "问题类型文本")
    private  String typeText;

    @Schema(title = "申请时间")
    private Long createdTime;

    @Schema(description = "意见内容")
    private String content;

    @Schema(title = "截图")
    private String picUrls;

    @Schema(description = "是否已读 1 已读 | 0 未读")
    private Integer isRead;

    @Schema(description = "顶级id")
    private String feedId;

    @Schema(title = "更新时间")
    private Long updatedTime;

    @Schema(description = "回复时间")
    private Long backTime;

}

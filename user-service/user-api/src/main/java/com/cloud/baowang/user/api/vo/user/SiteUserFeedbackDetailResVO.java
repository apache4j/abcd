package com.cloud.baowang.user.api.vo.user;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@I18nClass
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "意见反馈详情")
public class SiteUserFeedbackDetailResVO implements Serializable {

    @Schema(title = "id")
    private String id;

    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.FEEDBACK_QUESTION_TYPE)
    @Schema(description = "问题类型")
    private  Integer type;

    @Schema(description = "问题类型")
    private  String typeText;

    @Schema(description = "相关订单号")
    private String orderId;

    @Schema(title = "申请时间")
    private Long createdTime;

    @Schema(title = "意见内容")
    private String content;

    @Schema(title = "回复内容")
    private List<BackContentText> backContent;

    @Schema(title = "最新回复内容")
    private String latestBackContent;

    @Schema(title = "回复人")
    private String backAccount;

    @Schema(title = "回复时间")
    private Long backTime;

    @Schema(title = "截图")
    private String picUrls;

    @Schema(description = "回复层级")
    private Integer sort;


    @Schema(title = "用户账号")
    private String userAccount;

    @Schema(title = "用户头像")
    private String avatarCode;

    @Schema(title = "用户头像")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String avatar;

    @Schema(title = "用户头像")
    private String avatarFileUrl;


}

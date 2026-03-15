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
@Schema(description = "意见反馈站点req")
public class SiteUserFeedbackSiteRespVO implements Serializable {

    @Schema(description = "id")
    private String id;

    @Schema(description = "用户id")
    private String userId;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "会员层级Id，多条根据','拼接")
    private String userLabel;

    /* VIP等级code */
    @Schema(description = "会员等级code")
    private Integer vipGradeCode;

    /* VIP等级名称 */
    @Schema(description = "会员等级名称")
    private String vipGradeName;

    @Schema(description = "问题类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.FEEDBACK_QUESTION_TYPE)
    private  Integer type;

    @Schema(description = "问题类型文本")
    private  String typeText;

    @Schema(description = "意见内容")
    private String content;

    @Schema(description = "相关订单号")
    private String orderId;

    @Schema(description = "回复内容")
    private String backContent;

    @Schema(description = "回复人")
    private String backAccount;

    @Schema(description = "回复时间")
    private Long backTime;

    @Schema(description = "锁单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_LOCK_STATUS)
    private Integer lockStatus ;

    @Schema(description = "锁单状态")
    private String lockStatusText ;

    @Schema(description = "锁单人")
    private String locker;

    @Schema(title = "申请时间")
    private Long createdTime;

    @Schema(description = "是否已读 1 已读 | 0 未读")
    private Integer isRead;

    @Schema(description = "图片")
    private String picUrls;

    @Schema(description = "顶级id")
    private String feedId;

    @Schema(title = "更新时间")
    private Long updatedTime;

    @Schema(description = "当前已回复 0未回复 1已回复")
    private int currentBack;




}

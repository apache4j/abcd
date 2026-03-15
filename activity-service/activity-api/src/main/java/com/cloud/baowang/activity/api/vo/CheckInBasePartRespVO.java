package com.cloud.baowang.activity.api.vo;


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
 * 活动基础信息的所有字段属性
 */
@Data
@Schema(title = "活动基础信息-签到-客户端返回活动列表")
@I18nClass
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckInBasePartRespVO {

    /**
     * id
     */
    @Schema(title = "主键id")
    private String id;
    /**
     * 站点code
     */
    /*@Schema(title = "站点code", hidden = true)
    private String siteCode;*/

    @Schema(title = "活动名称")
    @I18nField
    private String activityNameI18nCode;

    /**
     * 活动模板-同system_param activity_template
     */
    @Schema(title = "活动模板-同system_param activity_template")
    private String activityTemplate;

    /*@Schema(title = "入口图-移动端")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String entrancePictureI18nCode;

    @Schema(title = "入口图-移动端")
    private String entrancePictureI18nCodeFileUrl;*/


    /**
     * 入口图-PC端
     */
    /*@Schema(title = "入口图-PC端")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String entrancePicturePcI18nCode;*/

    /**
     * 入口图-PC端
     */
    /*@Schema(title = "入口图-PC端")
    private String entrancePicturePcI18nCodeFileUrl;*/


    /**
     * 活动头图-移动端
     */
   /* @Schema(title = "活动头图-移动端")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String headPictureI18nCode;*/

    /**
     * 活动头图-移动端
     */
   /* @Schema(title = "活动头图-移动端,完整url")
    private String headPictureI18nCodeFileUrl;*/


    /**
     * 活动头图-PC端
     */
    /*@Schema(title = "活动头图-PC端")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String headPicturePcI18nCode;*/

    /**
     * 活动头图-PC端
     */
    /*@Schema(title = "活动头图-PC端完整url")
    private String headPicturePcI18nCodeFileUrl;*/



    @Schema(description = "活动对象-0:全体会员,1:新注册会员", required = true)
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ACTIVITY_USER_TYPE)
    private Integer userType;

    @Schema(description = "活动对象-多语言名称", required = true)
    private String userTypeText;

    /**
     * 活动时效-
     * ActivityDeadLineEnum
     */
    @Schema(title = "活动时效 0-限时,1-长期")
    private Integer activityDeadline;


    /**
     * 活动开始时间
     */
    @Schema(description = "活动开始时间")
    private Long activityStartTime;

    /**
     * 活动结束时间
     */
    @Schema(description = "活动结束时间")
    private Long activityEndTime;
    /**
     * 活动图上架时间
     */
    /*private Long showStartTime;*/

    /**
     * 活动图下架时间
     */
    /*private Long showEndTime;*/
    /**
     * 活动结束时间
     */
    @Schema(description = "活动展示了,是否可进入,判断当前时间是否在活动开启")
    private Boolean enable = false;


    /**
     * 是否签到
     */
    @Schema(description = "是否签到-该会员是否今天签到")
    private Boolean checkInStatus = false;


    /**
     * 活动是否开启时间范围内
     */
    @Schema(description = "活动是否开启展示时间范围内")
    private Boolean showFlag = false;


    /**
     *
     */
    /*@Schema(description = "当前时间不在有效时间之内 1.活动尚未开始 2.活动已经结束")
    private Integer enableFlag;*/
    /**
     * 活动规则,多语言
     */
    @Schema(description = "活动规则,多语言")
    @I18nField
    private String activityRuleI18nCode;

    /**
     * 活动描述,多语言
     */
    @Schema(description = "活动描述,多语言")
    @I18nField
    private String activityDescI18nCode;

    /*@Schema(description = "活动简介-多语言")
    @I18nField
    private String activityIntroduceI18nCode;*/

    @Schema(description = "账号类型 1测试 2正式")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private String accountType;

    @Schema(description = "账号类型名称")
    private String accountTypeText;
    @Schema(description = "活动是否禁用")
    private Boolean isForbidden = true;


}
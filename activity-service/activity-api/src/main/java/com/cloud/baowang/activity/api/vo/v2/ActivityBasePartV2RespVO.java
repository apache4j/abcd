package com.cloud.baowang.activity.api.vo.v2;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
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
@Schema(title = "活动基础信息-客户端返回活动列表V2")
@I18nClass
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityBasePartV2RespVO {


    @Schema(title = "主键id")
    private String id;

    @Schema(title = "活动名称")
    @I18nField
    private String activityNameI18nCode;

    /**
     * 活动模板-同system_param activity_template
     */
    @Schema(title = "活动模板-同system_param activity_template")
    private String activityTemplate;

    @Schema(title = "入口图-移动端")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String entrancePictureI18nCode;

    @Schema(title = "入口图-移动端")
    private String entrancePictureI18nCodeFileUrl;

    @Schema(description = "入口图-移动端-黑夜-code")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String entrancePictureBlackI18nCode;

    @Schema(description = "入口图-移动端-黑夜-codeFileUrl", required = true)
    private String entrancePictureBlackI18nCodeFileUrl;


    /**
     * 入口图-PC端
     */
    @Schema(title = "入口图-PC端")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String entrancePicturePcI18nCode;

    /**
     * 入口图-PC端
     */
    @Schema(title = "入口图-PC端")
    private String entrancePicturePcI18nCodeFileUrl;

    @Schema(description = "入口图-PC端-黑夜-code", required = true)
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String entrancePicturePcBlackI18nCode;

    @Schema(description = "入口图-PC端-黑夜-codeFileUrl", required = true)
    private String entrancePicturePcBlackI18nCodeFileUrl;


    /**
     * 活动头图-移动端
     */
    @Schema(title = "活动头图-移动端")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String headPictureI18nCode;

    /**
     * 活动头图-移动端
     */
    @Schema(title = "活动头图-移动端,完整url")
    private String headPictureI18nCodeFileUrl;

    @Schema(description = "活动头图-移动端-黑夜-code", required = true)
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String headPictureBlackI18nCode;

    @Schema(description = "活动头图-移动端-黑夜-codeFileUrl", required = true)
    private String headPictureBlackI18nCodeFileUrl;


    /**
     * 活动头图-PC端
     */
    @Schema(title = "活动头图-PC端")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String headPicturePcI18nCode;

    /**
     * 活动头图-PC端
     */
    @Schema(title = "活动头图-PC端完整url")
    private String headPicturePcI18nCodeFileUrl;

    @Schema(description = "活动头图-PC端-黑夜-code", required = true)
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String headPicturePcBlackI18nCode;

    @Schema(description = "活动头图-PC端-黑夜-codeFileUrl", required = true)
    private String headPicturePcBlackI18nCodeFileUrl;

    @Schema(title = "注册成功弹窗展示图(移动)-code")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String recommendTerminalsPicI18nCode;

    @Schema(description = "注册成功弹窗展示图(移动)-code-codeFileUrl", required = true)
    private String recommendTerminalsPicI18nCodeFileUrl;

    @Schema(title = "注册成功弹窗展示图(PC)-code")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String recommendTerminalsPicPcI18nCode;

    @Schema(description = "注册成功弹窗展示图(PC)-codeFileUrl", required = true)
    private String recommendTerminalsPicPcI18nCodeFileUrl;

    @Schema(description = "未登录首页浮动图标(移动端)-code", required = true)
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String floatIconAppI18nCode;

    @Schema(description = "未登录首页浮动图标(移动端)-codeFileUrl", required = true)
    private String floatIconAppI18nCodeFileUrl;

    @Schema(description = "未登录首页浮动图标(PC端)-code", required = true)
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String floatIconPcI18nCode;

    @Schema(description = "未登录首页浮动图标(PC端)-codeFileUrl", required = true)
    private String floatIconPcI18nCodeFileUrl;

    @Schema(title = "浮标排序 越大越靠前")
    private Integer floatIconSort;

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
     * 活动结束时间还有
     */
    @Schema(description = "活动结束时间(秒)")
    private Long activityTimeRemaining;

    @Schema(description = "活动状态 0 未开启， 1 ，开启, 2，已结束")
    private int openStatus;

    /**
     * 活动图上架时间
     */
    private Long showStartTime;

    /**
     * 活动图下架时间
     */
    private Long showEndTime;
    /**
     * 活动结束时间
     */
    @Schema(description = "活动展示了,是否可进入,判断当前时间是否在活动开启")
    private Boolean enable;


    /**
     *
     */
    @Schema(description = "当前时间不在有效时间之内 1.活动尚未开始 2.活动已经结束")
    private Integer enableFlag;

    /**
     * 活动是否开启时间范围内
     */
    @Schema(description = "活动是否开启展示时间范围内")
    private Boolean showFlag ;
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

    @Schema(description = "活动简介-多语言")
    @I18nField
    private String activityIntroduceI18nCode;

    /**
     * 顺序
     */
    @Schema(title = "顺序")
    private Integer sort;

    @Schema(title = "活动分类-活动页签主键")
    private String labelId;

    @Schema(title = "是否是任务,true是任务,false是活动")
    private Boolean taskFlag = false;

    @Schema(title = "每周任务是否展示,true是展示,false是不展示 ")
    private Boolean weeklyTaskFlag = false;

    @Schema(title = "每周任务截止时间戳")
    private Long weeklyEndTime;

    @Schema(title = "每日任务是否展示,true是展示,false是不展示")
    private Boolean dailyTaskFlag = false;

    @Schema(title = "每日任务截止时间戳")
    private Long dailyTaskEndTime;

    @Schema(title = "任务是否开启卡图配置,true是开启,false是关闭")
    private Boolean flashCardTaskFlag = false;


    /**
     * 注册成功弹窗终端
     */
    @Schema(title = "注册成功弹窗终端")
    private String recommendTerminals;

    /**
     * 是否推荐活动（0.不推荐。 1. 推荐）
     */
    @Schema(title = "是否推荐活动(0.不推荐。 1. 推荐）")
    private Boolean recommended;

    /**
     * 弹窗宣传图PC
     */
    @Schema(title = "弹窗宣传图PC")
    private String picShowupPcI18nCode;

    /**
     * 弹窗宣传图APP
     */
    @Schema(title = "弹窗宣传图APP")
    private String picShowupAppI18nCode;

    @Schema(title = "活动币种类型（0.平台币，1. 法币）")
    private String platformOrFiatCurrency;


    /**
     * 弹窗宣传图PC
     */
    @Schema(title = "所有的排序")
    private int allSort;


    @Schema(description = "h5活动跳转URl")
    private String h5ActivityUrl;


}
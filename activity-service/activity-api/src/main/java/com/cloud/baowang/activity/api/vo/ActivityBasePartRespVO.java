package com.cloud.baowang.activity.api.vo;


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
@Schema(title = "活动基础信息-客户端返回活动列表")
@I18nClass
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityBasePartRespVO {

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

    @Schema(title = "入口图-移动端")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String entrancePictureI18nCode;

    @Schema(title = "入口图-移动端")
    private String entrancePictureI18nCodeFileUrl;


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


    /* *//**
     * 活动分类-活动分类主键
     *//*
    @Schema(title = "活动分类-活动页签主键")
    private String labelId;


    *//**
     * 活动生效的账户类型
     *//*
    @Schema(title = "活动生效的账户类型")
    private Integer accountType;



    *//**
     * 洗码倍率
     *//*
    @Schema(title = "洗码倍率")
    private BigDecimal washRatio;


    *//**
     * 活动展示终端
     *//*
    @Schema(title = "活动展示终端")
    private String showTerminal;



    *//**
     * 活动展示开始时间
     *//*
    @Schema(title = "活动展示开始时间")
    private Long showStartTime;

    *//**
     * 活动展示结束时间
     *//*
    @Schema(title = "活动展示结束时间")
    private Long showEndTime;

    *//**
     * 活动头图-PC端
     *//*
     *//*    @Schema(title = "活动头图-PC端")
    private List<I18nMsgFrontVO> headPicturePcI18nCodeList;*//*

     *//**
     * 顺序
     *//*
    @Schema(title = "顺序")
    private Integer sort;

    *//**
     * 状态 0已禁用 1开启中
     *//*
    @Schema(title = "状态 0已禁用 1开启中")
    private Integer status;


    *//**
     * 创建人
     *//*
    @Schema(description = "创建人")
    private String creator;

    *//**
     * 创建人
     *//*
    @Schema(description = "创建时间")
    private Long createdTime;
    *//**
     * 修改人
     *//*
    @Schema(description = "修改人")
    private String updater;

    *//**
     * 修改时间
     *//*
    @Schema(description = "修改时间")
    private Long updatedTime;



    *//**
     * 活动参与终端
     *//*
    private String supportTerminal;


    *//**
     * 完成手机号绑定才能参与: 0 - 关, 1 - 开
     *//*
    private Integer switchPhone;

    *//**
     * 完成邮箱绑定才能参与: 0 - 关, 1 - 开
     *//*
    private Integer switchEmail;

    *//**
     * 同登录IP只能1次: 0 - 关, 1 - 开
     *//*
    private Integer switchIp;*/

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




}
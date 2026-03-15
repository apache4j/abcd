package com.cloud.baowang.activity.api.vo;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 活动基础信息的所有字段属性
 */
@Data
@Schema(title = "活动基础信息")
@I18nClass
public class ActivityBaseVO implements Serializable {

    /**
     * id
     */
    @Schema(title = "主键id")
    private String id;

    private List<String> ids;
    /**
     * 站点code
     */
    @Schema(title = "站点code",hidden = true)
    private String siteCode;

    @Schema(title = "任务ID")
    private String xxlJobId;

    @Schema(title = "活动名称-多语言")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String activityNameI18nCode;

    /**
     * 活动名称-多语言
     */
    @Schema(title = "活动名称-多语言")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> activityNameI18nCodeList;

    /**
     * 活动分类-活动分类主键
     */
    @Schema(title = "活动页签-活动分类主键")
    private String labelId;

    /**
     * 活动时效-
     * ActivityDeadLineEnum
     */
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(title = "活动时效 字典CODE：activity_deadline ")
    private Integer activityDeadline;

    /**
     * 活动开始时间
     */
    @Schema(title = "活动开始时间")
    private Long activityStartTime;

    /**
     * 活动结束时间
     */
    @Schema(title = "活动结束时间")
    private Long activityEndTime;

    /**
     * 活动展示开始时间
     */
    @Schema(title = "活动展示开始时间")
    private Long showStartTime;

    /**
     * 活动展示结束时间
     */
    @Schema(title = "活动展示结束时间")
    private Long showEndTime;

    /**
     * 活动模板-同system_param activity_template
     */
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(title = "活动模板-字典CODE： activity_template")
    private String activityTemplate;

    /**
     * 活动模板-同system_param activity_template
     */
    @Schema(title = "活动模板列表")
    private List<String> activityTemplateList;

    /**
     * 洗码倍率
     */
    @Schema(title = "洗码倍率")
    //@NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal washRatio;

    /**
     * 活动生效的账户类型
     */
    @Schema(title = "活动生效的账户类型 字典CODE：agent_type")
    private Integer accountType;

    /**
     * 活动展示终端
     */
    @Schema(title = "活动展示终端")
    private String showTerminal;

    @Schema(title = "入口图-移动端")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String entrancePictureI18nCode;

    /**
     * 入口图-移动端
     */
    @Schema(title = "入口图-移动端")
    //@NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> entrancePictureI18nCodeList;


    @Schema(title = "入口图-PC端")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String entrancePicturePcI18nCode;


    /**
     * 入口图-PC端
     */
    @Schema(title = "入口图-PC端")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> entrancePicturePcI18nCodeList;



    @Schema(title = "活动头图-移动端")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String headPictureI18nCode;


    /**
     * 活动头图-移动端
     */
    @Schema(title = "活动头图-移动端")
    //@NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> headPictureI18nCodeList;


    @Schema(title = "活动头图-PC端")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String headPicturePcI18nCode;

    /**
     * 活动头图-PC端
     */
    @Schema(title = "活动头图-PC端")
    //@NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> headPicturePcI18nCodeList;

    /**
     * 顺序
     */
    @Schema(title = "顺序")
    private Integer sort;

    /**
     * 状态 0已禁用 1开启中
     */
    @Schema(title = "状态 0已禁用 1开启中")
    private Integer status;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String operator;

    @Schema(description = "参与资格: 字典CODE:activity_eligibility")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<Integer> activityEligibility;

    @Schema(description = "活动规则-多语言")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String activityRuleI18nCode;

    @Schema(description = "活动规则-多语言")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> activityRuleI18nCodeList;


    @Schema(description = "活动描述-多语言")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String activityDescI18nCode;

    @Schema(description = "活动描述-多语言")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> activityDescI18nCodeList;

    private Boolean activityDeadlineShow;

    private String activityNo;


    private List<String> activityNoList;


    @Schema(description = "活动简介-多语言")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String activityIntroduceI18nCode;

    @Schema(description = "活动简介-多语言")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> activityIntroduceI18nCodeList;

    @Schema(description = "是否展示 0 不展示，1 展示")
    private Integer showFlag = 1;

    // 申请操作:true 派发操作:false
    private boolean applyFlag= true;

    @Schema(title = "未登录首页浮动图标是否展示（0 不展示 1 展示）")
    private Boolean floatIconShowFlag;

    @Schema(title = "未登录首页浮动图标(移动端)-code")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String floatIconAppI18nCode;

    @Schema(title = "未登录首页浮动图标(移动端)-list")
    private List<I18nMsgFrontVO> floatIconAppI18nCodeList;

    @Schema(title = "未登录首页浮动图标(PC端)-code")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String floatIconPcI18nCode;

    @Schema(title = "未登录首页浮动图标(PC端)-list")
    private List<I18nMsgFrontVO> floatIconPcI18nCodeList;

    @Schema(title = "浮标排序 越大越靠前")
    private Integer floatIconSort;

}
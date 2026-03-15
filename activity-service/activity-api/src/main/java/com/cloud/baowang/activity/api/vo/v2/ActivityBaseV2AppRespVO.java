package com.cloud.baowang.activity.api.vo.v2;


import com.cloud.baowang.activity.api.enums.ActivityEligibilityEnum;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 活动基础信息的所有字段属性
 */
@Data
@Schema(title = "活动基础信息-返回活动列表")
@I18nClass
public class ActivityBaseV2AppRespVO {


    @Schema(title = "id")
    private String id;

    @Schema(title = "活动编号")
    private String activityNo;

    @Schema(title = "活动名称")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String activityNameI18nCode;

    /**
     * 活动名称-多语言
     */
    @Schema(title = "活动名称-多语言")
    private List<I18nMsgFrontVO> activityNameI18nCodeList;

    /**
     * 活动分类-活动分类主键
     */
    @Schema(title = "活动分类-活动分类主键")
    private String labelId;

    /**
     * 活动分类-活动分类主键
     */
    @I18nField
    @Schema(title = "活动分类-活动分类主键")
    private String labelName;

    /**
     * 活动模板-同system_param activity_template
     */
    @Schema(title = "活动模板-同system_param activity_template")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ACTIVITY_TEMPLATE)
    private String activityTemplate;

    @Schema(title = "活动模板-同system_param activity_template")
    private String activityTemplateText;

    /**
     * 活动生效的账户类型
     */
    @Schema(title = "活动生效的账户类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private Integer accountType;

    @Schema(description = "活动生效的账户类型")
    private String accountTypeText;

    /**
     * 活动时效-
     * ActivityDeadLineEnum
     */
    @Schema(title = "活动时效 0-限时，1-长期")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ACTIVITY_DEADLINE)
    private Integer activityDeadline;

    @Schema(title = "活动时效 0-限时，1-长期")
    private String activityDeadlineText;

    /**
     * 洗码倍率
     */
    @Schema(title = "洗码倍率")
    private BigDecimal washRatio;

    /**
     * 活动展示终端
     */
    @Schema(title = "活动展示终端")
    @I18nField(type = I18nFieldTypeConstants.DICT_CODE_TO_STR, value = CommonConstant.DEVICE_TERMINAL)
    private String showTerminal;

    @Schema(title = "活动展示终端名称")
    private String showTerminalText;

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
     * 活动头图-PC端
     */
    @Schema(title = "活动头图-PC端")
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
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    private Integer status;

    /**
     * 状态 0已禁用 1开启中
     */
    @Schema(title = "状态 0已禁用 1开启中")
    private String statusText;


    @Schema(title = "入口图-移动端")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String entrancePictureI18nCode;

    /**
     * 入口图-移动端
     */
    @Schema(description = "入口图-移动端")
    private List<I18nMsgFrontVO> entrancePictureI18nCodeList;
    /**
     * 入口图-PC端
     */
    @Schema(title = "入口图-PC端")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String entrancePicturePcI18nCode;

    /**
     * 入口图-PC端
     */
    @Schema(description = "入口图-PC端")
    private List<I18nMsgFrontVO> entrancePicturePcI18nCodeList;


    /**
     * 活动头图-移动端
     */
    @Schema(title = "活动头图-移动端")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String headPictureI18nCode;
    /**
     * 活动头图-移动端
     */
    @Schema(description = "活动头图-移动端")
    private List<I18nMsgFrontVO> headPictureI18nCodeList;

    /**
     * 活动头图-PC端
     */
    @Schema(title = "活动头图-PC端")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String headPicturePcI18nCode;


    /**
     * 创建人
     */
    @Schema(description = "创建时间")
    private Long createdTime;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private Long updatedTime;

    /**
     * 活动开始时间
     */
    private Long activityStartTime;

    /**
     * 活动结束时间
     */
    private Long activityEndTime;

    /**
     * 活动参与终端
     */
    private String supportTerminal;


    /**
     * 完成手机号绑定才能参与: 0 - 关, 1 - 开
     */
    private Integer switchPhone;

    /**
     * 完成邮箱绑定才能参与: 0 - 关, 1 - 开
     */
    private Integer switchEmail;

    /**
     * 同登录IP只能1次: 0 - 关, 1 - 开
     */
    private Integer switchIp;

    @Schema(description = "参与资格")
    List<String> activityEligibility;


    public List<String> getActivityEligibility() {
        List<String> eligibilityArray = Lists.newArrayList();
        if (this.getSwitchIp() != null && EnableStatusEnum.ENABLE.getCode().equals(this.getSwitchIp())) {
            eligibilityArray.add(ActivityEligibilityEnum.IP.getValue() + "");
        }
        if (this.getSwitchPhone() != null && EnableStatusEnum.ENABLE.getCode().equals(this.getSwitchPhone())) {
            eligibilityArray.add(ActivityEligibilityEnum.PHONE.getValue() + "");
        }
        if (this.getSwitchEmail() != null && EnableStatusEnum.ENABLE.getCode().equals(this.getSwitchEmail())) {
            eligibilityArray.add(ActivityEligibilityEnum.EMAIL.getValue() + "");
        }

        return eligibilityArray;
    }

    /**
     * 活动规则,多语言
     */
    @Schema(description = "活动规则,多语言")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String activityRuleI18nCode;

    /**
     * 活动规则,多语言
     */
    @Schema(description = "活动规则,多语言")
    private List<I18nMsgFrontVO> activityRuleI18nCodeList = new ArrayList<>();

    /**
     * 活动描述,多语言
     */
    @Schema(description = "活动描述,多语言")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String activityDescI18nCode;

    @Schema(description = "活动描述,多语言")
    private List<I18nMsgFrontVO> activityDescI18nCodeList = new ArrayList<>();

    /**
     * 活动简介,多语言
     */

    @Schema(description = "活动简介-多语言")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String activityIntroduceI18nCode;

    @Schema(description = "活动简介-多语言")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> activityIntroduceI18nCodeList = new ArrayList<>();

    @Schema(description = "是否展示 0 不展示，1 展示")
    private Integer showFlag = 1;

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
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String picShowupPcI18nCode;

    private List<I18nMsgFrontVO> picShowupPcI18nCodeList = new ArrayList<>();

    /**
     * 弹窗宣传图APP
     */
    @Schema(title = "弹窗宣传图APP")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String picShowupAppI18nCode;


    private List<I18nMsgFrontVO> picShowupAppI18nCodeList = new ArrayList<>();

    @Schema(description = "h5活动跳转URl")
    private String h5ActivityUrl;

}
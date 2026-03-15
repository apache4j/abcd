package com.cloud.baowang.user.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@I18nClass
@Schema(title = "未登录推荐活动返回实体")
public class ActivityRegisterRecommendResVO {


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

    @Schema(title = "活动名称")
    @I18nField
    private String activityNameI18nCode;

    @Schema(title = "主键id")
    private String id;

    @Schema(title = "活动模板")
    private String activityTemplate;

    /**
     * 活动描述,多语言
     */
    @Schema(description = "活动描述,多语言")
    @I18nField
    private String activityDescI18nCode;

    @Schema(description = "活动简介-多语言")
    @I18nField
    private String activityIntroduceI18nCode;

    @Schema(description = "h5活动跳转URl")
    private String h5ActivityUrl;

    //排序用
    private int sort;

}

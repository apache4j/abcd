package com.cloud.baowang.system.api.vo.site.tutorial.content;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.system.api.api.i18n.dto.I18NMessageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@Schema(title = "教程内容配置")
public class TutorialContentRspVO {
    private String id;
    private String siteCode;

    private String siteName;
    @Schema(description = "内容名称-中文")
    @I18nField
    private String nameCn;


    @Schema(description = "富文本内容-中文")
    @I18nField
    private String contentDetailCn;

    @Schema(description = "状态 0:禁用 1:启用")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_TYPE)
    private Integer status;


    @Schema(description = "状态多语言")
    private String statusText;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "最近操作人")
    private String operator;

    @Schema(description = "创建时间")
    private Long createTime;

    @Schema(description = "最近操作时间")
    private Long updateTime;

    @Schema(description = "教程大类名称(分类上级)")
    @I18nField
    private String categoryName;

    @Schema(description = "教程大类id")
    private String categoryId;

    @Schema(description = "教程分类名称(页签上级)")
    @I18nField
    private String className;

    @Schema(description = "教程分类id")
    private String classId;

    @Schema(description = "页签名称")
    @I18nField
    private String tabsName;
    @Schema(description = "页签id")
    private String tabsId;

    @Schema(title = "多语言集合 ")
    private List<I18NMessageDTO> i18nMessages;

    @Schema(title = "手动排序字段 ")
    private Integer sort;
}

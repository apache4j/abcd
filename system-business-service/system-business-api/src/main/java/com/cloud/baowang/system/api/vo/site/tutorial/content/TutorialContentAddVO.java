package com.cloud.baowang.system.api.vo.site.tutorial.content;

import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "新增教程内容配置vo")
public class TutorialContentAddVO {
    private String id;

    private String siteCode;

    @Schema(title = "多语言集合 只需要language和message;language取language_type中的code;格式:zh-CN")
    private List<I18nMsgFrontVO> i18nMessages;

    @Schema(title = "教程大类id(上级)")
    private Long categoryId;


    @Schema(title ="教程大类名称-传下拉框的type")
    private String categoryName;

    @Schema(title = "教程分类id")
    private Long classId;

    @Schema(title = "教程分类名称-传下拉框的type")
    private Long className;

    @Schema(title = "页签id")
    private Long tabsId;

    @Schema(title = "页签名称-传下拉框的type")
    private Long tabsName;

}

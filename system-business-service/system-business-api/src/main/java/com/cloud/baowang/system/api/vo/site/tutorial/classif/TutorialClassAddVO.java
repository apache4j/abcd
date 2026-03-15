package com.cloud.baowang.system.api.vo.site.tutorial.classif;

import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
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
@Schema(title ="新增教程大类配置vo")
public class TutorialClassAddVO {
    private String id;

    private String siteCode;

    @Schema(title = "教程大类图片")
    private String imgKey;
    @Schema(title = "多语言集合 只需要language和message;language取language_type中的code;格式:zh-CN")
    private List<I18nMsgFrontVO> i18nMessages;

    @Schema(title ="教程大类id(上级)")
    private String categoryId;

    @Schema(title ="教程大类名称-传下拉框的type")
    private String categoryName;


}

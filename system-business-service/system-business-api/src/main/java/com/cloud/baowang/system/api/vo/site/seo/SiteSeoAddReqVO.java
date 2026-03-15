package com.cloud.baowang.system.api.vo.site.seo;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
public class SiteSeoAddReqVO {

    private String siteCode;
    /**
     *  标题
     */
    @Schema(title = "标题")
    @NotNull(message = ConstantsCode.PARAM_MISSING)
    private String title;
    /**
     *  网站摘要
     */
    @Schema(title = "网站摘要")
    @NotNull(message = ConstantsCode.PARAM_MISSING)
    private String meta;

    /**
     *  语言
     */
    @Schema(title = "语言")
    @NotNull(message = ConstantsCode.PARAM_MISSING)
    private String lang;


    /**
     *  更新时间
     */
    @Schema(title = "最近操作时间")
    private Long updatedTime;

    /**
     *
     */
    @Schema(title = "最近操作人")
    private String updater;

    private String creator;
    private Long createdTime;
}

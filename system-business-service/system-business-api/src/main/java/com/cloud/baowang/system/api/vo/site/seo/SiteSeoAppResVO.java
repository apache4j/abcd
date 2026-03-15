package com.cloud.baowang.system.api.vo.site.seo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteSeoAppResVO {

    /**
     *  标题
     */
    @Schema(title = "标题")
    private String title;
    /**
     *  网站摘要
     */
    @Schema(title = "网站摘要")
    private String meta;

    /**
     *  语言
     */
    @Schema(title = "语言")
    private String lang;

}

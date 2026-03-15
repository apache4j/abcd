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
public class SiteSeoResVO {


    /**
     *  主键id
     */
    @Schema(title = "id")
    private String id;

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

    @Schema(title = "语言名称")
    private String langName;


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
}

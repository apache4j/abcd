package com.cloud.baowang.system.api.vo.site.agreement;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "媒体号配置")
public class MediaInfo {

    private String id;

    private String imgUrl;

    private String imgFullUrl;

    private String imgLink;

//    @Schema(hidden = true)
//    private String siteCode;
//
//    @Schema(hidden = true)
//    private Long updatedTime;
//    @Schema(hidden = true)
//    private String updater;
}

package com.cloud.baowang.system.api.vo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/8/21 17:42
 * @Version: V1.0
 **/
@Data
@Schema(description = "文件导出响应")
public class FileExportRespVO {
    @Schema(description = "主键ID")
    private String id;
    @Schema(description = "创建人")
    private String creator;
    @Schema(description = "创建时间")
    private Long createdTime;
    @Schema(description = "修改人")
    private String updater;
    @Schema(description = "修改时间")
    private Long updatedTime;

    @Schema(description = "页面名称")
    private String pageName;

    @Schema(description = "MINIO返回的fileKey")
    private String fileKey;

    @Schema(description = "MINIO返回的fileKey全路径")
    private String fileKeyUrl;

    @Schema(description = "导出的文件名")
    private String fileName;

    @Schema(description = "站点编码")
    private String siteCode;
}

package com.cloud.baowang.system.api.vo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 新增 文件导出数据
 *
 * @author kimi
 * @since 2024-07-02 10:00:00
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "新增 文件导出数据")
public class AddFileExportVO {

    @Schema(description = "页面名称")
    private String pageName;

    @Schema(description = "MINIO返回的fileKey")
    private String fileKey;

    @Schema(description = "导出的文件名")
    private String fileName;

    @Schema(description = "站点编码")
    private String siteCode;

    @Schema(description = "操作人id")
    private String adminId;
}

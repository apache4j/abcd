package com.cloud.baowang.system.api.vo.param;

import com.cloud.baowang.common.core.vo.base.SitePageVO;
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
@Schema(title = "文件导出分页查询条件")
public class FileExportPageVO extends SitePageVO {

    @Schema(description = "页面名称")
    private String pageName;

    @Schema(description = "MINIO返回的fileKey")
    private String fileKey;

    @Schema(description = "导出的文件名")
    private String fileName;

    @Schema(description = "操作人id",hidden = true)
    private String adminId;

}

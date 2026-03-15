package com.cloud.baowang.system.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 文件导出表
 *
 * @author kimi
 * @since 2024-07-02 10:00:00
 */
@Data
@Accessors(chain = true)
@TableName("file_export")
@Schema(description = "文件导出表")
public class FileExportPO extends SiteBasePO implements Serializable {

    @Schema(description = "页面名称")
    private String pageName;

    @Schema(description = "MINIO返回的fileKey")
    private String fileKey;

    @Schema(description = "导出的文件名")
    private String fileName;

}

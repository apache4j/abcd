package com.cloud.baowang.system.api.vo.version;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "版本管理分页查询VO")
public class SystemVersionManagerPageQueryVO extends PageVO {

    @Schema(description = "站点编码")
    private String siteCode;

    /**
     * {@link com.cloud.baowang.system.api.enums.versions.VersionMobilePlatform}
     */
    @Schema(description = "平台类型")
    private Integer deviceTerminal;

    /**
     * {@link com.cloud.baowang.system.api.enums.versions.VersionUpdateStatus}
     */
    @Schema(description = "更新状态（使用 VersionUpdateStatus 枚举）")
    private Integer versionUpdateStatus;


    @Schema(description = "创建人", hidden = true)
    private String creator;


}

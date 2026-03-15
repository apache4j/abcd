package com.cloud.baowang.system.api.vo.version;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "版本管理请求入参vo")
public class SystemVersionManagerReqVO {
    @Schema(description = "主键，唯一标识每条记录")
    private String id;

    @Schema(description = "站点编码")
    private String siteCode;

    @Schema(description = "站点名称")
    private String siteName;
    /**
     * {@link com.cloud.baowang.system.api.enums.versions.VersionMobilePlatform}
     */
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(description = "平台类型")
    private Integer deviceTerminal;

    @Schema(description = "版本名称")
    private String versionName;

    @Schema(description = "版本代码")
    @NotBlank(message = ConstantsCode.PARAM_ERROR)
    private String versionCode;

    @Schema(description = "版本号")
    @NotBlank(message = ConstantsCode.PARAM_ERROR)
    private String versionNumber;

    @Schema(description = "文件地址")
    @NotBlank(message = ConstantsCode.PARAM_ERROR)
    private String fileUrl;

    @Schema(description = "文件大小（字节）")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Long fileSize;

    @Schema(description = "更新状态（使用 VersionUpdateStatus 枚举）")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer versionUpdateStatus;

    @Schema(description = "更新描述多语言数组")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> updateDescription;

    @Schema(description = "创建人", hidden = true)
    private String creator;

    @Schema(description = "修改人", hidden = true)
    private String updater;

    @Schema(description = "创建时间（以时间戳形式表示）", hidden = true)
    private Long createdTime;

    @Schema(description = "更新时间（以时间戳形式表示）", hidden = true)
    private Long updatedTime;
}

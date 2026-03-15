package com.cloud.baowang.system.api.vo.version;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "版本管理响应vo")
@I18nClass
public class SystemVersionManagerRespVO {
    @Schema(description = "主键，唯一标识每条记录")
    private String id;

    @Schema(description = "站点编码")
    private String siteCode;

    @Schema(description = "站点名称")
    private String siteName;

    @Schema(description = "plist文件地址-如果是ios,则返回")
    private String plistUrl;

    /**
     * {@link com.cloud.baowang.system.api.enums.versions.VersionMobilePlatform}
     */
    @Schema(description = "平台类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VERSION_MOBILE_PLATFORM)
    private Integer deviceTerminal;

    @Schema(description = "平台类型")
    private String deviceTerminalText;

    @Schema(description = "版本名称")
    private String versionName;

    @Schema(description = "版本代码")
    private String versionCode;

    @Schema(description = "版本号")
    private String versionNumber;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "文件地址")
    private String fileUrl;

    @Schema(description = "文件下载地址-展示用")
    private String fileShowUrl;

    @Schema(description = "更新状态 0.最新版本,1.提示升级,2.强制升级")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VERSION_UPDATE_STATUS)
    private Integer versionUpdateStatus;

    @Schema(description = "更新状态")
    private String versionUpdateStatusText;

    @Schema(description = "更新描述")
    @I18nField
    private String updateDescription;

    @Schema(description = "更新描述多语言数组-展示用")
    private List<I18nMsgFrontVO> updateDescriptionArr;

    @Schema(description = "创建人", hidden = true)
    private String creator;

    @Schema(description = "修改人", hidden = true)
    private String updater;

    @Schema(description = "创建时间（以时间戳形式表示）", hidden = true)
    private Long createdTime;

    @Schema(description = "更新时间（以时间戳形式表示）", hidden = true)
    private Long updatedTime;
}

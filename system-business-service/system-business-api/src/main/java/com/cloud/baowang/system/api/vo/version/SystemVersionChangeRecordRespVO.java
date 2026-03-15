package com.cloud.baowang.system.api.vo.version;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "变更记录vo")
@I18nClass
public class SystemVersionChangeRecordRespVO {

    @Schema(description = "主键，唯一标识每条记录")
    private String id;

    @Schema(description = "站点编码，标识所属站点")
    private String siteCode;

    @Schema(description = "站点名称")
    private String siteName;

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

    @Schema(description = "版本更新状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VERSION_UPDATE_STATUS)
    private Integer versionUpdateStatus;

    @Schema(description = "版本更新状态")
    private String versionUpdateStatusText;

    @Schema(description = "变更前")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VERSION_UPDATE_STATUS)
    private Integer changeBefore;

    @Schema(description = "变更前更新状态")
    private String changeBeforeText;

    @Schema(description = "变更后")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VERSION_UPDATE_STATUS)
    private Integer changeAfter;

    @Schema(description = "变更后更新状态")
    private String changeAfterText;

    @Schema(description = "创建人")
    private String creator;
    @Schema(description = "创建时间")
    private Long createdTime;
    @Schema(description = "操作人")
    private String updater;
    @Schema(description = "操作时间")
    private Long updatedTime;

    @Schema(description = "变更前下载地址")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String beforeFile;

    @Schema(description = "变更前完整下载地址-展示用")
    private String beforeFileFileUrl;

    @Schema(description = "变更后下载地址")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String afterFile;

    @Schema(description = "变更后完整下载地址-展示用")
    private String afterFileFileUrl;
}

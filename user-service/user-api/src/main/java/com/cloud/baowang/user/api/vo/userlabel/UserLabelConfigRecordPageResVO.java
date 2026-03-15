package com.cloud.baowang.user.api.vo.userlabel;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * @author 阿虹
 */
@I18nClass
@Data
@Schema(description = "标签配置记录分页 返回")
public class UserLabelConfigRecordPageResVO {
    @Schema(description = "操作时间结束")
    private Long updatedTime;

    @Schema(description = "标签名称")
    private String labelName;

    @Schema(description = "变更类型")
    private String changeType;

    @I18nField
    @Schema(description = "变更类型名称")
    private String changeTypeName;

    @Schema(description = "变更前")
    private String beforeChange;

    @Schema(description = "变更后")
    private String afterChange;

    @Schema(description = "操作人")
    private String updaterName;
}

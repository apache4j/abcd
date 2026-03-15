package com.cloud.baowang.user.api.vo.userlabel;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author 阿虹
 */
@Data
@Schema(description ="标签配置记录分页 Request")
public class UserLabelConfigRecordPageReqVO extends PageVO {
    @Schema(description = "操作时间-开始")
    private Long updateTimeStart;

    @Schema(description = "操作时间-结束")
    private Long updateTimeEnd;

    @Schema(description = "变更类型")
    private String changeType;

    @Schema(description = "操作人")
    private String updaterName;

    @Schema(description = "操作人id_前端忽略")
    private String updater;
}

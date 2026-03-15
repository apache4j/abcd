package com.cloud.baowang.user.api.vo.medal;

import com.cloud.baowang.common.core.vo.base.SitePageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/8/20 10:57
 * @Version: V1.0
 **/
@Data
@Schema(description = "勋章变更记录查询条件")
public class SiteMedalOperLogReqVO extends SitePageVO {

    @Schema(description = "开始时间")
    private Long operBeginTime;

    @Schema(description = "结束时间")
    private Long operEndTime;

    @Schema(description = "操作项")
    private String operItem;

    @Schema(description = "操作人")
    private String operUserNo;

}

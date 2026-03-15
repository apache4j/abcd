package com.cloud.baowang.report.api.vo;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Schema(title = "平台统计分页列表VO")
public class PlatformVenueRequestVO extends PageVO {

    @NotNull(message = "userAccount can not be empty")
    @Schema(description ="会员账号")
    private String userAccount;

    @Schema(description ="站点编号")
    private String siteCode;


    @Schema(description = "开始时间")
    private Long startTime;

    @Schema(description = "结束时间")
    private Long endTime;
}

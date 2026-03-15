package com.cloud.baowang.agent.api.vo.site;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "代理用户数据概览请求入参")
@Builder
public class AgentDataOverviewResVo implements Serializable {
    @Schema(title = "开始时间(时间戳)")
    @NotNull(message = "开始时间不能为空")
    private String startTime;

    @Schema(title = "结束时间(时间戳)")
    @NotNull(message = "结束时间不能为空")
    private String endTime;
    @Schema(description = "站点code", hidden = true)
    private String siteCode;
}

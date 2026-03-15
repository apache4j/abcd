package com.cloud.baowang.agent.api.vo.agentinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: kimi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "更新流量代理的白名单 Request")
public class UpdateWhitelistVO {

    @Schema(title = "代理id")
    @NotNull(message = "代理id不能为空")
    private Long id;

    @Schema(title = "IP白名单")
    @NotEmpty(message = "IP白名单不能为空")
    private String agentWhiteList;
}

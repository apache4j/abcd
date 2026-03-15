package com.cloud.baowang.agent.api.vo.agent.clienthome;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: kimi
 */
@Data
@Schema(description = "查询快捷入口 Param")
public class SelectQuickEntryParam {

    @Schema(description = "终端类型 1:PC端 2:H5端")
    @NotNull(message = "终端类型不能为空")
    private Integer pcOrH5;
    @Schema(description = "当前代理id",hidden = true)
    private String currentId;
    @Schema(description = "当前代理账号",hidden = true)
    private String currentAgent;
    @Schema(description = "站点编号",hidden = true)
    private String siteCode;
}

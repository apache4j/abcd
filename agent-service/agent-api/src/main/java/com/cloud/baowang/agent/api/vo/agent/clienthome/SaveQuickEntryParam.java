package com.cloud.baowang.agent.api.vo.agent.clienthome;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * @author: kimi
 */
@Data
@Schema(description = "编辑保存快捷入口 Param")
public class SaveQuickEntryParam {

    @Schema(description = "首页功能")
    @NotNull(message = "首页功能不能为空")
    @Size(min = 1, message = "首页功能不能为空")
    private List<SelectQuickEntryVO> quickEntry;

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

package com.cloud.baowang.agent.api.vo.agent.clienthome;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author: kimi
 */
@Data
@Schema(description = "首页快捷入口 Response")
public class SelectQuickEntryResponse {

    @Schema(description = "首页功能")
    private List<SelectQuickEntryVO> quickEntry;

    @Schema(description = "全部功能")
    private List<SelectQuickEntryVO> allEntry;
}

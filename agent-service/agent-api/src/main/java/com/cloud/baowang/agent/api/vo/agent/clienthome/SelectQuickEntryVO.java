package com.cloud.baowang.agent.api.vo.agent.clienthome;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: kimi
 */
@Data
@Schema(description = "首页快捷入口 VO")
public class SelectQuickEntryVO {

    @Schema(description = "快捷菜单code")
    private Integer code;

    @Schema(description = "快捷菜单name")
    private String name;
}

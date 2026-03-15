package com.cloud.baowang.user.api.vo.agent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: ford
 */
@Data
@Schema(description = "查询快捷入口 Param")
public class SiteSelectQuickEntryParam {
    @Schema(description = "当前代理id",hidden = true)
    private String adminId;
    @Schema(description = "站点编号",hidden = true)
    private String siteCode;
}

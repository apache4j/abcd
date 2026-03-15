package com.cloud.baowang.system.api.vo.business;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author: ford
 */
@Data
@Schema(description = "站点首页快捷入口 Response")
public class SiteSelectQuickEntryResponse {

    @Schema(description = "首页功能")
    private List<BusinessUserMenuRespVO> quickEntry;

    @Schema(description = "全部功能")
    private List<BusinessUserMenuRespVO> allEntry;
}

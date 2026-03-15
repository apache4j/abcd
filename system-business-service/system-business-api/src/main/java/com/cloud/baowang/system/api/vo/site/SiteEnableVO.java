package com.cloud.baowang.system.api.vo.site;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : 小智
 * @Date : 2024/8/3 10:51
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "站点禁用/启用对象")
public class SiteEnableVO {

    @Schema(description = "站点code")
    private String siteCode;

    @Schema(description = "0:禁用,1:启用,2.维护中")
    private Integer status;

    @Schema(description = "维护时间-开始时间")
    private Long maintenanceTimeStart;

    @Schema(description = "维护时间-结束时间")
    private Long maintenanceTimeEnd;

    @Schema(description = "操作人",hidden = true)
    private String operator;

    @Schema(description = "操作时间",hidden = true)
    private Long operatorTime;
}

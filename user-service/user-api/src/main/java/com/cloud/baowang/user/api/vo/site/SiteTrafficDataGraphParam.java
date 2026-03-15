package com.cloud.baowang.user.api.vo.site;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * @author: wade
 */
@Data
@Schema(description = "流量数据 曲线图 Param")
public class SiteTrafficDataGraphParam {



    /**
     * {@link com.cloud.baowang.user.enums.SiteDataCompareGraphEnum}
     */
    @Schema(title = "数据类型  1-PV 2-UV 3-IP")
    @NotNull(message = "数据类型  1-PV 2-UV 3-IP ")
    private Integer dataType = 1;

    @Schema(title = "站点", hidden = true)
    private String siteCode;

    @Schema(title = "时区", hidden = true)
    private String timeZone;
}

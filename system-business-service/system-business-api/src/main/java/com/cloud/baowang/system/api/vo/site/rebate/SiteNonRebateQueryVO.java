package com.cloud.baowang.system.api.vo.site.rebate;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@Schema(description = "不返水配置查询vo")
@Builder
public class SiteNonRebateQueryVO extends PageVO implements Serializable {

    private String siteCode;

    @Schema(description = "场馆类型 下拉框返回值中value")
    @NotNull(message = "场馆类型type不能为空")
    private String venueType;

    @Schema(description = "场馆名称 - 下拉框返回值中venueCode")
    @NotNull(message = "场馆名称code不能为空")
    private String venueCode;

    @Schema(description = "游戏名称")
    private String gameName;

    @Schema(description = "操作人")
    private String updater;

}

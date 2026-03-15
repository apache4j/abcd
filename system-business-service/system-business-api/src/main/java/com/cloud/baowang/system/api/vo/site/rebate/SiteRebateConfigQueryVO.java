package com.cloud.baowang.system.api.vo.site.rebate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;


@Data
@Schema(description = "返水配置查询vo")
@Builder
public class SiteRebateConfigQueryVO implements Serializable {

    @Schema(hidden = true)
    private String siteCode;

    @NotNull(message = "币种不能为空")
    private String currencyCode;

}

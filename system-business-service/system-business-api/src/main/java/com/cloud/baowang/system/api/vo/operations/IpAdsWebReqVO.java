package com.cloud.baowang.system.api.vo.operations;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "IP归属币种请求类")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IpAdsWebReqVO   {

    @Schema(description = "siteCode")
    private String siteCode;
    @Schema(description = "ip")
    private String ip;
    @Schema(description = "包含国家")
    private String areaCode;
    @Schema(description = "city")
    private String city;
}

package com.cloud.baowang.system.api.vo.operations;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "IP归属币种请求类")
@Data
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
public class IpAdsWebResVO{

    @Schema(description = "用户ip")
    private String ip;
    @Schema(description = "用户国家")
    private String areaCode;
    @Schema(description = "用户国家")
    private String city;
    @Schema(description = "用户默认币种")
    private String currencyCode;
    @Schema(description = "包含国家")
    private List<SiteCurrencyRespVO> currencys;
}

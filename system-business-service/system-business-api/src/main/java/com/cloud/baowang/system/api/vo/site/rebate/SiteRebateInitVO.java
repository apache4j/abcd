package com.cloud.baowang.system.api.vo.site.rebate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;


@Data
@Schema(description = "不返水配置查询vo")
@Builder
public class SiteRebateInitVO  implements Serializable {

    @NotNull(message = "站点代码不能为空")
    private String siteCode;

    @Schema(hidden = true)
    private Integer capMode;

    private Integer vipGradeCode;

    @Schema(description = "1-开启, 0-关闭")
    @NotNull(message = "返水状态不能为空")
    private Integer status;

    @NotNull(message = "币种代码不能为空")
    private String currencyCode;

}

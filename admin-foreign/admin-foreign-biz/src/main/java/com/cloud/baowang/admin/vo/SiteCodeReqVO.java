package com.cloud.baowang.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/8/14 18:27
 * @Version: V1.0
 **/
@Data
@Schema(description = "站点币种请求参数")
public class SiteCodeReqVO {

    @Schema(description = "站点")
    @NotNull(message = "站点代码不能为空")
    private String siteCode;
}

package com.cloud.baowang.agent.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/8/15 15:21
 * @Version: V1.0
 **/
@Data
@Schema(description = "基础信息")
public class BaseReqVO {
    @Schema(description = "管理员登录ID",hidden = true)
   // @NotNull(message = "管理员登录ID不能为空")
    private String adminId;
    @Schema(description = "管理员名称",hidden = true)
   // @NotNull(message = "管理员名称不能为空")
    private String adminName;
    @Schema(description = "站点代码",hidden = true)
   // @NotNull(message = "站点代码不能为空")
    private String siteCode;
}

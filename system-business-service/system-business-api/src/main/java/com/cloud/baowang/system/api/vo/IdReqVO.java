package com.cloud.baowang.system.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/8/22 13:37
 * @Version: V1.0
 **/
@Data
@Schema(description = "主键ID查询条件")
public class IdReqVO {
    @Schema(description = "主键ID")
    @NotNull(message = "id不能为空")
    private String id;
}

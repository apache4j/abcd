package com.cloud.baowang.user.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/8/29 18:39
 * @Version: V1.0
 **/
@Data
@Schema(description = "主键ID请求参数")
public class IdReqVO {
    @Schema(description = "主键ID")
    private String id;
}

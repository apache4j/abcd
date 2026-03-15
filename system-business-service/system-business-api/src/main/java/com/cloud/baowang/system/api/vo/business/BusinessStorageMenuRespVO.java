package com.cloud.baowang.system.api.vo.business;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "常用功能VO")
public class BusinessStorageMenuRespVO implements Serializable {
    @Schema(description = "用于排序")
    private Integer code;

    @Schema(description = "路径")
    private String path;

    @Schema(description = "菜单名")
    private String name;


}

package com.cloud.baowang.common.core.vo.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


@Data
@Schema(title = "公共VO")
public class BaseVO implements Serializable {

    @Schema(title = "Id")
    private String id;

    @Schema(title = "creator")
    private String creator;

    @Schema(title = "createdTime")
    private Long createdTime;

    @Schema(title = "updater")
    private String updater;

    @Schema(title = "updatedTime")
    private Long updatedTime;
}

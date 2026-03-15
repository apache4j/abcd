package com.cloud.baowang.system.api.vo.verify;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 通道状态改变请求对象
 *
 */
@Data
@Schema(description = "通道状态改变请求对象")
public class ChannelStatusVO {

    @Schema(description = "id")
    @NotNull(message = "id不能为空")
    private String id;

    @Schema(description = "状态")
    @NotNull(message = "状态不能为空")
    private String status;

    @Schema(description = "更新人", hidden = true)
    private String updater;

}

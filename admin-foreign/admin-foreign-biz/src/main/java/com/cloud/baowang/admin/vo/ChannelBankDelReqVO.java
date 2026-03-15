package com.cloud.baowang.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 *  通道编码删除req
 **/
@Data
@Schema(description = "币种请求参数")
public class ChannelBankDelReqVO {

    @Schema(description = "id")
    @NotNull(message = "id不能为空")
    private String id;
}

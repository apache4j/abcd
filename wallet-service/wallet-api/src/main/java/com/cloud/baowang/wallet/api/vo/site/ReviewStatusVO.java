package com.cloud.baowang.wallet.api.vo.site;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 启用禁用
 *
 * @author kimi
 */
@Data
@Schema(description = "启用禁用")
public class ReviewStatusVO {

    @Schema(description = "id")
    @NotNull(message = "id不能为空")
    private String id;

    @Schema(description = "状态 0,解锁 1加锁")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "操作人账号")
    private String operatorName;
}

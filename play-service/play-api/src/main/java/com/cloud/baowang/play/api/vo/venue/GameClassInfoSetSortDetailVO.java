package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author qiqi
 */
@Data
@Schema(description = "排序请求对象")
public class GameClassInfoSetSortDetailVO {

    @Schema(description = "ID", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String id;

    @Schema(description = "顺序", required = true)
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer sort;



}

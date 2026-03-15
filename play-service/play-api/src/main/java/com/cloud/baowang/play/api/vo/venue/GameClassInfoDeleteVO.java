package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @Author: sheldon
 * @Date: 3/26/24 2:19 下午
 */
@Data
@Schema(description = "一级分类删除")
public class GameClassInfoDeleteVO {

    @Schema(description = "id", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String id;


}

package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiqi
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "游戏信息添加或修改对象")
public class GameInfoAddOrUpdateRequest {

    @Schema(description = "ID", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String id;

    @Schema(description = "标签 字典code:game_label", required = true)
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer label;

    @Schema(description = "角标 字典code:corner_labels", required = true)
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer cornerLabels;


}

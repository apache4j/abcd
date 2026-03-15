package com.cloud.baowang.play.api.vo.lobby;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sheldon
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "收藏游戏对象")
public class LobbyGameCollectionRequestVO {

    @Schema(description = "gameId", required = true)
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String gameId;

    @Schema(description = "true:收藏,false:取消收藏", required = true)
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Boolean type;

}

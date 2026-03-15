package com.cloud.baowang.play.api.vo.lobby;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: sheldon
 * @Date: 4/2/24 10:28 上午
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "查询游戏详情数据")
public class LobbyGameInfoByCodeRequestVO {

    @Schema(description = "游戏CODE", required = true)
    private String gameId;

    @Schema(description = "场馆CODE", required = true)
    private String venueCode;


}

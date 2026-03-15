package com.cloud.baowang.play.api.vo.lobby;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@Schema(title = "皮肤4-游戏大厅热门游戏")
public class Skin4LobbyGameByTwoListVO implements Serializable {

    @Schema(description = "游戏列表")
    private Page<Skin4LobbyGameInfoVO> gamePage;

    @Schema(description = "所有游戏数量")
    private Long allGameTotal;

    @Schema(description = "热门游戏数量")
    private Long hotGameTotal;

    @Schema(description = "最新游戏数量")
    private Long newGameTotal;

    @Schema(description = "收藏游戏数量")
    private Long collectGamesTotal;

}

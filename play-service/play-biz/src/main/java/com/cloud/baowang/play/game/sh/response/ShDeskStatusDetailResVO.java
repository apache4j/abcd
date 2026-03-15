package com.cloud.baowang.play.game.sh.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ShDeskStatusDetailResVO {

    @Schema(description = "桌台名称")
    private String deskName;
    @Schema(description = "游戏类型id")
    private Long gameCategoryId;
    @Schema(description = "游戏名称")
    private String gameName;
    @Schema(description = "状态 1开启 2禁用 3维护")
    private Integer status;
    @Schema(description = "桌台编码")
    private String deskNumber;
}

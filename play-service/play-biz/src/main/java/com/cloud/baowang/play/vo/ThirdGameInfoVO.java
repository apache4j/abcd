package com.cloud.baowang.play.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


@Data
@Schema(title = "三方游戏VO")
public class ThirdGameInfoVO {

    @Schema(title = "游戏编码")
    private String gameCode;

    @Schema(title = "游戏编码")
    private List<GameNameVO> gameName;




}

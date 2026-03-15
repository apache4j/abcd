package com.cloud.baowang.play.api.vo.venue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "查询游戏开启状态vo")
public class GameInfoValidRequestVO implements Serializable {

    @Schema(description = "站点")
    private String siteCode;

    @Schema(description = "游戏ID")
    private String gameId;


    @Schema(description = "场馆")
    private String venueCode;


}
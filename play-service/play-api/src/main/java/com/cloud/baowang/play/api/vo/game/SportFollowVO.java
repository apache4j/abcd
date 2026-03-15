package com.cloud.baowang.play.api.vo.game;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SportFollowVO {

    @Schema(title = "盘口类型:1:冠军，2:赛事")
    private String type;

    @Schema(title = "三方ID")
    private String thirdId;

    @Schema(title = "球类")
    private Integer sportType;

    @Schema(title = "id")
    private String id;


}

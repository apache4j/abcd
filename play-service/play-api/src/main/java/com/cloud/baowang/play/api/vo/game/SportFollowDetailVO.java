package com.cloud.baowang.play.api.vo.game;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SportFollowDetailVO {

    @Schema(title = "盘口类型:1:冠军，2:赛事")
    private String type;

    @Schema(title = "详情")
    private List<SportFollowVO> list;

}

package com.cloud.baowang.play.api.vo.lobby;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "体育推荐赛事")
public class LobbySportEventsVO implements Serializable {

    @Schema(description = "赛事ID", required = true)
    private String eventsId;

}

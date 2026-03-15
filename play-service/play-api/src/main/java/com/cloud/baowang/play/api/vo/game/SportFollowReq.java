package com.cloud.baowang.play.api.vo.game;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SportFollowReq {

    @Schema(description = "赛事ID")
    private String thirdId;

    @Schema(description = "球类")
    private Integer sportType;

    @Schema(description = "盘口类型:1:冠军，2:赛事,3:赛事_球类,4:冠军_球类")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer type;



}

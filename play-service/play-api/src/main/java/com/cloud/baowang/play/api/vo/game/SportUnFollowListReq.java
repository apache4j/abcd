package com.cloud.baowang.play.api.vo.game;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SportUnFollowListReq {

    @Schema(title = "赛事ID")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<SportFollowReq> thirdId;
}

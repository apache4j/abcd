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
public class SportLobbyEvents {

    @Schema(description = "球类")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Long sportType;

    @Schema(description = "场馆")
    private String venueCode;


}

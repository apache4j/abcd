package com.cloud.baowang.play.api.vo.third;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "三方游戏登录对象检查活动")
public class CheckActivityVO implements Serializable {

    @Schema(title = "场馆code", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = ConstantsCode.PARAM_ERROR)
    private String venueCode;

    @Schema(title = "用户id", hidden = true)
    private String userId;

    @Schema(title = "站点code", hidden = true)
    private String siteCode;

}

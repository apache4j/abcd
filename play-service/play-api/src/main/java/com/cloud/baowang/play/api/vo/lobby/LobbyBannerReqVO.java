package com.cloud.baowang.play.api.vo.lobby;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "轮播图入参")
public class LobbyBannerReqVO {

    @Schema(description = "展示位置(大于0=一级分类ID,0:首页,-1:皮肤四,我的,-2:皮肤四,优惠活动)", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String gameOneClassId;


}

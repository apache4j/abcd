package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : 小智
 * @Date : 2024/7/29 14:13
 * @Version : 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(title = "站点游戏授权请求对象")
public class SiteGameRequestVO extends PageVO {

    @Schema(description = "游戏名称")
    private String gameName;

    @Schema(description = "游戏代码")
    private String gameId;

    @Schema(description = "场馆code")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String venueCode;

    @Schema(description = "状态")
    private String status;

    @Schema(description ="场馆code")
    private String siteCode;
}

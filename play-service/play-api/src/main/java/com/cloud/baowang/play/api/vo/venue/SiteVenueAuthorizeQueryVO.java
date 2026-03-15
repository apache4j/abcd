package com.cloud.baowang.play.api.vo.venue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 场馆授权回显反查对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "场馆授权回显反查对象")
public class SiteVenueAuthorizeQueryVO implements Serializable {

    @Schema(description = "场馆ID")
    private String venueId;
    @Schema(description = "场馆code")
    private String venueCode;

    @Schema(description = "关联游戏ID集合")
    private List<String> gameId;

    @Schema(description = "负盈利手续费")
    private BigDecimal handlingFee;

    @Schema(description = "有效流水费率")
    private BigDecimal validProportion;

}

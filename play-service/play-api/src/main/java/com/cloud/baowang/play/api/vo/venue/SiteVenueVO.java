package com.cloud.baowang.play.api.vo.venue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author : 小智
 * @Date : 2024/7/27 14:02
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "站点配置对象")
public class SiteVenueVO {

    @Schema(description = "场馆ID")
    private String venueId;
    @Schema(description = "场馆code")
    private String venueCode;
    @Schema(description = "关联游戏ID集合")
    private List<String> gameId;

    @Schema(description = "手续费")
    private BigDecimal handlingFee;

    @Schema(description = "场馆有效流水费率")
    private BigDecimal validProportion;
    @Schema(description = "操作人")
    private String operator;

}

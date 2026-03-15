package com.cloud.baowang.play.api.vo.venue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author : 小智
 * @Date : 2024/8/6 10:24
 * @Version : 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(title = "手续费对象")
public class FeeVO {
    @Schema(description = "venueId", hidden = true)
    private String venueId;

    @Schema(description = "venueCode")
    private String venueCode;

    @Schema(description = "负盈利手续费")
    private BigDecimal fee;

    @Schema(description = "有效流水费率")
    private BigDecimal validProportion;
}

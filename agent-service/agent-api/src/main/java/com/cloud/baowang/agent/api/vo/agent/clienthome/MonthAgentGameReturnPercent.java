package com.cloud.baowang.agent.api.vo.agent.clienthome;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/10/2 18:59
 * @Version: V1.0
 **/
@Data
@Schema(title = "每月流水返点比例")
public class MonthAgentGameReturnPercent {
    @Schema(description = "场馆类型")
    private String venueType;
    @Schema(description = "返点比例")
    private BigDecimal returnPercent;
}

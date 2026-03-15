package com.cloud.baowang.user.api.vo.medal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/8/8 11:03
 * @Version: V1.0
 **/
@Data
@Schema(description = "勋章奖励")
public class MedalRemarkRespVO {

    @Schema(description = "勋章名称")
    private String medalName;

    @Schema(description = "奖励金额")
    private BigDecimal rewardAmount;

}

package com.cloud.baowang.user.api.vo.medal;

import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
@Schema(description = "勋章额外奖励")
public class MedalRewardRemarkRespVO {

    @Schema(description = "解锁数量")
    private Integer unlockMedalNum;

    @Schema(description = "奖励金额")
    @JsonFormat(pattern = "0.00")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal rewardAmount;

}

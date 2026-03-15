package com.cloud.baowang.activity.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className: CheckInReqVO
 * @author: wade
 * @description: 签到入参
 * @date: 21/4/25 17:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "签到抽奖记录查询入参")
public class CheckInReqVO {

    @Schema(description = "时间2025-04-01")
    private String dataStr;

}

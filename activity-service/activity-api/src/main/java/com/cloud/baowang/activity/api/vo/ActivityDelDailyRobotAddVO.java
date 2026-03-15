package com.cloud.baowang.activity.api.vo;


import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//每日竞赛机器人配置
public class ActivityDelDailyRobotAddVO {


    @Schema(description = "站点编号",hidden = true)
    private String siteCode;

    @Schema(description = "id")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String id;


}

package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "充值方式请求 RechargeWayRequestVO")
public class RechargeWayRequestVO {

    private String mainCurrency;

    private Integer vipRank;

    private String siteCode;

    private Integer vipGradeCode;
    /**
     *  盘口模式 0:国际盘 1:大陆盘
     *  总控后台为null
     *  其他默认为0
     */
    private Integer handicapMode;
}

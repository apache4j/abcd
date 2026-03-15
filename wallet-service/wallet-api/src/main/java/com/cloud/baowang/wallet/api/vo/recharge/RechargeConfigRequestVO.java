package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "充值配置信息请求")
public class RechargeConfigRequestVO {


    /**
     * 充值方式ID
     */
    @Schema(description = "充值方式ID")
    private String rechargeWayId;


    private String userAccount;

    private String mainCurrency;

    private Integer vipGradeCode;

    private String siteCode;

    private String userId;
}

package com.cloud.baowang.wallet.api.vo.recharge;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SiteRechargeWayVO {

    /**
     * 充值配置ID
     * SystemRechargeWay.id
     */
    private Long rechargeWayId;

    /**
     * 站点代码
     */
    private String siteCode;

    /**
     * 手续费 5 代表5%
     */
    private BigDecimal wayFee;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;
}

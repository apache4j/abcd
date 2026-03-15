package com.cloud.baowang.wallet.api.vo.withdraw;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 站点提款方式配置表
 * </p>
 *
 * @author qiqi
 */
@Data
public class SiteWithdrawWayVO  {

    /**
     * 提款配置ID
     * SystemRechargeWay.id
     */
    private String withdrawId;

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

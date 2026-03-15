package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <p>
 * 站点提款方式配置表
 * </p>
 *
 * @author qiqi
 */
@Getter
@Setter
@TableName("site_withdraw_way")
public class SiteWithdrawWayPO extends BasePO {

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
    /**
     * 手续费类型
     */
    private Integer feeType;

    /**
     * 固定金额手续费
     */
    private BigDecimal wayFeeFixedAmount;

    /**
     * 排序
     */
    private Integer sortOrder;

}

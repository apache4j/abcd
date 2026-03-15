package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <p>
 * 站点充值方式配置表
 * </p>
 *
 * @author ford
 * @since 2024-07-27 05:22:56
 */
@Getter
@Setter
@TableName("site_recharge_way")
public class SiteRechargeWayPO extends BasePO {

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
     * 手续费类型 手续费类型 0百分比 1固定金额 2百分比+固定金额
     */
    private Integer feeType;

    /**
     * 手续费 5 代表5%
     */
    private BigDecimal wayFee;

    /**
     * 固定金额手续费
     */
    private BigDecimal wayFeeFixedAmount;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * VIP等级使用范围
     */
    private String vipGradeUseScope;


}

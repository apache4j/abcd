package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <p>
 * 充值方式配置表
 * </p>
 *
 * @author ford
 * @since 2024-07-26 11:50:43
 */
@Getter
@Setter
@TableName("system_recharge_way")
public class SystemRechargeWayPO extends BasePO {

    /**
     * 货币代码
     */
    private String currencyCode;
    /**
     * 充值类型Id
     */
    private String rechargeTypeId;

    /**
     * 充值类型编码
     */
    private String rechargeTypeCode;

    /**
     * 充值方式中文名称
     */
    private String rechargeWay;

    /**
     * 充值方式多语言
     */
    private String rechargeWayI18;

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
     * 快捷金额
     */
    private String quickAmount;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 图标
     */
    private String wayIcon;

    /**
     * 备注
     */
    private String memo;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;

    /**
     * 是否推荐 0:未推荐 1:推荐
     */
    private Integer recommendFlag;


    /**
     * 网络协议类型 TRC20 ERC20
     */
    private String networkType;


}

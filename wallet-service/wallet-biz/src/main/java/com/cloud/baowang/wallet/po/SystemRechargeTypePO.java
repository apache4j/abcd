package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 充值类型配置表
 * </p>
 *
 * @author ford
 * @since 2024-07-26 11:50:29
 */
@Getter
@Setter
@TableName("system_recharge_type")
public class SystemRechargeTypePO extends BasePO {

    /**
     * 货币代码
     */
    private String currencyCode;

    /**
     * 充值类型Code
     */
    private String rechargeCode;

    /**
     * 充值类型
     */
    private String rechargeType;

    /**
     * 充值类型多语言
     */
    private String rechargeTypeI18;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 备注
     */
    private String memo;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;

}

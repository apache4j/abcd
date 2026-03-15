package com.cloud.baowang.system.po.exchange;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 货币汇率配置表
 * </p>
 *
 * @author ford
 * @since 2024-05-20 08:21:41
 */
@Data
@TableName("system_rate_config")
public class SystemRateConfigPO extends BasePO {

    private String siteCode;

    /**
     * 货币代码
     */
    private String currencyCode;


    /**
     * 展示方式 WITHDRAW:取款 RECHARGE:存款
     */
    private String showWay;

    /**
     * 主货币代码
     */
    private String baseCurrencyCode;

    /**
     * 三方汇率
     */
    private BigDecimal thirdRate;

    /**
     * 汇率调整方式
     */
    private String adjustWay;

    /**
     * 调整数值
     */
    private String adjustNum;

    /**
     *  汇率类型: ENCRYPT:加密货币 CURRENCY:主货币
     */
    private String rateType;

    /**
     * 调整后汇率
     */
    private BigDecimal finalRate;

    /**
     * 状态 1:生效 0:未生效
     */
    private Integer status;

    /**
     * 三方汇率更新时间
     */
    private Long thirdRateTime;

}

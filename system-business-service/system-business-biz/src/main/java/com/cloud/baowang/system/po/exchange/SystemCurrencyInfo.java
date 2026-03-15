package com.cloud.baowang.system.po.exchange;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <p>
 * 币种信息
 * </p>
 *
 * @author ford
 * @since 2024-07-26 11:39:51
 */
@Getter
@Setter
@TableName("system_currency_info")
public class SystemCurrencyInfo  extends BasePO {

    private static final long serialVersionUID = 1L;

    /**
     * 货币代码
     */
    private String currencyCode;

    /**
     * 货币名称
     */
    private String currencyName;

    /**
     * 货币名称 多语言
     */
    private String currencyNameI18;

    /**
     * 货币符号
     */
    private String currencySymbol;


    /**
     * 精度 TWO:2位小数 K:千位
     */
    private String currencyDecimal;

    /**
     * 图标
     */
    private String currencyIcon;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;


    /**
     * 平台币兑换汇率
     */
    private BigDecimal finalRate;



}

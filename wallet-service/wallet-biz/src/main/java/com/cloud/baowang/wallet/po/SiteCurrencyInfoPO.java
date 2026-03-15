package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 站点币种信息
 * </p>
 *
 * @author ford
 * @since 2024-09-03
 */
@Data
@TableName("site_currency_info")
public class SiteCurrencyInfoPO extends SiteBasePO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 平台币代码-统一
     */
    private String platCurrencyCode;

    /**
     * 平台币简称
     */
    private String platCurrencyName;


    /**
     * 平台币符号
     */
    private String platCurrencySymbol;

    /**
     * 平台币图标
     */
    private String platCurrencyIcon;
    /**
     * 货币代码
     */
    private String currencyCode;

    /**
     * 平台币兑换汇率
     * eg: 越南盾 = 平台币 * finalRate
     * 平台币 = 越南盾 / finalRate
     */
    private BigDecimal finalRate;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;

    public Integer getSortOrder() {
        return sortOrder==null?-1:sortOrder;
    }
}

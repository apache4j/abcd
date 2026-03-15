package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <p>
 * 提款方式配置表
 * </p>
 *
 * @author qiqi
 */
@Getter
@Setter
@TableName("system_withdraw_way")
public class SystemWithdrawWayPO extends BasePO {

    /**
     * 货币代码
     */
    private String currencyCode;

    /**
     * 提款类型Code
     */
    private String withdrawTypeId;

    /**
     * 提款类型编码
     */
    private String withdrawTypeCode;

    /**
     * 提款方式
     */
    private String withdrawWay;


    /**
     * 提款方式 多语言
     */
    private String withdrawWayI18;

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
     * 信息收集 json格式
     */
    private String collectInfo;

    /**
     * 网络协议类型 TRC20 ERC20
     */
    private String networkType;

}

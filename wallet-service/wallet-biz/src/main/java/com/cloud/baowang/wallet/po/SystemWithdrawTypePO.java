package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 提款类型配置表
 * </p>
 *
 * @author qiqi
 */
@Getter
@Setter
@TableName("system_withdraw_type")
public class SystemWithdrawTypePO extends BasePO {

    /**
     * 货币代码
     */
    private String currencyCode;

    /**
     * 提款类型
     */
    private String withdrawTypeCode;

    /**
     * 充值类型
     */
    private String withdrawType;

    /**
     * 提款类型多语言
     */
    private String withdrawTypeI18;

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

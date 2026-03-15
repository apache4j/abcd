package com.cloud.baowang.system.po.partner;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

@TableName("system_payment_vendor")
@Data
public class SystemPaymentVendorPO extends BasePO {
    /**
     * 支付商名
     */
    private String vendorName;
    /**
     * 支付图标
     */
    private String vendorIcon;
    /**
     * 启用状态
     */
    private Integer status;
}

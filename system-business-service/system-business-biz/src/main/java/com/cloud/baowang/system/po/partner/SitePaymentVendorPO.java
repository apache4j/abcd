package com.cloud.baowang.system.po.partner;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

@Data
@TableName("site_payment_vendor")
public class SitePaymentVendorPO extends BasePO {
    /**
     * siteCode
     */
    private String siteCode;

    /**
     * 系统支付商ID
     */
    private Long systemPaymentVendorId;
    /**
     * 支付商名称
     */
    private String vendorName;
    /**
     * 支付商图标
     */
    private String vendorIcon;
    /**
     * {@link com.cloud.baowang.common.core.enums.EnableStatusEnum}
     * 启用状态0.禁用，1.启用
     */
    private Integer status;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 创建时间
     */
    private Long createdTime;
    /**
     * 修改人
     */
    private String updater;
    /**
     * 修改时间
     */
    private Long updatedTime;

}

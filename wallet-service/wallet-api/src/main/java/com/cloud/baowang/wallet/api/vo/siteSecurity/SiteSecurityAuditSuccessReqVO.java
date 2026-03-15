package com.cloud.baowang.wallet.api.vo.siteSecurity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2025/6/27 21:23
 * @Version: V1.0
 **/
@Data
public class SiteSecurityAuditSuccessReqVO {
    //来源订单号
    private String  sourceOrderNo;

    //站点编码
    private String siteCode;

    /**
     * 调整类型 增加保证金 减少保证金 设置保证金透支额度
     *  {@link com.cloud.baowang.wallet.api.enums.SiteSecurityReviewEnums}
     */
    private Integer adjustType;
    //调整金额
    private BigDecimal adjustAmount;
    //币种
    private String currency;

    //业务类型 不用传
    private String sourceOrderType;
    //修改人
    private String updateUser;
}

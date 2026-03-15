package com.cloud.baowang.wallet.api.vo.siteSecurity;

import com.cloud.baowang.wallet.api.enums.SiteSecurityCoinTypeEnums;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption: 帐变记录
 * @Author: Ford
 * @Date: 2025/6/27 21:23
 * @Version: V1.0
 **/
@Data
public class SiteSecurityBalanceChangeRecordReqVO {
    //来源订单号
    private String  sourceOrderNo;

    //站点编码
    private String siteCode;

    //调整金额
    private BigDecimal adjustAmount;

    //业务类型
    /**
     * {@link com.cloud.baowang.wallet.api.enums.SiteSecuritySourceCoinTypeEnums}
     */
    private String sourceCoinType;
    //帐变类型
    /**
     * {@link SiteSecurityCoinTypeEnums}
     */
    private String coinType;
    /**
     * 会员ID
     */
    private String userId;
    /**
     * 会员名称
     */
    private String userName;
    //修改人
    private String updateUser;
}

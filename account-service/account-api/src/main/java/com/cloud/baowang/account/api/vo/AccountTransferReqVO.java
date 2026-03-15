package com.cloud.baowang.account.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author ford
 * @Date 2025-10-14
 */
@Data
@Schema( description = "会员钱包账变请求对象")
public class AccountTransferReqVO {
    /**
     * 会员ID
     */
    @Schema( description ="会员ID")
    private String userId;

    @Schema( description ="站点编码")
    private String siteCode;

    @Schema( description ="会员账号")
    private String userAccount;

    @Schema( description ="账号状态")
    private String accountStatus;
    /**
     * 法币币种 必填
     */
    @Schema( description ="法币币种")
    private String currencyCode;
    /**
     * 系统订单号
     */
    @Schema( description ="系统订单号")
    private String innerOrderNo;

    @Schema( description ="三方场馆venue_code")
    private String venueCode;

    @Schema( description ="转入转出标识 false 用户钱带入场馆转入 true转出")
    private Boolean transferFlag;

    @Schema( description ="收支类型false 为锁定 true 锁定")
    private Boolean isLock;

    /**
     * WTC汇率
     */
    @Schema( description ="WTC汇率")
    private BigDecimal finalRate;
}

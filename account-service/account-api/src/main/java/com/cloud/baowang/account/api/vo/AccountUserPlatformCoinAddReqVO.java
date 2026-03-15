package com.cloud.baowang.account.api.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@Schema( description = "会员平台币钱包添加请求对象")
public class AccountUserPlatformCoinAddReqVO {

    @Schema( description ="会员ID")
    private String userId;

    /**
     * 上级代理
     */
    @Schema( description ="上级代理id")
    private String agentId;
    /**
     * 上级代理信息
     */
    @Schema( description ="上级代理账号")
    private String agentAccount;

    @Schema( description ="站点编码")
    private String siteCode;

    @Schema( description ="会员名称")
    private String userName;

    @Schema( description ="会员账号")
    private String userAccount;

    @Schema( description ="会员标签")
    private String userLabelId;

    @Schema( description ="VIP等级CODE")
    private Integer vipGradeCode;

    @Schema( description ="VIP等级")
    private Integer vipRank;

    @Schema( description ="账号状态")
    private String accountStatus;

    @Schema( description ="账号类型")
    private String accountType;

    @Schema( description ="风控层级ID")
    private String riskLevelId;


    @Schema( description ="风控层级")
    private String riskLevel;


    /**
     * 币种 必填
     */
    @Schema( description ="币种 WTC")
    private String currencyCode;
    /**
     * 系统订单号
     */
    @Schema( description ="系统订单号")
    private String innerOrderNo;
    /**
     * 三方关联订单号
     */
    @Schema( description ="三方交易单号")
    private String thirdOrderNo;

    /**
     * 三方CODE user，agent站点code,三方场馆venue_code/三方支付通道code
     */
    @Schema( description ="三方CODE user，agent站点code,三方场馆venue_code/三方支付通道code")
    private String toThirdCode;

    /**
     * 业务类型 必填
     * 对应枚举类 {@link com.cloud.baowang.account.api.enums.AccountPlatformWalletEnum.BusinessCoinTypeEnum}
     */
    @Schema( description ="业务类型")
    private String businessCoinType;

    /**
     * 账变类型 必填
     * 对应枚举类 {@link com.cloud.baowang.account.api.enums.AccountPlatformWalletEnum.CoinTypeEnum}
     */
    @Schema( description ="账变类型")
    private String coinType;

    /**
     * 客户端账变类型 WalletEnum.CustomerCoinTypeEnum
     * 非必填
     */
    @Schema( description ="客户端账变类型 ")
    private String customerCoinType;

    /**
     * 收支类型 1收入 2支出 必填
     * 对应枚举类 CoinBalanceTypeEnum
     */
    @Schema( description ="收支类型 1收入 2支出 ")
    private String balanceType;

    /**
     * 金额改变数量 必填
     */
    @Schema( description ="金额改变数量 ")
    private BigDecimal coinValue;


    /**
     * 备注
     */
    @Schema( description ="备注")
    private String remark;




    /**
     * 账变时间
     */
    @Schema( description ="账变时间")
    private Long coinTime;

    /**
     * WTC汇率
     */
    @Schema( description ="WTC汇率")
    private BigDecimal finalRate;


    /**
     * 活动标识
     * 对应枚举类 {@link com.cloud.baowang.account.api.enums.activity.AccountActivityTemplateEnum}
     * 必填
     */
    @Schema( description ="活动标识")
    private String activityFlag;
}

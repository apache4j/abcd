package com.cloud.baowang.account.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author mufan
 * @Date 2025-10-14
 */
@Data
@Schema( description = "会员钱包添加请求对象")
public class AccountBusinessUserReqVO {

    @Schema( description ="账号名称 用户名称，代理名称")
    private String accountName;

    @Schema( description ="来源用户编号 userId、agentId等")
    private String sourceAccountNo;

    @Schema( description ="来源user,agent站点code")
    private String siteCode;
    /**
     * {@link com.cloud.baowang.account.api.enums.AccountTransferEnums}
     */
    @Schema( description ="业务枚举code用于识别账务系统的业务和帐变类目类型")
    private String code;
    /**
     * {@link com.cloud.baowang.account.api.enums.SourceAccountTypeEnums}
     */
    @Schema( description ="业务枚举code用于识别账务系统的业务和帐变类目类型")
    private String userType;

    @Schema( description ="业务枚举code用于识别账务系统的业务和帐变类目类型用于如果是冻结则传递冻结标识,如果是VIP福利活着是活动优惠则传递对应的标识")
    private String bussinessFlag;

    /**
     * {@link com.cloud.baowang.account.api.enums.BalanceTypeEnums}
     */
    @Schema( description ="账变类型 +收入 -支出")
    private String BalanceType;

    @Schema( description ="法币币种 必填")
    private String currencyCode;

    @Schema( description ="用户状态")
    private String accountStatus;

    @Schema( description ="内部订单号")
    private String innerOrderNo;

    @Schema( description ="三方关联订单号")
    private String thirdOrderNo;

    @Schema( description ="去往user，agent站点code,三方场馆venue_code，三方支付code")
    private String toThridCode;

    @Schema( description ="用户当前账户总金额，只有第一次同步的时候用之后就不用了，第一次初始化用户账户钱包，冻结钱包")
    private BigDecimal walletAmount;

    @Schema( description ="金额改变数量 必填 所有游戏对接，账变记录 只做4位截断")
    private BigDecimal coinValue;

    @Schema( description ="账变时间")
    private Long coinTime;

    @Schema(description = "汇率")
    private BigDecimal finalRate;

}

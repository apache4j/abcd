package com.cloud.baowang.common.kafka.vo;

import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * @author: mufan
 * @createTime: 2025/10/25 18:11
 * @description:
 */

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "注单第一次结算发送的消息实体")
public class AccountPlatfromCoinRequestMqVO extends MessageBaseVO {

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

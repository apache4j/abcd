package com.cloud.baowang.wallet.api.vo.userCoin;


import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@Schema( description = "会员钱包添加请求对象")
public class UserCoinAddVO {

    private String userName;


    /**
     * 会员ID 必填
     */
    private String userId;

    /**
     * 法币币种 必填
     */
    private String currency;

    /**
     * 订单号 必填
     */
    private String orderNo;

    /**
     * 三方单号
     */
    private String thirdOrderNo;


    /**
     * 三方充值渠道code
     */
    private String toThridCode;

    /**
     * 业务类型 必填
     * 对应枚举类  {@link com.cloud.baowang.wallet.api.enums.wallet.WalletEnum.BusinessCoinTypeEnum}
     */
    private String businessCoinType;

    /**
     * 账变类型 必填
     * 对应枚举类  {@link com.cloud.baowang.wallet.api.enums.wallet.WalletEnum.CoinTypeEnum}
     */
    private String coinType;

    /**
     * 客户端账变类型 {@link com.cloud.baowang.wallet.api.enums.wallet.WalletEnum.CustomerCoinTypeEnum}
     * 必填
     */
    private String customerCoinType;

    /**
     * 收支类型 1收入 2支出 3冻结 4解冻
     * 对应枚举类 CoinBalanceTypeEnum
     * 必填
     */
    private String balanceType;

    /**
     * 金额改变数量 必填 所有游戏对接，账变记录 只做4位截断
     */
    private BigDecimal coinValue;


    /**
     * 备注
     */
    private String remark;

    /**
     * 冻结标记，为1 只扣除冻结金额
     *
     */
    private Integer freezeFlag;

    /**
     * 用户信息
     * 必填
     */
    private WalletUserInfoVO userInfoVO;

    /**
     * 账变时间
     */
    private Long coinTime;

    private String venueCode;


    /**
     * 描述信息，用于存特殊场馆的一些备注
     */
    private String descInfo;


    /**
     * 新的财务游戏必填-只要关注游戏的即可
     * 对应枚举类  {@link com.cloud.baowang.account.api.enums.AccountCoinTypeEnums}
     */
    private String accountCoinType;

    /**
     * 活动标识
     * 对应枚举类 {@link com.cloud.baowang.account.api.enums.activity.AccountActivityTemplateEnum}
     * 必填
     */
    @Schema( description ="活动标识")
    private String activityFlag;


}

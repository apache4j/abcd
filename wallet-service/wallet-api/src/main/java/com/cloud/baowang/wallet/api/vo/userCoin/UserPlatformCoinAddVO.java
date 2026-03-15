package com.cloud.baowang.wallet.api.vo.userCoin;


import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@Schema( description = "会员平台币钱包添加请求对象")
public class UserPlatformCoinAddVO {

    /**
     * 会员ID 必填
     */
    private String userId;


    /**
     * 订单号 必填
     */
    private String orderNo;

    /**
     * 业务类型 必填
     * 对应枚举类 {@link com.cloud.baowang.common.core.enums.wallet.PlatformWalletEnum.BusinessCoinTypeEnum}
     */
    private String businessCoinType;

    /**
     * 账变类型 必填
     * 对应枚举类 {@link com.cloud.baowang.common.core.enums.wallet.PlatformWalletEnum.CoinTypeEnum}
     */
    private String coinType;

    /**
     * 客户端账变类型 WalletEnum.CustomerCoinTypeEnum
     * 非必填
     */
    private String customerCoinType;

    /**
     * 收支类型 1收入 2支出 必填
     * 对应枚举类 CoinBalanceTypeEnum
     */
    private String balanceType;

    /**
     * 金额改变数量 必填
     */
    private BigDecimal coinValue;


    /**
     * 备注
     */
    private String remark;


    /**
     * 用户信息 必填
     */
    private WalletUserInfoVO userInfoVO;


    /**
     * 账变时间
     */
    private Long coinTime;

    /**
     * 活动标识
     * 对应枚举类 {@link com.cloud.baowang.account.api.enums.activity.AccountActivityTemplateEnum}
     * 必填
     */
    @Schema( description ="活动标识")
    private String activityFlag;
}

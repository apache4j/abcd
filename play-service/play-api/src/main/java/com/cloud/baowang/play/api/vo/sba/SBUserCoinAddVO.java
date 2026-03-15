package com.cloud.baowang.play.api.vo.sba;

import cn.hutool.core.util.ObjectUtil;
//import com.cloud.baowang.user.api.vo.UserInfoVO;
//import com.cloud.baowang.wallet.api.enums.usercoin.FreezeFlagEnum;
//import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
//import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
//import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SBUserCoinAddVO {


    /**
     * 扣款唯一ID
     */
    private String orderId;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 币种
     */
    private String currency;

    /**
     * true = 加款,false = 扣款
     */
    private boolean type;

    /**
     * 用户信息
     */
//    private UserInfoVO userInfoVO;
//
//    /**
//     * 类型
//     */
//    private CoinBalanceTypeEnum coinBalanceTypeEnum;


    /**
     * @param orderId             唯一ID
     * @param userId              玩家账号
     * @param amount              账变金额
     * @param currency            币种
     * @param type                true = 加款，false = 扣款
     * @param userInfoVO          用户信息
     * @param coinBalanceTypeEnum
     * @param freezeFlagEnum      扣款类型
     */
//    public static UserCoinAddVO buildUserCoinAddVO(String orderId, String userId, BigDecimal amount
//            , String currency, boolean type, UserInfoVO userInfoVO, CoinBalanceTypeEnum coinBalanceTypeEnum,
//                                                   FreezeFlagEnum freezeFlagEnum, String remark) {
//        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
//        userCoinAddVO.setOrderNo(orderId);
//        userCoinAddVO.setCurrency(currency);
//        userCoinAddVO.setRemark(remark);
//
//        WalletEnum.CoinTypeEnum coinTypeEnum = null;
//        WalletEnum.BusinessCoinTypeEnum businessCoinTypeEnum = null;
//        WalletEnum.CustomerCoinTypeEnum customerCoinTypeEnum = null;
//
//
//        if (!type) {
//            coinTypeEnum = WalletEnum.CoinTypeEnum.GAME_BET;
//            businessCoinTypeEnum = WalletEnum.BusinessCoinTypeEnum.GAME_BET;
//            customerCoinTypeEnum = WalletEnum.CustomerCoinTypeEnum.GAME_BET;
//        } else {
//            coinTypeEnum = WalletEnum.CoinTypeEnum.GAME_PAYOUT;
//            businessCoinTypeEnum = WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT;
//            customerCoinTypeEnum = WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT;
//        }
//        userCoinAddVO.setCoinType(coinTypeEnum.getCode());
//        userCoinAddVO.setBalanceType(coinBalanceTypeEnum.getCode());
//        userCoinAddVO.setBusinessCoinType(businessCoinTypeEnum.getCode());
//        userCoinAddVO.setCustomerCoinType(customerCoinTypeEnum.getCode());
//        userCoinAddVO.setUserId(userId);
//        userCoinAddVO.setCoinValue(amount);
//        userCoinAddVO.setUserInfoVO(userInfoVO);
//        if (ObjectUtil.isNotEmpty(freezeFlagEnum)) {
//            userCoinAddVO.setFreezeFlag(freezeFlagEnum.getCode());
//        }
//        return userCoinAddVO;
//    }
}

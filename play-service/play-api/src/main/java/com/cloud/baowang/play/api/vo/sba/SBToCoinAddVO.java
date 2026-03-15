package com.cloud.baowang.play.api.vo.sba;

//import com.cloud.baowang.wallet.api.enums.usercoin.FreezeFlagEnum;
//import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SBToCoinAddVO {


    /**
     * 备注
     */
    private String remark;

    /**
     * 扣款唯一ID
     */
    private String orderId;

    /**
     * 用户账号
     */
    private String userId;

    /**
     * 新增金额
     */
    private BigDecimal addAmount;

    /**
     * 扣除金额
     */
    private BigDecimal subAmount;

    /**
     * 实际账变金额
     */
    private BigDecimal amount;

    /**
     * true = 增加余额,false = 扣除余额
     */
    private Boolean type;

    /**
     * 类型
     */
    private String coinBalanceTypeEnum;


    private Integer freezeFlagEnum;




    /**
     * @param orderId             账变唯一ID
     * @param userId         用户账号
     * @param addAmount           加款金额
     * @param subAmount           扣款金额
     * @param coinBalanceTypeEnum 支收类型
     * @param freezeFlagEnum      冻结标记
     */
//    public static SBToCoinAddVO getBuilderCoinAdd(String orderId, String userId, BigDecimal addAmount,
//                                                  BigDecimal subAmount, CoinBalanceTypeEnum coinBalanceTypeEnum, FreezeFlagEnum freezeFlagEnum) {
//
//        //true = 增加余额,false = 扣除余额
//        boolean type = addAmount.compareTo(BigDecimal.ZERO) > 0;
//
//        BigDecimal amount = type ? addAmount : subAmount;
//
//        //不等于冻结 跟解冻 的情况下,根据 type来判断是什么类型
//        if (!CoinBalanceTypeEnum.FREEZE.equals(coinBalanceTypeEnum) && !CoinBalanceTypeEnum.UN_FREEZE.equals(coinBalanceTypeEnum)) {
//            coinBalanceTypeEnum = type ? CoinBalanceTypeEnum.INCOME : CoinBalanceTypeEnum.EXPENSES;
//        }
//
//        return SBToCoinAddVO.builder()
//                .orderId(orderId)
//                .type(type)
//                .userId(userId)
//                .amount(amount)
//                .addAmount(addAmount)
//                .subAmount(subAmount)
//                .coinBalanceTypeEnum(coinBalanceTypeEnum)
//                .freezeFlagEnum(freezeFlagEnum)
//                .build();
//    }

}

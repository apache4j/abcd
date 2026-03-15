package com.cloud.baowang.wallet.api.vo.user;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiqi
 */
@Data
@I18nClass
@AllArgsConstructor
@NoArgsConstructor
@Schema(title ="会员财务信息")
public class WalletUserFinanceVO {


    @Schema(description ="盈利信息")
    private WalletUserProfitVO userProfitVO;

    @Schema(description ="财务会员投注信息")
    private WalletUserBetsVO userBets;

    @Schema(description ="财务会员充提信息")
    private WalletUserDepositWithdrawVO  userDepositWithdraw;

    @Schema(description ="活动优惠信息")
    private WalletUserPromotionsVO userPromotionsVO;

    @Schema(description ="输赢前3")
    private List<WalletUserBetsVO> winLoseTopThree;

    @Schema(description ="投注前3")
    private List<WalletUserBetsVO> betsTopThree;
}

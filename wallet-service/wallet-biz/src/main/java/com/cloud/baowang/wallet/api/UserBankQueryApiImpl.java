package com.cloud.baowang.wallet.api;


import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WithdrawTypeEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserBankQueryApi;
import com.cloud.baowang.wallet.api.vo.user.WalletUserBasicRequestVO;
import com.cloud.baowang.wallet.api.vo.uservirtualcurrency.UserDepositWithdrawalResponseVO;
import com.cloud.baowang.wallet.service.UserDepositWithdrawService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
public class UserBankQueryApiImpl implements UserBankQueryApi {



    private final UserDepositWithdrawService userDepositWithdrawService;


    /**
     * @param requestVO
     * @return
     */
    @Override
    public ResponseVO<List<UserDepositWithdrawalResponseVO>> queryBankCardInfo(WalletUserBasicRequestVO requestVO) {
        requestVO.setDepositWithdrawTypeCode(WithdrawTypeEnum.BANK_CARD.getCode());
        requestVO.setRiskType(RiskTypeEnum.RISK_BANK.getCode());
        return ResponseVO.success(userDepositWithdrawService.userDepositWithdrawalList(requestVO));
    }

    /**
     * @param requestVO
     * @return 虚拟币
     */
    @Override
    public ResponseVO<List<UserDepositWithdrawalResponseVO>> queryVirtualInfo(WalletUserBasicRequestVO requestVO) {
        requestVO.setDepositWithdrawTypeCode(WithdrawTypeEnum.CRYPTO_CURRENCY.getCode());
        requestVO.setRiskType(RiskTypeEnum.RISK_VIRTUAL.getCode());
        return ResponseVO.success(userDepositWithdrawService.userDepositWithdrawalList(requestVO));
    }

    @Override
    public ResponseVO<List<UserDepositWithdrawalResponseVO>> queryWalletInfo(WalletUserBasicRequestVO requestVO) {
        requestVO.setDepositWithdrawTypeCode(WithdrawTypeEnum.ELECTRONIC_WALLET.getCode());
        requestVO.setRiskType(RiskTypeEnum.RISK_WALLET.getCode());
        return ResponseVO.success(userDepositWithdrawService.userDepositWithdrawalList(requestVO));
    }
}

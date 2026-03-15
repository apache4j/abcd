package com.cloud.baowang.wallet.api;


import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserWithdrawConfigApi;
import com.cloud.baowang.wallet.api.vo.withdraw.UserVipWithdrawConfigAPPVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigAddOrUpdateVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigVO;
import com.cloud.baowang.wallet.po.UserWithdrawConfigPO;
import com.cloud.baowang.wallet.service.UserWithdrawConfigService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author qiqi
 */
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserWithdrawConfigApiImpl implements UserWithdrawConfigApi {

    private final UserWithdrawConfigService userWithdrawConfigService;


    @Override
    public ResponseVO<List<UserWithdrawConfigVO>> listUserWithdrawConfig(UserWithdrawConfigRequestVO vo) {
        return ResponseVO.success(userWithdrawConfigService.listUserWithdrawConfig(vo));
    }


    @Override
    public ResponseVO<Integer> addUserWithdrawConfig(@RequestBody UserWithdrawConfigAddOrUpdateVO vo) {
        checkWithdrawConfigParam(vo);
       /* if (!userWithdrawConfigService.checkVipRankUnique(vo.getVipRankCode(),  null)) {
            throw new BaowangDefaultException(ResultCode.VIP_RANK_CODE_IS_EXIST);
        }*/
        return ResponseVO.success(userWithdrawConfigService.addUserWithdrawConfig(vo));
    }

    @Override
    public ResponseVO<Boolean> initUserWithdrawConfigData(List<UserWithdrawConfigAddOrUpdateVO> vos) {
        return ResponseVO.success(userWithdrawConfigService.initUserWithdrawConfigData(vos));
    }


    @Override
    public ResponseVO<Integer> updateUserWithdrawConfig(@RequestBody UserWithdrawConfigAddOrUpdateVO vo) {
        checkWithdrawConfigParam(vo);
        /*if (!userWithdrawConfigService.checkVipRankUnique(vo.getVipRankCode(),  vo.getId())) {
            throw new BaowangDefaultException(ResultCode.VIP_RANK_CODE_IS_EXIST);
        }*/

        return ResponseVO.success(userWithdrawConfigService.updateUserWithdrawConfig(vo));
    }

    @Override
    public ResponseVO<Boolean> updateBySiteCodeAndRankCode(String siteCode, Integer vipRankCode, List<UserWithdrawConfigAddOrUpdateVO> vos) {
        return ResponseVO.success(userWithdrawConfigService.updateBySiteCodeAndRankCode(siteCode, vipRankCode, vos));
    }

    public ResponseVO<Boolean> updateBySiteCodeAndVipGradeCode( UserWithdrawConfigAddOrUpdateVO vo) {
        return ResponseVO.success(userWithdrawConfigService.updateBySiteCodeAndVipGradeCode(vo));
    }

    @Override
    public ResponseVO<UserWithdrawConfigVO> detailUserWithdrawConfig(IdVO idVO) {
        UserWithdrawConfigVO userWithdrawConfigVO = userWithdrawConfigService.detailUserWithdrawConfig(idVO);
        return ResponseVO.success(userWithdrawConfigVO);
    }

    private void checkWithdrawConfigParam(UserWithdrawConfigAddOrUpdateVO vo) {
        BigDecimal singleMaxWithdrawAmount = vo.getSingleMaxWithdrawAmount();
        if (vo.getBankCardSingleWithdrawMinAmount().compareTo(vo.getBankCardSingleWithdrawMaxAmount()) >= 0) {
            throw new BaowangDefaultException(ResultCode.BANK_CARD_MIN_AMOUNT_GT_BANK_MAX_AMOUNT);
        }
        if (vo.getCryptoCurrencySingleWithdrawMinAmount().compareTo(vo.getCryptoCurrencySingleWithdrawMaxAmount()) >= 0) {
            throw new BaowangDefaultException(ResultCode.CRYPTO_CURRENCY_MIN_AMOUNT_GT_VIRTUAL_MAX_AMOUNT);
        }
        if (vo.getElectronicWalletWithdrawMinAmount().compareTo(vo.getElectronicWalletWithdrawMaxAmount()) >= 0) {
            throw new BaowangDefaultException(ResultCode.ELECTRONIC_WALLET_MIN_AMOUNT_GT_ELECTRONIC_WALLET_MAX_AMOUNT);
        }

    }


}

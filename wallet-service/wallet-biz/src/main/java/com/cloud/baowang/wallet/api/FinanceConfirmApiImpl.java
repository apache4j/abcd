package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.ReviewVO;
import com.cloud.baowang.user.api.vo.StatusVO;
import com.cloud.baowang.wallet.api.api.FinanceConfirmApi;
import com.cloud.baowang.wallet.api.vo.WalletStatusVO;
import com.cloud.baowang.wallet.api.vo.activity.WalletReviewVO;
import com.cloud.baowang.wallet.api.vo.financeconfirm.FinanceManualConfirmQueryVO;
import com.cloud.baowang.wallet.api.vo.financeconfirm.FinanceManualConfirmRecordQueryVO;
import com.cloud.baowang.wallet.api.vo.financeconfirm.FinanceManualConfirmRecordVO;
import com.cloud.baowang.wallet.api.vo.financeconfirm.FinanceManualConfirmVO;
import com.cloud.baowang.wallet.service.UserWithdrawRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class FinanceConfirmApiImpl implements FinanceConfirmApi {

    private final UserWithdrawRecordService userWithdrawRecordService;

    @Override
    public ResponseVO<Page<FinanceManualConfirmVO>> manualConfirmMemberWithdrawPage(FinanceManualConfirmQueryVO requestVO, String adminName) {
        return ResponseVO.success(userWithdrawRecordService.manualConfirmMemberWithdrawPage(requestVO, adminName));
    }

    @Override
    public ResponseVO<Boolean> withdrawManualLock(WalletStatusVO vo, String adminId, String adminName) {
        return userWithdrawRecordService.withdrawManualLock(vo,adminId,adminName);
    }

    @Override
    public ResponseVO<Boolean> withdrawManualOneSuccess(WalletReviewVO vo, String adminId, String adminName) {
        return userWithdrawRecordService.withdrawManualOneSuccess(vo,adminId,adminName);
    }

    @Override
    public ResponseVO<Boolean> withdrawManualOneFail(WalletReviewVO vo, String adminId, String adminName) {
        return userWithdrawRecordService.withdrawManualOneFail(vo,adminId,adminName);
    }

    @Override
    public ResponseVO<Page<FinanceManualConfirmRecordVO>> manualConfirmMemberWithdrawRecPage(FinanceManualConfirmRecordQueryVO requestVO) {
        return ResponseVO.success(userWithdrawRecordService.manualConfirmMemberWithdrawRecPage(requestVO));
    }

    @Override
    public ResponseVO<Long> manualConfirmMemberWithdrawRecCount(FinanceManualConfirmRecordQueryVO vo) {
        return ResponseVO.success(userWithdrawRecordService.manualConfirmMemberWithdrawRecCount(vo));
    }
}

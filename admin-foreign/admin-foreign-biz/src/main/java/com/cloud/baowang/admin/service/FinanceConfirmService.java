package com.cloud.baowang.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.FinanceConfirmApi;
import com.cloud.baowang.wallet.api.vo.WalletStatusVO;
import com.cloud.baowang.wallet.api.vo.activity.WalletReviewVO;
import com.cloud.baowang.wallet.api.vo.financeconfirm.FinanceManualConfirmQueryVO;
import com.cloud.baowang.wallet.api.vo.financeconfirm.FinanceManualConfirmRecordQueryVO;
import com.cloud.baowang.wallet.api.vo.financeconfirm.FinanceManualConfirmRecordVO;
import com.cloud.baowang.wallet.api.vo.financeconfirm.FinanceManualConfirmVO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class FinanceConfirmService {


    private final FinanceConfirmApi financeConfirmApi;
    public ResponseVO<Page<FinanceManualConfirmVO>> manualConfirmMemberWithdrawPage(FinanceManualConfirmQueryVO requestVO, String adminName) {
        return financeConfirmApi.manualConfirmMemberWithdrawPage(requestVO, adminName);
    }

    public ResponseVO<Boolean> withdrawManualLock(WalletStatusVO vo, String adminId, String adminName) {
        return financeConfirmApi.withdrawManualLock(vo, adminId, adminName);
    }

    public ResponseVO<Boolean> withdrawManualOneSuccess(WalletReviewVO vo, String adminId, String adminName) {
        return financeConfirmApi.withdrawManualOneSuccess(vo, adminId, adminName);
    }

    public ResponseVO<Boolean> withdrawManualOneFail(WalletReviewVO vo, String adminId, String adminName) {
        return financeConfirmApi.withdrawManualOneFail(vo, adminId, adminName);
    }

    public ResponseVO<Page<FinanceManualConfirmRecordVO>> manualConfirmMemberWithdrawRecPage(FinanceManualConfirmRecordQueryVO requestVO) {
        return financeConfirmApi.manualConfirmMemberWithdrawRecPage(requestVO);
    }
}

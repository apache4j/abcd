package com.cloud.baowang.wallet.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.wallet.api.vo.userCoin.UserDepositWithdrawalResVO;
import com.cloud.baowang.wallet.api.api.UserWithdrawRecordApi;
import com.cloud.baowang.wallet.api.vo.agent.WalletAgentSubLineReqVO;
import com.cloud.baowang.wallet.api.vo.agent.WalletAgentSubLineResVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordRespVO;
import com.cloud.baowang.wallet.api.vo.risk.RiskWithdrawRecordVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserDepositWithdrawVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawRecordPagesVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawalRecordRequestVO;
import com.cloud.baowang.wallet.service.UserDepositWithdrawService;
import com.cloud.baowang.wallet.service.UserManualDownRecordService;
import com.cloud.baowang.wallet.service.UserWithdrawRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserWithdrawRecordApiImpl implements UserWithdrawRecordApi {

    private final UserWithdrawRecordService userWithdrawRecordService;

    private final UserDepositWithdrawService userDepositWithdrawService;

    private final UserManualDownRecordService userManualDownRecordService;

    @Override
    public UserWithdrawRecordPagesVO withdrawalRecordPageList(UserWithdrawalRecordRequestVO vo) {
        return userWithdrawRecordService.withdrawalRecordPageList(vo);
    }

    @Override
    public Long withdrawalRecordPageCount(UserWithdrawalRecordRequestVO vo) {
        return userWithdrawRecordService.withdrawalRecordPageCount(vo);
    }

    @Override
    public WalletUserDepositWithdrawVO getUserDepositWithdraw(String userId) {
        return userDepositWithdrawService.getUserDepositWithdraw(userId);
    }

    @Override
    public List<WalletAgentSubLineResVO> getUserFundsListByAgent(WalletAgentSubLineReqVO reqVO) {
        return userDepositWithdrawService.getUserFundsListByAgent(reqVO);
    }

    @Override
    public List<WalletAgentSubLineResVO> getManualAmountGroupAgent(WalletAgentSubLineReqVO reqVO) {
        return userManualDownRecordService.getManualAmountGroupAgent(reqVO);
    }

    @Override
    public UserDepositWithdrawalResVO getRecordByOrderId(String orderId) {
        return userDepositWithdrawService.getRecordByOrderId(orderId);
    }

    @Override
    public UserDepositRecordRespVO getWithDrawalRecord(UserWithdrawalRecordRequestVO vo) {
        return userDepositWithdrawService.getWithDrawalRecord(vo);
    }

    @Override
    public Page<RiskWithdrawRecordVO> getWithdrawalRecordDuplicateList(UserWithdrawalRecordRequestVO vo) {
        return userWithdrawRecordService.getWithdrawalRecordDuplicateList(vo);
    }

    @Override
    public long getWithdrawalRecordDuplicateListCount(UserWithdrawalRecordRequestVO vo) {
        return userWithdrawRecordService.getWithdrawalRecordDuplicateListCount(vo);
    }

    @Override
    public UserDepositWithdrawalResVO getUserFirstSuccessWithdrawal(UserWithdrawalRecordRequestVO vo) {
        return userDepositWithdrawService.getUserFirstSuccessWithdrawal(vo);
    }

}

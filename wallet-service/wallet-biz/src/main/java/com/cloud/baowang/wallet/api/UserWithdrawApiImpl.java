package com.cloud.baowang.wallet.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserWithdrawApi;
import com.cloud.baowang.wallet.api.vo.withdraw.CheckRemainingFlowVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithDrawApplyVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawConfigRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawConfigVO;
import com.cloud.baowang.wallet.service.UserDepositRecordService;
import com.cloud.baowang.wallet.service.UserWithdrawService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserWithdrawApiImpl implements UserWithdrawApi {

    private final UserWithdrawService userWithdrawService;


    private UserDepositRecordService userDepositRecordService;

    @Override
    public ResponseVO<Integer> userWithdrawApply(UserWithDrawApplyVO vo) {
        return userWithdrawService.userWithdrawApply(vo);
    }

    @Override
    public WithdrawConfigVO getWithdrawConfig(WithdrawConfigRequestVO vo) {
        return userWithdrawService.getWithdrawConfig(vo);
    }

    @Override
    public CheckRemainingFlowVO checkRemainingFlow(String userId) {
        return userWithdrawService.checkRemainingFlow(userId);
    }


}

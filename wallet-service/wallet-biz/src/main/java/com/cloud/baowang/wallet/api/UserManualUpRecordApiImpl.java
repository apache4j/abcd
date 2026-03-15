package com.cloud.baowang.wallet.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserManualUpRecordApi;
import com.cloud.baowang.wallet.api.vo.agent.WalletAgentActiveVO;
import com.cloud.baowang.wallet.api.vo.agent.AgentUserTeamParam;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualUpRecordPageVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualUpRecordResult;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.GetDepositWithdrawManualRecordListResponse;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.GetDepositWithdrawManualRecordListVO;
import com.cloud.baowang.wallet.service.UserManualUpDownRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserManualUpRecordApiImpl implements UserManualUpRecordApi {

    private UserManualUpDownRecordService userManualUpDownRecordService;

    @Override
    public UserManualUpRecordResult getUpRecordPage(UserManualUpRecordPageVO vo) {
        return userManualUpDownRecordService.getUpRecordPage(vo);
    }

    @Override
    public ResponseVO<Long> getUpRecordPageCount(UserManualUpRecordPageVO vo) {
        return userManualUpDownRecordService.getUpRecordPageCount(vo);
    }

    @Override
    public WalletAgentActiveVO getDepositActiveInfo(AgentUserTeamParam vo) {
        return userManualUpDownRecordService.getDepositActiveInfo(vo);
    }

    @Override
    public GetDepositWithdrawManualRecordListResponse getDepositWithdrawManualRecordList(GetDepositWithdrawManualRecordListVO vo) {
        return userManualUpDownRecordService.getDepositWithdrawManualRecordList(vo);
    }
}

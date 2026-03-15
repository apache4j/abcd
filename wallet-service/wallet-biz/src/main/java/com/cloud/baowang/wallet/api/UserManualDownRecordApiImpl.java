package com.cloud.baowang.wallet.api;

import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentWinLossParamVO;
import com.cloud.baowang.agent.api.vo.agent.winLoss.UserDepositSumVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserManualDownRecordApi;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownRecordResponseVO;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownSubmitVO;
import com.cloud.baowang.wallet.service.UserManualDownRecordService;
import com.cloud.baowang.wallet.service.UserManualUpDownRecordService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
public class UserManualDownRecordApiImpl  implements UserManualDownRecordApi {

    private final UserManualDownRecordService userManualDownRecordService;
    private final UserManualUpDownRecordService userManualUpDownRecordService;


    @Override
    public ResponseVO<Boolean> saveManualDown(UserManualDownSubmitVO vo) {
        return userManualDownRecordService.saveManualDown(vo);
    }

    @Override
    public ResponseVO<UserManualDownRecordResponseVO> listUserManualDownRecordPage(UserManualDownRecordRequestVO userCoinRecordRequestVO) {
        return ResponseVO.success(userManualDownRecordService.listUserManualDownRecordPage(userCoinRecordRequestVO));
    }

    @Override
    public ResponseVO<Long> listUserManualDownRecordPageExportCount(UserManualDownRecordRequestVO vo) {
        return ResponseVO.success(userManualDownRecordService.listUserManualDownRecordPageExportCount(vo));
    }


    @Override
    public long getTotalPendingReviewBySiteCode(String siteCode) {
        return userManualUpDownRecordService.getTotalPendingReviewBySiteCode(siteCode);
    }


}

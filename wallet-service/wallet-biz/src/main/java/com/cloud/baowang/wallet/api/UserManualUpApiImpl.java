package com.cloud.baowang.wallet.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserManualUpApi;
import com.cloud.baowang.wallet.api.vo.fundadjust.GetUserBalanceQueryVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.GetUserBalanceVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualAccountResponseVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualAccountResultVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualDownAccountResponseVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualDownAccountResultVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualUpSubmitVO;
import com.cloud.baowang.wallet.service.UserManualUpDownRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserManualUpApiImpl implements UserManualUpApi {

    private UserManualUpDownRecordService userManualUpDownRecordService;

    @Override
    public ResponseVO<Boolean> submit(UserManualUpSubmitVO vo,String operator) {
        return userManualUpDownRecordService.submit(vo, operator);
    }

    @Override
    public ResponseVO<GetUserBalanceVO> getUserBalance(GetUserBalanceQueryVO vo) {
        return userManualUpDownRecordService.getUserBalance(vo);
    }

    @Override
    public ResponseVO<UserManualAccountResponseVO> checkUpUserAccountInfo(List<UserManualAccountResultVO> list) {
        return userManualUpDownRecordService.checkUpUserAccountInfo(list);
    }

    @Override
    public ResponseVO<UserManualDownAccountResponseVO> checkDownUserAccountInfo(List<UserManualDownAccountResultVO> list) {
        return userManualUpDownRecordService.checkDownUserAccountInfo(list);
    }
}

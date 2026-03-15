package com.cloud.baowang.wallet.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserPlatformCoinManualUpRecordApi;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpRecordPageVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpRecordResult;
import com.cloud.baowang.wallet.service.UserPlatformCoinManualUpDownRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserPlatformCoinManualUpRecordApiImpl implements UserPlatformCoinManualUpRecordApi {

    private UserPlatformCoinManualUpDownRecordService userPlatformCoinManualUpDownRecordService;

    @Override
    public UserPlatformCoinManualUpRecordResult getUpRecordPage(UserPlatformCoinManualUpRecordPageVO vo) {
        return userPlatformCoinManualUpDownRecordService.getUpRecordPage(vo);
    }

    @Override
    public ResponseVO<Long> getUpRecordPageCount(UserPlatformCoinManualUpRecordPageVO vo) {
        return userPlatformCoinManualUpDownRecordService.getUpRecordPageCount(vo);
    }

}

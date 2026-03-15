package com.cloud.baowang.wallet.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserPlatformCoinManualDownRecordApi;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualDownRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualDownRecordResponseVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpRecordVO;
import com.cloud.baowang.wallet.service.UserPlatformCoinManualUpDownRecordService;
import com.cloud.baowang.wallet.service.UserPlatformCoinManualUpDownService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
public class UserPlatformCoinManualDownRecordApiImpl implements UserPlatformCoinManualDownRecordApi {


    private final UserPlatformCoinManualUpDownRecordService userPlatformCoinManualUpDownService;



    @Override
    public ResponseVO<UserPlatformCoinManualDownRecordResponseVO> listPlatformCoinUserManualDownRecordPage(UserPlatformCoinManualDownRecordRequestVO vo) {
        return ResponseVO.success(userPlatformCoinManualUpDownService.listUserManualDownRecordPage(vo));
    }

    @Override
    public ResponseVO<Long> listUserPlatformCoinManualDownRecordPageExportCount(UserPlatformCoinManualDownRecordRequestVO vo) {
        return ResponseVO.success(userPlatformCoinManualUpDownService.listUserManualDownRecordPageExportCount(vo));
    }

    @Override
    public Long getUpRecordTodoCount(UserPlatformCoinManualUpRecordVO vo) {
        return userPlatformCoinManualUpDownService.getUpRecordTodoCount(vo);
    }



}

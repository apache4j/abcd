package com.cloud.baowang.wallet.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
import com.cloud.baowang.wallet.api.api.UserPlatformCoinRecordApi;
import com.cloud.baowang.wallet.api.vo.report.WinLoseRecalculateReqWalletVO;
import com.cloud.baowang.wallet.api.vo.report.WinLoseRecalculateWalletVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordResponseVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserPlatformCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserPlatformCoinRecordResponseVO;
import com.cloud.baowang.wallet.service.UserCoinRecordService;
import com.cloud.baowang.wallet.service.UserPlatformCoinRecordService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
public class UserPlatformCoinRecordApiImpl implements UserPlatformCoinRecordApi {

    private final UserPlatformCoinRecordService userCoinRecordService;


    @Override
    public ResponseVO<UserPlatformCoinRecordResponseVO> listUserPlatformCoinRecordPage(UserPlatformCoinRecordRequestVO vo) {
        return ResponseVO.success(userCoinRecordService.listUserPlatformCoinRecordPage(vo));
    }

    @Override
    public ResponseVO<Long> userPlatformCoinRecordPageCount(UserPlatformCoinRecordRequestVO vo) {
        return ResponseVO.success(userCoinRecordService.userPlatformCoinRecordPageCount(vo));
    }

    @Override
    public Page<WinLoseRecalculateWalletVO> winLoseRecalculateMainPage(WinLoseRecalculateReqWalletVO reqWalletVO) {
        return userCoinRecordService.winLoseRecalculateMainPage(reqWalletVO);
    }

}

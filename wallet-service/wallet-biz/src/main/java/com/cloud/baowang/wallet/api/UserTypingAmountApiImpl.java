package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserBasicRequestVO;
import com.cloud.baowang.wallet.api.api.UserTypingAmountApi;
import com.cloud.baowang.wallet.api.vo.user.WalletUserBasicRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserWithdrawRunningWaterVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.GetUserTypingAmountVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserTypingAmountVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserTypingRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserTypingRecordVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.WithdrawRunningWaterAddVO;
import com.cloud.baowang.wallet.service.UserTypingAmountHandleService;
import com.cloud.baowang.wallet.service.UserTypingAmountRecordService;
import com.cloud.baowang.wallet.service.UserTypingAmountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserTypingAmountApiImpl implements UserTypingAmountApi {

    private final UserTypingAmountHandleService userTypingAmountHandleService;

    private final UserTypingAmountService userTypingAmountService;

    private final UserTypingAmountRecordService userTypingAmountRecordService;

    @Override
    public ResponseVO<Object> cleanWithdrawRunningWater(String siteCode,String userAccount) {
        return userTypingAmountHandleService.cleanWithdrawRunningWater(siteCode,userAccount);
    }

    @Override
    public ResponseVO<Object> addWithdrawRunningWater(WithdrawRunningWaterAddVO requestVO) {
        return userTypingAmountHandleService.addWithdrawRunningWater(requestVO);
    }

    @Override
    public UserWithdrawRunningWaterVO getWithdrawRunningWater(WalletUserBasicRequestVO requestVO) {
        return userTypingAmountService.getWithdrawRunningWater(requestVO);
    }

    @Override
    public UserTypingAmountVO getUserTypingAmountByAccount(String siteCode,String userAccount) {
        return userTypingAmountService.getUserTypingAmountByAccount(siteCode,userAccount);
    }

    @Override
    public UserTypingAmountVO getUserTypingAmount(String userAccount,String siteCode) {
        return userTypingAmountService.getUserTypingAmount(userAccount,siteCode);
    }

    @Override
    public ResponseVO<Page<UserTypingRecordVO>> listUserTypingRecordPage(UserTypingRecordRequestVO vo) {
        return ResponseVO.success(userTypingAmountRecordService.listUserTypingRecordPage(vo));
    }

    @Override
    public ResponseVO<Long> userTypingRecordPageCount(UserTypingRecordRequestVO vo) {
        return ResponseVO.success(userTypingAmountRecordService.userTypingRecordPageCount(vo));
    }

    @Override
    public void userTypingAmountCleanZero() {
        userTypingAmountRecordService.userTypingAmountCleanZero();
    }

    @Override
    public List<UserTypingAmountVO> getUserTypingAmountListByAccounts(GetUserTypingAmountVO vo) {
        return userTypingAmountService.getUserTypingAmountListByAccounts(vo.getUserAccountList());
    }

    @Override
    public ResponseVO<Object> cleanActivityRunningWater(String siteCode,String userAccount) {
        return userTypingAmountHandleService.cleanActivityRunningWater(siteCode,userAccount);
    }

    @Override
    public void userTypingAmountCleanZeroByUserId(String userId) {
        userTypingAmountService.userTypingAmountCleanZeroByUserId(userId);
    }
}

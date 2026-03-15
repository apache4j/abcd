package com.cloud.baowang.wallet.api;


import com.cloud.baowang.wallet.api.api.UserWithdrawConfigDetailApi;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigDetailAddOrUpdateVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigDetailQueryVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigDetailResponseVO;
import com.cloud.baowang.wallet.service.UserWithdrawConfigDetailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserWithdrawConfigDetailApiImpl implements UserWithdrawConfigDetailApi {

    private final UserWithdrawConfigDetailService userWithdrawConfigDetailService;
    @Override
    public UserWithdrawConfigDetailResponseVO getUserWithdrawConfigDetail(UserWithdrawConfigDetailQueryVO queryVO) {
        return userWithdrawConfigDetailService.getUserWithdrawConfigDetail(queryVO);
    }

    @Override
    public Integer setUserWithdrawConfigDetail(UserWithdrawConfigDetailAddOrUpdateVO userWithdrawConfigAddVO) {
        return userWithdrawConfigDetailService.setUserWithdrawConfigDetail(userWithdrawConfigAddVO);
    }

    @Override
    public Integer resetUserWithdrawConfigDetail(String userId) {
        return userWithdrawConfigDetailService.resetUserWithdrawConfigDetail(userId);
    }


}

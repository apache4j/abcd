package com.cloud.baowang.wallet.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserWithdrawManualRecordApi;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawManualPageReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawManualRecordPageResVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawManualDetailReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawManualPayReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawManualRecordlDetailVO;
import com.cloud.baowang.wallet.service.UserWithdrawManualRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserWithdrawManualRecordApiImpl implements UserWithdrawManualRecordApi {

    private final UserWithdrawManualRecordService userWithdrawManualRecordService;


    @Override
    public Page<UserWithdrawManualRecordPageResVO> withdrawManualPage(UserWithdrawManualPageReqVO vo) {
        return userWithdrawManualRecordService.withdrawManualPage(vo);
    }

    @Override
    public UserWithdrawManualRecordlDetailVO withdrawManualDetail(UserWithdrawManualDetailReqVO vo) {
        return userWithdrawManualRecordService.withdrawManualDetail(vo);
    }

    @Override
    public ResponseVO<Boolean> withdrawManualPay(UserWithdrawManualPayReqVO vo) {
        return userWithdrawManualRecordService.withdrawManualPay(vo);
    }

    @Override
    public ResponseVO<Long> withdrawalManualRecordPageCount(UserWithdrawManualPageReqVO vo) {
        return ResponseVO.success(userWithdrawManualRecordService.withdrawalManualRecordPageCount(vo));
    }
}

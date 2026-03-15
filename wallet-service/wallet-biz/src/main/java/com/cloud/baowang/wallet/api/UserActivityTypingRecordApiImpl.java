package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserActivityTypingRecordApi;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserActivityTypingRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserTypingRecordVO;
import com.cloud.baowang.wallet.service.UserActivityTypingAmountRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserActivityTypingRecordApiImpl implements UserActivityTypingRecordApi {
    private final UserActivityTypingAmountRecordService userActivityTypingAmountRecordService;
    @Override
    public ResponseVO<Page<UserTypingRecordVO>> listPage(UserActivityTypingRecordRequestVO vo) {
        return ResponseVO.success(userActivityTypingAmountRecordService.listPage(vo)) ;
    }

    @Override
    public ResponseVO<Long> count(UserActivityTypingRecordRequestVO vo) {
        return ResponseVO.success(userActivityTypingAmountRecordService.userTypingRecordPageCount(vo)) ;
    }
}

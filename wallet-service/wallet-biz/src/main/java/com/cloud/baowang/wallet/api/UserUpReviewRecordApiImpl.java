package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserUpReviewRecordApi;
import com.cloud.baowang.wallet.api.vo.fundrecord.GetRecordPageVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.GetRecordResponseResultVO;
import com.cloud.baowang.wallet.service.UserManualUpReviewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserUpReviewRecordApiImpl implements UserUpReviewRecordApi {

    private UserManualUpReviewService userManualUpReviewService;

    @Override
    public Page<GetRecordResponseResultVO> getRecordPage(GetRecordPageVO vo) {
        return userManualUpReviewService.getRecordPage(vo);
    }

    @Override
    public ResponseVO<Long> getTotalCount(GetRecordPageVO vo) {
        return userManualUpReviewService.getTotalCount(vo);
    }
}

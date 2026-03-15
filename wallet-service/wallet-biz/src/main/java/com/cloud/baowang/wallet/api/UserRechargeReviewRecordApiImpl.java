package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserRechargeReviewRecordApi;
import com.cloud.baowang.wallet.api.vo.fundrecord.GetUserRechargeRecordPageVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.GetUserRechargeRecordResponseVO;
import com.cloud.baowang.wallet.service.UserRechargeReviewService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * @className: 会员充值审核记录 服务类
 * @author: wade
 * @description: 会员充值审核记录 服务类
 * @date: 2024/5/24 20:18
 */

@RestController
@AllArgsConstructor
public class UserRechargeReviewRecordApiImpl implements UserRechargeReviewRecordApi {
    private final UserRechargeReviewService userRechargeReviewService;

    @Override
    public Page<GetUserRechargeRecordResponseVO> getRechargeRecordPage(GetUserRechargeRecordPageVO vo) {
        return userRechargeReviewService.getRechargeRecordPage(vo);
    }

    @Override
    public ResponseVO<Long> getTotalCount(GetUserRechargeRecordPageVO vo) {
        return userRechargeReviewService.getTotalCount(vo);
    }
}

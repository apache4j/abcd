package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserRechargeReviewApi;
import com.cloud.baowang.wallet.api.vo.WalletStatusVO;
import com.cloud.baowang.wallet.api.vo.activity.WalletReviewVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.UserRechargeReviewPageVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.UserRechargeReviewResponseVO;
import com.cloud.baowang.wallet.api.vo.userreview.EditAmountVO;
import com.cloud.baowang.wallet.api.vo.userreview.TwoSuccessVO;
import com.cloud.baowang.wallet.service.UserRechargeReviewService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * @className: 会员充值审核 服务类
 * @author: wade
 * @description: 会员充值审核 服务类
 * @date: 2024/5/24 20:18
 */

@RestController
@AllArgsConstructor
public class UserRechargeReviewApiImpl implements UserRechargeReviewApi {

    private UserRechargeReviewService userRechargeReviewService;


    @Override
    public ResponseVO<?> rechargeLock(WalletStatusVO vo, String adminId, String adminName) {
        return userRechargeReviewService.rechargeLock(vo, adminId, adminName);
    }

    @Override
    public ResponseVO<?> oneSuccess(WalletReviewVO vo, String adminId, String adminName) {
        return userRechargeReviewService.oneSuccess(vo, adminId, adminName);
    }

    @Override
    public ResponseVO<?> editAmount(EditAmountVO vo, String adminId, String adminName) {
        return userRechargeReviewService.editAmount(vo, adminId, adminName);
    }

    @Override
    public ResponseVO<?> oneFail(WalletReviewVO vo, String adminId, String adminName) {
        return userRechargeReviewService.oneFail(vo, adminId, adminName);
    }

    @Override
    public Page<UserRechargeReviewResponseVO> getReviewPage(UserRechargeReviewPageVO vo, String adminName) {
        return userRechargeReviewService.getReviewPage(vo, adminName);
    }

    @Override
    public ResponseVO<?> rechargeLock2(WalletStatusVO vo, String adminId, String adminName) {
        return userRechargeReviewService.rechargeLock2(vo, adminId, adminName);
    }

    @Override
    public ResponseVO<?> twoSuccess(TwoSuccessVO vo, String adminId, String adminName) {
        return userRechargeReviewService.twoSuccess(vo, adminId, adminName);
    }

    @Override
    public ResponseVO<?> getNotReviewNum() {
        return ResponseVO.success(userRechargeReviewService.getNotReviewNum());
    }

}

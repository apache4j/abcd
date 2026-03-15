package com.cloud.baowang.wallet.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
import com.cloud.baowang.wallet.api.vo.report.WinLoseRecalculateReqWalletVO;
import com.cloud.baowang.wallet.api.vo.report.WinLoseRecalculateWalletVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.*;
import com.cloud.baowang.wallet.service.UserCoinRecordService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
public class UserCoinRecordApiImpl implements UserCoinRecordApi {

    private final UserCoinRecordService userCoinRecordService;


    @Override
    public ResponseVO<UserCoinRecordResponseVO> listUserCoinRecordPage(UserCoinRecordRequestVO userCoinRecordRequestVO) {
        return ResponseVO.success(userCoinRecordService.listUserCoinRecordPage(userCoinRecordRequestVO));
    }

    @Override
    public ResponseVO<Long> userCoinRecordPageCount(UserCoinRecordRequestVO userCoinRecordRequestVO) {
        return ResponseVO.success(userCoinRecordService.userCoinRecordPageCount(userCoinRecordRequestVO));
    }

    @Override
    public ResponseVO<List<UserCoinRecordVO>> getUserCoinRecords(UserCoinRecordRequestVO userCoinRecordRequestVO) {
        return ResponseVO.success(userCoinRecordService.getUserCoinRecords(userCoinRecordRequestVO));
    }

    @Override
    public Long callFriendRechargeCount(UserCoinRecordCallFriendsRequestVO requestVO) {
        return userCoinRecordService.callFriendRechargeCount(requestVO);
    }

    @Override
    public List<String> getOrderNoByOrders(List<String> orders) {
        return userCoinRecordService.getOrderNoByOrders(orders);
    }

    @Override
    public Page<WinLoseRecalculateWalletVO> winLoseRecalculateMainPage(WinLoseRecalculateReqWalletVO vo) {
        return userCoinRecordService.winLoseRecalculateMainPage(vo);
    }

    @Override
    public UserCoinRecordVO getUserCoinRecord(String remark, String userId, String balanceType) {
        return userCoinRecordService.getUserCoinRecord(remark, userId, balanceType);
    }

    @Override
    public List<UserCoinRecordVO> getUserCoinRecordsForEVO(String orderNo, String userId) {
        return userCoinRecordService.getUserCoinRecordsForEVO(orderNo, userId);
    }

    @Override
    public List<UserCoinRecordVO> getUserCoinRecordPG(String remark, String userId, String balanceType) {
        return userCoinRecordService.getUserCoinRecordPG(remark, userId, balanceType);
    }

    @Override
    public ResponseVO<UserCoinRecordVO> getJDBUserCoinRecords(JDBUserCoinRecordVO jdbUserCoinRecordVO) {
        return ResponseVO.success(userCoinRecordService.getJDBUserCoinRecords(jdbUserCoinRecordVO));
    }

    @Override
    public ResponseVO<List<UserCoinRecordVO>> getJDBBetRecords(JDBUserCoinRecordVO jdbUserCoinRecordVO) {
        return ResponseVO.success(userCoinRecordService.getJDBBetRecords(jdbUserCoinRecordVO));
    }
}

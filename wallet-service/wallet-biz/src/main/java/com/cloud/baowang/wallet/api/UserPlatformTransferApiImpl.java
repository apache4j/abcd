package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentWinLossParamVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserPlatformTransferApi;
import com.cloud.baowang.wallet.api.vo.userwallet.ClientUserPlatformTransferRecordReqVO;
import com.cloud.baowang.wallet.api.vo.userwallet.ClientUserPlatformTransferRespVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserPlatformTransferCondReqVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserPlatformTransferRespVO;
import com.cloud.baowang.wallet.service.UserPlatformTransferRecordService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/10/16 15:19
 * @Version: V1.0
 **/
@RestController
@Validated
@AllArgsConstructor
public class UserPlatformTransferApiImpl implements UserPlatformTransferApi {

    private UserPlatformTransferRecordService userPlatformTransferRecordService;
    @Override
    public ResponseVO<Page<UserPlatformTransferRespVO>> listPage(UserPlatformTransferCondReqVO userPlatformTransferCondReqvO) {
        return ResponseVO.success(userPlatformTransferRecordService.listPage(userPlatformTransferCondReqvO));
    }


    @Override
    public BigDecimal getTransferAmountByUserAccount(String userAccount, String siteCode) {
        return userPlatformTransferRecordService.getTransferAmountByUserAccount(userAccount,siteCode);
    }

    @Override
    public ResponseVO<Page<ClientUserPlatformTransferRespVO>> platformTransferRecord(ClientUserPlatformTransferRecordReqVO userPlatformTransferCondReqvO) {
        return userPlatformTransferRecordService.platformTransferRecord(userPlatformTransferCondReqvO);
    }

    @Override
    public ResponseVO<Boolean> hasPlatformTransferRecord(String userId) {
        return userPlatformTransferRecordService.hasPlatformTransferRecord(userId);
    }

}

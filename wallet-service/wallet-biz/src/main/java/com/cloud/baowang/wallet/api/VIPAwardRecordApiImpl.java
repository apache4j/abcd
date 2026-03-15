package com.cloud.baowang.wallet.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.VIPAwardRecordApi;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserAwardRecordVO;
import com.cloud.baowang.wallet.service.VIPAwardRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author : 小智
 * @Date : 2024/10/12 18:34
 * @Version : 1.0
 */
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class VIPAwardRecordApiImpl implements VIPAwardRecordApi {

    private VIPAwardRecordService vipAwardRecordService;

    @Override
    public ResponseVO<Boolean> recordVIPAward(List<UserAwardRecordVO> userAwardRecordVOList) {
        return ResponseVO.success(vipAwardRecordService.recordVIPAward(userAwardRecordVOList));
    }

    @Override
    public ResponseVO<Boolean> receiveUserAward(String userId) {
        return ResponseVO.success(vipAwardRecordService.receiveUserAward(userId));
    }

    @Override
    public ResponseVO<Boolean> receiveActiveAward(String id) {
        return ResponseVO.success(vipAwardRecordService.receiveActiveAward(id));
    }

    @Override
    public void vipExpired() {
        vipAwardRecordService.vipExpired();
    }
}

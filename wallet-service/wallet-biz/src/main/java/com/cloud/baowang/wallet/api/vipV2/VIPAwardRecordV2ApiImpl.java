package com.cloud.baowang.wallet.api.vipV2;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.VIPAwardRecordApi;
import com.cloud.baowang.wallet.api.api.vipV2.VIPAwardRecordV2Api;
import com.cloud.baowang.wallet.api.vo.activityV2.UserAwardRecordV2ReqVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserAwardRecordVO;
import com.cloud.baowang.wallet.api.vo.vipV2.UserAwardRecordV2VO;
import com.cloud.baowang.wallet.service.VIPAwardRecordService;
import com.cloud.baowang.wallet.service.vipV2.VIPAwardRecordV2Service;
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
public class VIPAwardRecordV2ApiImpl implements VIPAwardRecordV2Api {

    private VIPAwardRecordV2Service vipAwardRecordService;

    @Override
    public ResponseVO<Boolean> recordVIPAward(List<UserAwardRecordV2VO> userAwardRecordVOList) {
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

    @Override
    public ResponseVO<Boolean> awardHasReceive(UserAwardRecordV2ReqVO vo) {
        return vipAwardRecordService.awardHasReceive(vo);
    }

    @Override
    public ResponseVO<UserAwardRecordV2VO> awardRecordByUserId(UserAwardRecordV2ReqVO vo) {
        return vipAwardRecordService.awardRecordByUserId(vo);
    }
    @Override
    public List<UserAwardRecordV2VO> awardRecordByVipGrade(UserAwardRecordV2ReqVO vo) {
        return vipAwardRecordService.awardRecordByVipGrade(vo);
    }
}

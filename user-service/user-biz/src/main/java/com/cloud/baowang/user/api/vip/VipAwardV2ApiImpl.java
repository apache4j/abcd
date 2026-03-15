package com.cloud.baowang.user.api.vip;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.vip.VipAwardV2Api;
import com.cloud.baowang.user.api.vo.vip.VIPSendRewardReqVO;
import com.cloud.baowang.user.service.vipV2.VipRewardsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class VipAwardV2ApiImpl implements VipAwardV2Api {
    private final VipRewardsService vipRewardsService;

    @Override
    public ResponseVO<Boolean> weekAward(@RequestBody VIPSendRewardReqVO vo) {
        return vipRewardsService.autoWeekRedBagRewardHandle(vo);
    }


    @Override
    public ResponseVO<Boolean> birthDayAward(@RequestBody VIPSendRewardReqVO vo) {
        return vipRewardsService.autoBirthRedBagRewardHandle(vo);
    }
}

package com.cloud.baowang.user.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.api.vip.VipMedalRewardReceiveApi;
import com.cloud.baowang.user.service.VipMedalRewardReceiveService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class VipMedalRewardReceiveApiImpl implements VipMedalRewardReceiveApi {
    private final VipMedalRewardReceiveService rewardReceiveService;

    @Override
    public ResponseVO<Boolean> receiveMedal(List<UserInfoVO> userInfoVOS, String siteCode) {
        return rewardReceiveService.receiveMedal(userInfoVOS, siteCode);
    }
}

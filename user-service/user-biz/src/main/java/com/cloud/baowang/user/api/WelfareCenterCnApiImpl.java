package com.cloud.baowang.user.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.WelfareCenterCnApi;
import com.cloud.baowang.user.api.vo.welfarecenter.WelfareCenterRewardPageQueryVO;
import com.cloud.baowang.user.api.vo.welfarecenter.WelfareCenterRewardRespVO;
import com.cloud.baowang.user.api.vo.welfarecenter.WelfareCenterRewardResultVO;
import com.cloud.baowang.user.service.WelfareCenterService;
import com.cloud.baowang.user.service.vipV2.WelfareCenterV2Service;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class WelfareCenterCnApiImpl implements WelfareCenterCnApi {
    private final WelfareCenterV2Service welfareCenterV2Service;

    @Override
    public ResponseVO<WelfareCenterRewardResultVO> pageQuery(WelfareCenterRewardPageQueryVO queryVO) {
        return welfareCenterV2Service.pageQuery(queryVO);
    }

    @Override
    public ResponseVO<WelfareCenterRewardRespVO> detail(WelfareCenterRewardPageQueryVO queryVO) {
        return welfareCenterV2Service.detail(queryVO);
    }

    @Override
    public Integer getWaitReceiveByUserId(String userId) {
        return welfareCenterV2Service.getWaitReceiveByUserId(userId);
    }
}

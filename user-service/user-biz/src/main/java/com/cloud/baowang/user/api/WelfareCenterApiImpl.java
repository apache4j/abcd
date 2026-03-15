package com.cloud.baowang.user.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.WelfareCenterApi;
import com.cloud.baowang.user.api.vo.welfarecenter.WelfareCenterRewardPageQueryVO;
import com.cloud.baowang.user.api.vo.welfarecenter.WelfareCenterRewardRespVO;
import com.cloud.baowang.user.api.vo.welfarecenter.WelfareCenterRewardResultVO;
import com.cloud.baowang.user.service.WelfareCenterService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class WelfareCenterApiImpl implements WelfareCenterApi {
    private final WelfareCenterService welfareCenterService;

    @Override
    public ResponseVO<WelfareCenterRewardResultVO> pageQuery(WelfareCenterRewardPageQueryVO queryVO) {
        return welfareCenterService.pageQuery(queryVO);
    }

    @Override
    public ResponseVO<WelfareCenterRewardRespVO> detail(WelfareCenterRewardPageQueryVO queryVO) {
        return welfareCenterService.detail(queryVO);
    }

    @Override
    public Integer getWaitReceiveByUserId(String userId) {
        return welfareCenterService.getWaitReceiveByUserId(userId);
    }
}

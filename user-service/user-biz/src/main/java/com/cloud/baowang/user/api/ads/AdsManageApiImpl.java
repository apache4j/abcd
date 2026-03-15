package com.cloud.baowang.user.api.ads;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.api.ads.AdsManageApi;
import com.cloud.baowang.user.api.api.notice.UserNoticeApi;
import com.cloud.baowang.user.api.vo.ads.UserRechargeEventVO;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeRespVO;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeHeadReqVO;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeReqVO;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeSetReadStateReqVO;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeTargetAddVO;
import com.cloud.baowang.user.service.AdsService;
import com.cloud.baowang.user.service.UserNoticeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AdsManageApiImpl implements AdsManageApi {
    private final AdsService adsService;


    @Override
    public void onRechargeAdsEventArrive(UserRechargeEventVO userInfoVO) {
        adsService.onRechargeAdsEventArrive(userInfoVO);
    }
}

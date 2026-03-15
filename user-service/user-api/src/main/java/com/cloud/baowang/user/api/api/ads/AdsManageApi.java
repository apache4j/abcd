package com.cloud.baowang.user.api.api.ads;

import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.ads.UserRechargeEventVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(contextId = "remoteAdsManageApi", value = ApiConstants.NAME)
@Tag(name = "广告埋点 服务 - AdsManageApi")
public interface AdsManageApi {

    String PREFIX = ApiConstants.PREFIX + "/adsManageApi/api/";

    @PostMapping(PREFIX + "onRechargeAdsEventArrive")
    @Operation(summary = "充值广告埋点")
    void onRechargeAdsEventArrive(@RequestBody UserRechargeEventVO userInfoVO);
}

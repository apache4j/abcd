package com.cloud.baowang.user.api.api.vip;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "vipMedalRewardReceive", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - vip勋章奖励领取api(独上高楼勋章)")
public interface VipMedalRewardReceiveApi {
    String PREFIX = ApiConstants.PREFIX + "/vipMedal/api/";

    @PostMapping(PREFIX + "receiveMedal")
    @Operation(summary = "领取勋章")
    ResponseVO<Boolean> receiveMedal(@RequestBody List<UserInfoVO> userInfoVOS,
                                     @RequestParam("siteCode") String siteCode);

}

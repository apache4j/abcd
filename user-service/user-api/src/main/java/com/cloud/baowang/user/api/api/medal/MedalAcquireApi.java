package com.cloud.baowang.user.api.api.medal;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireCondReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireCondVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteMedalAcquireApi", value = ApiConstants.NAME)
@Tag(name = "RPC-勋章获取api")
public interface MedalAcquireApi {
    String PREFIX = ApiConstants.PREFIX + "/medalAcquire/api";

    @Operation(summary = "勋章信息列表")
    @PostMapping(value = PREFIX+"/findByMedalCode")
    ResponseVO<SiteMedalInfoRespVO> findByMedalCode(@RequestBody MedalAcquireCondReqVO medalAcquireCondReqVO);

    @Operation(summary = "勋章是否可以被解锁")
    @PostMapping(value = PREFIX+"/canLockMedal")
    ResponseVO<Boolean> canLockMedal(@RequestBody MedalAcquireCondVO medalAcquireCondVO);

    @Operation(summary = "勋章解锁")
    @PostMapping(value = PREFIX+"/unLockMedal")
    ResponseVO<Boolean> unLockMedal(@RequestBody MedalAcquireReqVO medalAcquireReqVO);

}

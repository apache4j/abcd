package com.cloud.baowang.wallet.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpRecordPageVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpRecordResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteUserPlatformCoinManualUpRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员平台币上分记录 服务")
public interface UserPlatformCoinManualUpRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/userPlatformCoinManualUpRecord/api/";

    @Operation(summary = "分页列表")
    @PostMapping(value = PREFIX + "getUpRecordPage")
    UserPlatformCoinManualUpRecordResult getUpRecordPage(@RequestBody UserPlatformCoinManualUpRecordPageVO vo);

    @Operation(summary = "总记录数")
    @PostMapping(value = PREFIX + "getUpRecordPageCount")
    ResponseVO<Long> getUpRecordPageCount(@RequestBody UserPlatformCoinManualUpRecordPageVO vo);

}

package com.cloud.baowang.wallet.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteUserActivityTypingRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员打码量 服务")
public interface UserActivityTypingRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/userActivityTypingRecord/api";


    @Operation(summary = "获取会员打码量记录" )
    @PostMapping(value = PREFIX+ "/listPage")
    ResponseVO<Page<UserTypingRecordVO>> listPage(@RequestBody UserActivityTypingRecordRequestVO vo);


    @Operation(summary = "获取会员打码量记录计数" )
    @PostMapping(value = PREFIX+ "/count")
    ResponseVO<Long> count(@RequestBody UserActivityTypingRecordRequestVO vo);


}

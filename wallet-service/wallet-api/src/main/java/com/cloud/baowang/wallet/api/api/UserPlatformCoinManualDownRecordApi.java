package com.cloud.baowang.wallet.api.api;


import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualDownRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualDownRecordResponseVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteUserPlatformCoinManualDownRecordApi",value = ApiConstants.NAME)
@Tag(name = "RPC 会员平台币下分记录服务 - UserPlatformCoinManualDownRecord")
public interface UserPlatformCoinManualDownRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/UserPlatformCoinManualDownRecord/api/";


    @Operation(summary ="会员平台币下分记录")
    @PostMapping(value = "/listUserPlatformCoinUserManualDownRecordPage")
    public ResponseVO<UserPlatformCoinManualDownRecordResponseVO> listPlatformCoinUserManualDownRecordPage(@RequestBody UserPlatformCoinManualDownRecordRequestVO userCoinRecordRequestVO);

    @Operation(summary ="会员平台币下分记录计数")
    @PostMapping(value = "/listUserPlatformCoinManualDownRecordPageExportCount")
    ResponseVO<Long> listUserPlatformCoinManualDownRecordPageExportCount(@RequestBody UserPlatformCoinManualDownRecordRequestVO vo);


    @Operation(summary ="会员平台币上分待处理计数")
    @PostMapping(value = "/getUpRecordTodoCount")
    Long getUpRecordTodoCount(@RequestBody UserPlatformCoinManualUpRecordVO vo);


}

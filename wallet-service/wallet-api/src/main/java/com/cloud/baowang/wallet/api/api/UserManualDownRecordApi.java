package com.cloud.baowang.wallet.api.api;


import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownRecordResponseVO;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownSubmitVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteUserManualDownApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - UserManualDown")
public interface UserManualDownRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/userManualDownRecord/api/";

    @PostMapping(value = PREFIX + "saveManualDown")
    @Operation(summary = "会员人工减额提交")
    ResponseVO<Boolean> saveManualDown(@RequestBody UserManualDownSubmitVO vo) ;

    @Operation(summary ="会员人工扣除记录")
    @PostMapping(value = "/listUserManualDownRecordPage")
    public ResponseVO<UserManualDownRecordResponseVO> listUserManualDownRecordPage(@RequestBody UserManualDownRecordRequestVO userCoinRecordRequestVO);

    @Operation(summary ="会员人工扣除记录计数")
    @PostMapping(value = "/listUserManualDownRecordPageExportCount")
    ResponseVO<Long> listUserManualDownRecordPageExportCount(@RequestBody UserManualDownRecordRequestVO vo);


    @Operation(summary = "统计当前站点下,全部会员人工加额待审核记录数")
    @GetMapping(PREFIX+"getTotalPendingReviewBySiteCode")
    long getTotalPendingReviewBySiteCode(@RequestParam("siteCode") String siteCode);


}

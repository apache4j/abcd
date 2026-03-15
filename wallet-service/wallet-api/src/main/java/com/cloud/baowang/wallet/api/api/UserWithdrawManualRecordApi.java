package com.cloud.baowang.wallet.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawManualPageReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawManualRecordPageResVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawManualDetailReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawManualPayReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawManualRecordlDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteUserWithdrawManualRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员人工出款记录 服务")
public interface UserWithdrawManualRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/userWithdrawManualRecord/api/";

    @Operation(summary = "会员人工出款分页列表")
    @PostMapping(value = PREFIX + "withdrawManualPage")
    Page<UserWithdrawManualRecordPageResVO> withdrawManualPage(@RequestBody UserWithdrawManualPageReqVO vo);


    @Operation(summary = "会员人工出款详情")
    @PostMapping(value = PREFIX + "withdrawManualDetail")
    UserWithdrawManualRecordlDetailVO withdrawManualDetail(@RequestBody UserWithdrawManualDetailReqVO vo);


    @Operation(summary = "会员人工出款详情")
    @PostMapping(value = PREFIX + "withdrawManualPay")
    ResponseVO<Boolean> withdrawManualPay(@RequestBody UserWithdrawManualPayReqVO vo);
    @Operation(summary = "会员人工款记录条数统计")
    @PostMapping(value = PREFIX + "withdrawalManualRecordPageCount")
    ResponseVO<Long> withdrawalManualRecordPageCount(@RequestBody UserWithdrawManualPageReqVO vo);
}

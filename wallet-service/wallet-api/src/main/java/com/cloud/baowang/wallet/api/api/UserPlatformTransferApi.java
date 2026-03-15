package com.cloud.baowang.wallet.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.userwallet.ClientUserPlatformTransferRecordReqVO;
import com.cloud.baowang.wallet.api.vo.userwallet.ClientUserPlatformTransferRespVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserPlatformTransferCondReqVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserPlatformTransferRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(contextId = "remoteUserPlatformTransferApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - userPlatformTransfer" )
public interface UserPlatformTransferApi {

    String PREFIX = ApiConstants.PREFIX + "/userPlatformTransfer/api/";


    @PostMapping(value = PREFIX + "listPage")
    @Operation(summary = "平台币兑换记录")
    ResponseVO<Page<UserPlatformTransferRespVO>> listPage(@RequestBody UserPlatformTransferCondReqVO userPlatformTransferCondReqvO);


    @PostMapping(value = PREFIX + "getTransferAmountByUserAccount")
    @Operation(summary = "获取会员转换金额")
    BigDecimal getTransferAmountByUserAccount(@RequestParam("userAccount") String userAccount,@RequestParam("siteCode")String siteCode);


    @PostMapping(value = PREFIX + "platformTransferRecord")
    @Operation(summary = "客户端-平台币兑换记录")
    ResponseVO<Page<ClientUserPlatformTransferRespVO>> platformTransferRecord(@RequestBody ClientUserPlatformTransferRecordReqVO userPlatformTransferCondReqvO);

    @PostMapping(value = PREFIX + "hasPlatformTransferRecord")
    @Operation(summary = "客户端-平台币兑换记录是否兑换过平台币")
    ResponseVO<Boolean> hasPlatformTransferRecord(@RequestParam("userId") String userId);
}

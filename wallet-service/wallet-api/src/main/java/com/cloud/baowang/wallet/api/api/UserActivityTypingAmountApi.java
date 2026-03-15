package com.cloud.baowang.wallet.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.activity.UserActivityTypingAmountResp;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserActivityTypingAmountVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserActivityTypingChangeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

@FeignClient(contextId = "remoteUserActivityTypingAmountApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员打码量 服务")
public interface UserActivityTypingAmountApi {

    String PREFIX = ApiConstants.PREFIX + "/userActivityTypingAmount/api";


    @Operation(summary = "初始化活动打码量" )
    @PostMapping(value = PREFIX+ "/initUserActivityTypingAmountLimit")
    ResponseVO<Boolean> initUserActivityTypingAmountLimit(@RequestBody /*@Validated*/ UserActivityTypingAmountVO vo);



    @Operation(summary = "获得活动打码量(vo里面只要siteCode,userId)" )
    @PostMapping(value = PREFIX+ "/getUserActivityTypingAmount")
    ResponseVO<BigDecimal> getUserActivityTypingAmount(@RequestBody WalletUserInfoVO vo);


    @Operation(summary = "校验用户是否配置了游戏大类" )
    @PostMapping(value = PREFIX+ "/checkUserActivityTypingLimit")
    Boolean checkUserActivityTypingLimit(@RequestBody WalletUserInfoVO vo);


    @Operation(summary = "获取用户是否配置，userId与siteCode" )
    @PostMapping(value = PREFIX+ "/getUserActivityTypingLimit")
    UserActivityTypingAmountResp getUserActivityTypingLimit(@RequestBody WalletUserInfoVO vo);


    @Operation(summary = "更新用户存款活动打码量" )
    @PostMapping(value = PREFIX+ "/updateUserActivityInfo")
    ResponseVO<Boolean> updateUserActivityInfo(@RequestBody @Validated UserActivityTypingChangeVO vo);


    @Operation(summary = "添加用户存款活动打码量" )
    @PostMapping(value = PREFIX+ "/addUserActivityInfo")
    ResponseVO<Boolean> addUserActivityInfo(@RequestBody @Validated UserActivityTypingChangeVO vo);


}

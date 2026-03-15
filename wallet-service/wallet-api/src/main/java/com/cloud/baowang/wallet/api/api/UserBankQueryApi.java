package com.cloud.baowang.wallet.api.api;


import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.user.WalletUserBasicRequestVO;
import com.cloud.baowang.wallet.api.vo.uservirtualcurrency.UserDepositWithdrawalResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "remoteUserUserBankQueryApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - userBankCardManage")
public interface UserBankQueryApi {

    String PREFIX = ApiConstants.PREFIX + "/userBank/api/";

    @Operation(summary = "会员详情银行卡分页列表")
    @PostMapping(value = PREFIX + "queryBankCardInfo")
    ResponseVO<List<UserDepositWithdrawalResponseVO>> queryBankCardInfo(@RequestBody WalletUserBasicRequestVO requestVO);

    @Operation(summary = "会员详情虚拟币分页列表")
    @PostMapping(value = PREFIX + "queryVirtualInfo")
    ResponseVO<List<UserDepositWithdrawalResponseVO>> queryVirtualInfo(@RequestBody WalletUserBasicRequestVO requestVO);


    @Operation(summary = "会员详情虚拟币分页列表")
    @PostMapping(value = PREFIX + "queryWalletInfo")
    ResponseVO<List<UserDepositWithdrawalResponseVO>> queryWalletInfo(@RequestBody WalletUserBasicRequestVO requestVO);




}

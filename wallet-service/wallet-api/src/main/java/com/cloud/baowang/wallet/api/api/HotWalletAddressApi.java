package com.cloud.baowang.wallet.api.api;


import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.recharge.GenHotWalletAddressReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.HotWalletAddressVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.AgentHotWalletAddressRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.AgentHotWalletAddressResponseVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.BatchCollectVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.SingleCollectVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserHotWalletAddressRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserHotWalletAddressResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteHotWalletAddressApi", value = ApiConstants.NAME)
@Tag(name = "RPC 热钱包地址 服务")
public interface HotWalletAddressApi {


    String PREFIX = ApiConstants.PREFIX + "/hotWalletAddress/api/";

    @PostMapping(value = PREFIX + "getHotWalletAddress")
    @Operation(summary = "获取热钱包地址")
    ResponseVO<String> getHotWalletAddress(@RequestBody GenHotWalletAddressReqVO vo);


    @PostMapping(value = PREFIX + "queryHotwalletAddress")
    @Operation(summary = "根据地址查询热钱包信息")
    HotWalletAddressVO queryHotWalletAddress(@RequestParam("address") String address);

    @PostMapping(value = PREFIX + "queryHotWalletAddressByUserId")
    @Operation(summary = "根据用户查询热钱包信息")
    List<HotWalletAddressVO> queryHotWalletAddressByUserId(@RequestParam("userId") String userId);

    @PostMapping(value = PREFIX + "listUserHotAddress")
    @Operation(summary = "会员热钱包地址列表")
    ResponseVO<UserHotWalletAddressResponseVO> listUserHotAddress(@RequestBody UserHotWalletAddressRequestVO userHotWalletAddressRequestVO);

    @PostMapping(value = PREFIX + "userHotWalletAddressPageCount")
    @Operation(summary = "会员热钱包地址分页计数")
    ResponseVO<Long> userHotWalletAddressPageCount(@RequestBody UserHotWalletAddressRequestVO vo);

    @PostMapping(value = PREFIX + "listAgentHotAddress")
    @Operation(summary = "代理热钱包地址列表")
    ResponseVO<AgentHotWalletAddressResponseVO> listAgentHotAddress(@RequestBody AgentHotWalletAddressRequestVO userHotWalletAddressRequestVO);

    @PostMapping(value = PREFIX + "agentHotWalletAddressPageCount")
    @Operation(summary = "代理热钱包地址分页计数")
    ResponseVO<Long> agentHotWalletAddressPageCount(@RequestBody AgentHotWalletAddressRequestVO vo);

    @PostMapping(value = PREFIX + "queryHotWalletAddressByOutAddressNo")
    @Operation(summary = "查询热钱包地址")
    HotWalletAddressVO queryHotWalletAddressByOutAddressNo(@RequestParam("outAddressNo") String outAddressNo);

    @PostMapping(value = PREFIX + "singleCollect")
    @Operation(summary = "单个归集")
    ResponseVO<Void> singleCollect(@RequestBody SingleCollectVO singleCollectVO);

    @PostMapping(value = PREFIX + "batchCollect")
    @Operation(summary = "批量归集")
    ResponseVO<Void> batchCollect(@RequestBody BatchCollectVO batchCollectVO);
}

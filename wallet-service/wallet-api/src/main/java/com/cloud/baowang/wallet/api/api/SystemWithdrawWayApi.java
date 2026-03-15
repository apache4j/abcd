package com.cloud.baowang.wallet.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.IdReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayRespVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayFeeVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayAddVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayDetailResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayStatusVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayUpdateVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawConfigRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawConfigVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawTypeListVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawWayListVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawWayRequestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(contextId = "remoteSystemWithdrawWayApi",value = ApiConstants.NAME)
@Tag(name = "RPC服务-提现方式信息")
public interface SystemWithdrawWayApi {

    String PREFIX = ApiConstants.PREFIX + "/withdrawWay/api/";


    @PostMapping(value = PREFIX + "selectPage")
    @Operation(summary = "提现方式分页查询")
    ResponseVO<Page<SystemWithdrawWayResponseVO>> selectPage(@RequestBody SystemWithdrawWayRequestVO withdrawWayRequestVO);


    @PostMapping(value = PREFIX + "insert")
    @Operation(summary = "提现方式新增")
    ResponseVO<Void> insert(@RequestBody SystemWithdrawWayAddVO withdrawWayAddVO);

    @PostMapping(value = PREFIX + "update")
    @Operation(summary = "提现方式修改")
    ResponseVO<Void> update(@RequestBody SystemWithdrawWayUpdateVO withdrawWayUpdateVO);


    @PostMapping(value = PREFIX + "enableOrDisable")
    @Operation(summary = "提现方式启用禁用")
    ResponseVO<Void> enableOrDisable(@RequestBody SystemWithdrawWayStatusVO withdrawWayStatusVO);

    @PostMapping(value = PREFIX + "selectAllValid")
    @Operation(summary = "所有有效提现方式列表")
    ResponseVO<List<SystemWithdrawWayResponseVO>> selectAllValid();

    @PostMapping(value = PREFIX + "selectAll")
    @Operation(summary = "所有提现方式列表")
    ResponseVO<List<SystemWithdrawWayResponseVO>> selectAll();

    @PostMapping(value = PREFIX + "queryWithdrawWayList")
    @Operation(summary = "新增站点查询提款方式集合")
    List<SystemWithdrawWayResponseVO> queryWithdrawWayList();

    @PostMapping(value = PREFIX + "withdrawWayList")
    @Operation(summary = "客户端-提现方式列表")
    List<WithdrawWayListVO> withdrawWayList(@RequestBody WithdrawWayRequestVO withdrawWayRequestVO);


    @PostMapping(value = PREFIX + "agentwithdrawWayList")
    @Operation(summary = "代理端-提现方式列表")
    List<WithdrawWayListVO> agentWithdrawWayList(@RequestBody WithdrawWayRequestVO withdrawWayRequestVO);

    @PostMapping(value = PREFIX + "info")
    @Operation(summary = "提现方式详情")
    ResponseVO<SystemWithdrawWayDetailResponseVO> info(@RequestBody IdReqVO idReqVO);


    @PostMapping(value = PREFIX + "getInfoById")
    @Operation(summary = "提现方式详情")
    ResponseVO<SystemWithdrawWayDetailResponseVO> getInfoById(@RequestBody IdReqVO idReqVO);

    @PostMapping(value = PREFIX + "selectBySort")
    @Operation(summary = "提现方式式排序列表")
    ResponseVO<List<SystemWithdrawWayResponseVO>> selectBySort(@RequestBody SystemWithdrawWayRequestVO systemWithdrawWayRequestVO);


    @PostMapping(value = PREFIX + "batchSave")
    @Operation(summary = "提现方式式批量保存")
    ResponseVO<Boolean> batchSave(@RequestParam("userAccount") String userAccount, @RequestBody List<SortNewReqVO> sortNewReqVOS);


    @PostMapping(value = PREFIX + "calculateSiteWithdrawWayFeeRate")
    @Operation(summary = "计算提现方式手续费")
    SiteWithdrawWayFeeVO calculateSiteWithdrawWayFeeRate(@RequestParam("siteCode")String siteCode, @RequestParam("withdrawWayId")String withdrawWayId,
                                                         @RequestParam("amount")BigDecimal amount);

    @PostMapping(value = PREFIX + "getChannelGroup")
    @Operation(summary = "根据站点编码 获取提款通道分组信息")
    Map<String, List<SystemWithdrawChannelResponseVO>>  getChannelGroup(@RequestParam("siteCode")String siteCode,@RequestParam("vipRank")String vipRank);
}

package com.cloud.baowang.wallet.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.agent.GetDepositStatisticsByAgentIdVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositAmountReqVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositAmountVO;
import com.cloud.baowang.wallet.api.vo.report.DepositWithdrawAllRecordVO;
import com.cloud.baowang.wallet.api.vo.site.GetAllArriveAmountBySiteCodeResponseVO;
import com.cloud.baowang.wallet.api.vo.site.GetDepositStatisticsBySiteCodeVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserDepositWithdrawalResVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "remoteUserDepositWithdrawApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员存取款记录 服务")
public interface UserDepositWithdrawApi {

    String PREFIX = ApiConstants.PREFIX + "/userDepositWithdraw/api/";

    @Operation(summary = "代理客户端-存取款金额 按天统计")
    @PostMapping(value = PREFIX + "getDepositStatisticsByAgentId")
    ResponseVO<List<GetDepositStatisticsByAgentIdVO>> getDepositStatisticsByAgentId(@RequestParam("siteCode") String siteCode,
                                                                                    @RequestParam("start") Long start,
                                                                                    @RequestParam("end") Long end,
                                                                                    @RequestParam("agentId") String agentId,
                                                                                    @RequestParam("type") Integer type,
                                                                                    @RequestParam("dbZone") String dbZone,
                                                                                    @RequestParam("currencyCode") String currencyCode
    );

    @Operation(summary = "代理客户端-存取款金额 按天统计")
    @PostMapping(value = PREFIX + "getDepositStatisticsBySiteCode")
    ResponseVO<List<GetDepositStatisticsBySiteCodeVO>> getDepositStatisticsBySiteCode(@RequestParam("siteCode") String siteCode,
                                                                                      @RequestParam("start") Long start,
                                                                                      @RequestParam("end") Long end,
                                                                                      @RequestParam("type") Integer type,
                                                                                      @RequestParam("dbZone") String dbZone,
                                                                                      @RequestParam("currencyCode") String currencyCode
    );

    @Operation(summary = "存取款人数 按天统计")
    @PostMapping(value = PREFIX + "getDepositWithdrawnUserCountBySiteCode")
    ResponseVO<List<GetDepositStatisticsBySiteCodeVO>> getDepositWithdrawnUserCountBySiteCode(@RequestParam("siteCode") String siteCode,
                                                                                      @RequestParam("start") Long start,
                                                                                      @RequestParam("end") Long end,
                                                                                      @RequestParam("type") Integer type,
                                                                                      @RequestParam("dbZone") String dbZone, @RequestParam(value = "currencyCode", required = false) String currencyCode
    );


    @Operation(summary = "代理客户端-查询某代理下 某个会员的充值提款总额")
    @PostMapping(value = PREFIX + "getAllArriveAmountByAgentUser")
    GetAllArriveAmountByAgentUserResponseVO getAllArriveAmountByAgentUser(@RequestParam("siteCode") String siteCode,
                                                                          @RequestParam("agentAccount") String agentAccount,
                                                                          @RequestParam("userAccount") String userAccount,
                                                                          @RequestParam("start") Long start,
                                                                          @RequestParam("end") Long end,
                                                                          @RequestParam("type") Integer type);


    @Operation(summary = " 查询所有会员充值提款总额")
    @PostMapping(value = PREFIX + "getAllArriveAmountBySiteCode")
    List<GetAllArriveAmountBySiteCodeResponseVO> getAllArriveAmountBySiteCode(@RequestParam("siteCode") String siteCode,
                                                                              @RequestParam("start") Long start,
                                                                              @RequestParam("end") Long end);


    @Operation(summary = "查询某个代理的充值总额(按照会员分组)")
    @PostMapping(PREFIX + "getAllArriveAmountByAgentId")
    List<GetAllArriveAmountByAgentIdVO> getAllArriveAmountByAgentId(@RequestParam("siteCode") String siteCode,
                                                                    @RequestParam("agentAccount") String agentAccount,
                                                                    @RequestParam("start") Long start,
                                                                    @RequestParam("end") Long end);

    @Operation(summary = "查询某个代理的提款总额(按照会员分组)")
    @PostMapping("/getAllWithdrawAmountByAgentId")
    List<GetAllWithdrawAmountByAgentIdVO> getAllWithdrawAmountByAgentId(@RequestParam("siteCode") String siteCode,
                                                                        @RequestParam("agentAccount") String agentAccount,
                                                                        @RequestParam("start") Long start,
                                                                        @RequestParam("end") Long end);



    @Operation(summary = "统计某个时间内所有操作成功的存取款信息")
    @PostMapping("/selectGroupByTime")
    Map<String, Map<String, List<UserDepositWithdrawalRecordVO>>> selectGroupByTime(
            @RequestParam("startTime") Long startTime,
            @RequestParam("endTime") Long endTime,
            @RequestParam(value = "siteCode", required = false) String siteCode);

    @Operation(summary = "根据提款类型,提款账号统计会员提款列表")
    @PostMapping(PREFIX + "getListByBankNoAndSiteCode")
    List<UserDepositWithdrawalResVO> getListByBankNoAndSiteCode(@RequestParam("withdrawTypeCode") String withdrawTypeCode,
                                                                @RequestParam("riskControlAccount") String riskControlAccount,
                                                                @RequestParam(value = "wayId", required = false) String wayId,
                                                                @RequestParam("siteCode") String siteCode);

    @Operation(summary = "获取时间段，站点所有的存取款记录")
    @PostMapping(PREFIX + "getAllDepositWithdrawRecord")
    List<DepositWithdrawAllRecordVO> getAllDepositWithdrawRecord(@RequestParam("startTime") Long startTime,
                                                                 @RequestParam("endTime") Long endTime,
                                                                 @RequestParam("siteCodes") List<String> siteCodes);

    @Operation(summary = "获取时间段、某个站点所有的存取款手续费")
    @PostMapping(PREFIX + "findDepositWithdrawPage")
    Page<UserDepositWithdrawalResVO> findDepositWithdrawPage(@RequestBody UserDepositWithdrawPageReqVO userDepositWithdrawPageReqVO);


    @Operation(summary = "获取时间段、会员ids集合 存款累计")
    @PostMapping(PREFIX + "queryDepositAmountByUserIds")
    List<UserDepositAmountVO> queryDepositAmountByUserIds(@RequestBody UserDepositAmountReqVO vo);


}

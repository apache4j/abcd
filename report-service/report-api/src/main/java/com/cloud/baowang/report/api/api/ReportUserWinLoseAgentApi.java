package com.cloud.baowang.report.api.api;


import com.cloud.baowang.report.api.enums.ApiConstants;
import com.cloud.baowang.report.api.vo.agent.GetWinLoseStatisticsByAgentIdVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentWinLossParamVO;
import com.cloud.baowang.report.api.vo.site.GetWinLoseStatisticsBySiteCodeVO;
import com.cloud.baowang.report.api.vo.userwinlose.ReportUserBetAmountSumVO;
import com.cloud.baowang.report.api.vo.userwinlose.ReportUserWinLossParamVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteReportUserWinLoseAgentApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员盈利-代理统计 服务")
public interface ReportUserWinLoseAgentApi {

    String PREFIX = ApiConstants.PREFIX + "/reportUserWinLoseAgent/api/";

    @Operation(summary = "代理客户端-总输赢 按天统计")
    @PostMapping(value = PREFIX + "getWinLoseStatisticsByAgentId")
    List<GetWinLoseStatisticsByAgentIdVO> getWinLoseStatisticsByAgentId(@RequestParam("start") Long start,
                                                                        @RequestParam("end") Long end,
                                                                        @RequestParam("agentAccount") String agentAccount,
                                                                        @RequestParam("dbZone") String dbZone,
                                                                        @RequestParam("currencyCode") String currencyCode
    );

    @Operation(summary = "代理客户端-总输赢 按天统计")
    @PostMapping(value = PREFIX + "getWinLoseStatisticsBySiteCode")
    List<GetWinLoseStatisticsBySiteCodeVO> getWinLoseStatisticsBySiteCode(@RequestParam("start") Long start,
                                                                          @RequestParam("end") Long end,
                                                                          @RequestParam("siteCode") String siteCode,
                                                                          @RequestParam("dbZone") String dbZone,
                                                                          @RequestParam(value = "currencyCode",required = false) String currencyCode
    );


    @Operation(summary = "代理客户端-总输赢 按天统计")
    @PostMapping(value = PREFIX + "getWinLoseStatisticsBySiteCodeHour")
    List<GetWinLoseStatisticsBySiteCodeVO> getWinLoseStatisticsBySiteCodeHour(@RequestParam("start") Long start,
                                                                              @RequestParam("end") Long end,
                                                                              @RequestParam("siteCode") String siteCode,
                                                                              @RequestParam("dbZone") String dbZone,
                                                                              @RequestParam(value = "currencyCode",required = false) String currencyCode
    );

    @Operation(summary = "代理客户端-总输赢 按天统计")
    @PostMapping(value = PREFIX + "getWinLoseStatisticsBySiteCodeMonth")
    List<GetWinLoseStatisticsBySiteCodeVO> getWinLoseStatisticsBySiteCodeMonth(@RequestParam("start") Long start,
                                                                               @RequestParam("end") Long end,
                                                                               @RequestParam("siteCode") String siteCode,
                                                                               @RequestParam("dbZone") String dbZone,
                                                                               @RequestParam(value = "currencyCode",required = false) String currencyCode
    );

    @Operation(summary = "代理客户端-总输赢 按天统计")
    @PostMapping(value = PREFIX + "getWinLoseAndProfitAndLossStatisticsBySiteCode")
    List<GetWinLoseStatisticsBySiteCodeVO> getWinLoseAndProfitAndLossStatisticsBySiteCode(@RequestParam("start") Long start,
                                                                                          @RequestParam("end") Long end,
                                                                                          @RequestParam("siteCode") String siteCode);



    @Operation(summary = "佣金报表-总输赢 按照时间范围统计")
    @PostMapping(value = PREFIX + "getWinLoseStatisticsByAgentIds")
    List<ReportUserBetAmountSumVO> getWinLoseStatisticsByAgentIds(@RequestBody ReportAgentWinLossParamVO paramVO);



    @Operation(summary = "佣金报表-总输赢 按照时间范围统计")
    @PostMapping(value = PREFIX + "getUserOrderAmountByUserId")
    List<ReportUserBetAmountSumVO> getUserOrderAmountByUserId(@RequestBody ReportUserWinLossParamVO paramVO);

}
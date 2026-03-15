package com.cloud.baowang.report.api.api;


import com.cloud.baowang.report.api.enums.ApiConstants;
import com.cloud.baowang.report.api.vo.ActiveByAgentVO;
import com.cloud.baowang.report.api.vo.ReportAgentActiveVO;
import com.cloud.baowang.report.api.vo.GetBetNumberByAgentIdVO;
import com.cloud.baowang.report.api.vo.agent.*;
import com.cloud.baowang.report.api.vo.user.ReportUserBetsVO;
import com.cloud.baowang.report.api.vo.user.base.ReportRecalculateVO;
import com.cloud.baowang.report.api.vo.userwinlose.DailyWinLoseResponseVO;
import com.cloud.baowang.report.api.vo.userwinlose.DailyWinLoseVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLossAmountParamVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLossAmountReportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteReportUserWinLoseApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 会员盈亏")
public interface ReportUserWinLoseApi {
    String PREFIX = ApiConstants.PREFIX + "/reportUserWinLose/api/";

    @PostMapping(value = PREFIX + "getTeamOrderInfo")
    @Operation(summary = "团队信息")
    ReportAgentTeamVO getTeamOrderInfo(@RequestBody ReportAgentUserWinLossVO vo);

    @PostMapping(value = PREFIX + "getUserBetsInfo")
    @Operation(summary = "会员投注信息")
    ReportUserBetsVO getUserBetsInfo(@RequestParam("userAccount") String userAccount, @RequestParam("siteCode") String siteCode);

    @PostMapping(value = PREFIX + "getUserWinLoseByAgent")
    @Operation(summary = "查询代理下会员输赢总计")
    List<ReportAgentSubLineResVO> getUserWinLoseByAgent(@RequestBody ReportAgentSubLineReqVO reqVO);

    @PostMapping(value = PREFIX + "getActiveInfoByAgent")
    @Operation(summary = "最新获取活跃")
    ActiveByAgentVO getActiveInfoByAgent(@RequestBody ReportAgentUserTeamParam param);

    @Operation(summary = "代理H5客户端-会员管理 本月投注总人数")
    @PostMapping(PREFIX + "getBetNumberByAgentId")
    List<GetBetNumberByAgentIdVO> getBetNumberByAgentId(@RequestParam("siteCode") String siteCode,
                                                        @RequestParam("start") Long start,
                                                        @RequestParam("end") Long end,
                                                        @RequestParam("agentId") String agentId,
                                                        @RequestParam(value = "userId", required = false) String userId);

    @Operation(summary = "代理H5客户端-会员管理 本月投注总人数")
    @PostMapping(PREFIX + "getAgentAddActiveInfo")
    ReportAgentActiveVO getAgentActiveInfo(@RequestBody ReportAgentUserTeamParam param);

    @Operation(summary = "查询代理所有下级盈亏数据")
    @PostMapping(PREFIX + "getUserWinLossByAgentIds")
    List<ReportAgentWinLoseVO> getUserWinLossByAgentIds(@RequestBody ReportAgentWinLossParamVO paramVO);

    @Operation(summary = "会员盈亏重算")
    @PostMapping(PREFIX + "addReportWinLoseRecord")
    void addReportWinLoseRecord(@RequestBody ReportRecalculateVO reportRecalculateVO);


    @Operation(summary = "查询会盈亏统计数据")
    @PostMapping(PREFIX + "queryUserOrderAmountByAgent")
    List<UserWinLossAmountReportVO> queryUserOrderAmountByAgent(@RequestBody UserWinLossAmountParamVO vo);

    @Operation(summary = "查询会盈亏统计数据")
    @PostMapping(PREFIX + "dailyWinLoseCurrency")
    List<DailyWinLoseResponseVO> dailyWinLoseCurrency(@RequestBody DailyWinLoseVO vo);
}

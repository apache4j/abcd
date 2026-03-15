package com.cloud.baowang.report.api.api;



import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.enums.ApiConstants;
import com.cloud.baowang.report.api.vo.ReportUserRechargeRequestVO;
import com.cloud.baowang.report.api.vo.ReportUserRechargeResponseVO;
import com.cloud.baowang.report.api.vo.ReportUserRechargeUserRequestVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentWinLossParamVO;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.*;
import com.cloud.baowang.report.api.vo.userwinlose.ReportUserWinLossParamVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(contextId = "remoteReportUserRechargeApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员存款累计服务")
public interface ReportUserRechargeApi {

    String PREFIX = ApiConstants.PREFIX + "/reportUserRecharge/api/";



    @PostMapping(value = PREFIX + "queryRechargeAmount")
    @Operation(summary = "查询会员充值累计-根据站点编码，会员账号，日期")
    ResponseVO<Page<ReportUserRechargeResponseVO>> queryRechargeAmount(@RequestBody ReportUserRechargeRequestVO vo);


    @PostMapping(value = PREFIX + "queryRechargeAmountByUserId")
    @Operation(summary = "查询会员充值累计-根据会员ID")
    ResponseVO<ReportUserRechargeResponseVO> queryRechargeAmountByUserId(@RequestBody ReportUserRechargeUserRequestVO vo);

    @PostMapping(value = PREFIX + "queryByTimeAndAgent")
    @Operation(summary = "查询时间范围内代理下会员存提总计")
    ResponseVO<List<ReportRechargeAgentVO>> queryByTimeAndAgent(@RequestBody ReportUserRechargeAgentReqVO vo);

    @PostMapping(value = PREFIX + "queryPayMethodByTimeAndAgent")
    @Operation(summary = "根据支付方式查询时间范围内代理下会员存提总计")
    ResponseVO<List<ReportUserRechargePayMethodAgentVO>> queryPayMethodByTimeAndAgent(@RequestBody ReportUserRechargePayMethodAgentReqVO vo);


    @PostMapping(value = PREFIX + "reportRealTimeUserRechargeWithdraw")
    @Operation(summary = "会员存取实时报表重算")
    void reportRealTimeUserRechargeWithdraw(@RequestBody ReportRealTimeUserDepositWithdrawReqParam param);

    @PostMapping(value = PREFIX + "getUserDepAmountByAgentIds")
    @Operation(summary = "查询代理下会员的总存提款")
    List<ReportUserAmountVO> getUserDepAmountByAgentIds(@RequestBody ReportAgentWinLossParamVO vo);

    @PostMapping(value = PREFIX + "getUserDepAmountByUserId")
    @Operation(summary = "查询会员的总存提款")
    List<ReportUserAmountVO> getUserDepAmountByUserId(@RequestBody ReportUserWinLossParamVO vo);

    @PostMapping(value = PREFIX + "getUserDepAmountByUserIds")
    @Operation(summary = "查询会员的总存提款-会员分组")
    List<ReportUserAmountVO> getUserDepAmountByUserIds(@RequestBody ReportUserWinLossParamVO vo);

    @PostMapping(value = PREFIX + "getUserFeeAmountByType")
    @Operation(summary = "查询代理下会员的根据类型分组的总存提款")
    List<ReportUserAmountVO> getUserFeeAmountByType(@RequestBody ReportAgentWinLossParamVO vo);
}

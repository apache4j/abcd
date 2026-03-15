package com.cloud.baowang.report.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.report.api.enums.ApiConstants;
import com.cloud.baowang.report.api.vo.ReportUserTopReqVO;
import com.cloud.baowang.report.api.vo.ReportUserWinLossReqVO;
import com.cloud.baowang.report.api.vo.user.ReportUserVenueBetsTopVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "remoteReportUserVenueFixedWinLoseApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员场馆盈亏首次结算时间api服务")
public interface ReportUserVenueFixedWinLoseApi {
    String PREFIX = ApiConstants.PREFIX + "/reportUserVenueFixedWinLose/api/";

    @PostMapping(value = PREFIX + "queryUserWinLossInfo")
    @Operation(summary = "查询指定会员输赢数据")
    ReportUserVenueBetsTopVO queryUserWinLossInfo(@RequestBody ReportUserWinLossReqVO userWinLossReqVO);

    @PostMapping(value = PREFIX + "queryVenueDayCurrency")
    @Operation(summary = "查询场馆在30天内有投注的币种")
    List<String> queryVenueDayCurrency(@RequestBody ReportUserTopReqVO userTopReqVO);


    @PostMapping(value = PREFIX + "queryUserIdsByVenueDayAmount")
    @Operation(summary = "查询场馆在30天内每天的投注额流水超过指定金额的用户")
    Page<ReportUserVenueBetsTopVO> queryUserIdsByVenueDayAmount(@RequestBody ReportUserTopReqVO userTopReqVO);

    @PostMapping(value = PREFIX + "queryUserBetsTopPlatBetAmount")
    @Operation(summary = "查询出前100的用户投注,转平台币排序")
    List<ReportUserVenueBetsTopVO> queryUserBetsTopPlatBetAmount(@RequestBody ReportUserTopReqVO userTopReqVO);

    @PostMapping(value = PREFIX + "queryUserBetsPlatBetAmountTotal")
    @Operation(summary = "查询出用户的币种投注汇总金额")
    ReportUserVenueBetsTopVO queryUserBetsPlatBetAmountTotal(@RequestBody ReportUserTopReqVO userTopReqVO);


    @PostMapping(value = PREFIX + "queryVenueBetsPlatBetAmountTotal")
    @Operation(summary = "查询出场馆的币种投注汇总金额")
    ReportUserVenueBetsTopVO queryVenueBetsPlatBetAmountTotal(@RequestBody ReportUserTopReqVO userTopReqVO);

    @PostMapping(value = PREFIX + "queryUserBetsTop")
    @Operation(summary = "会员流水排行榜统计")
    Page<ReportUserVenueBetsTopVO> queryUserBetsTop(@RequestBody ReportUserTopReqVO userTopReqVO);

}

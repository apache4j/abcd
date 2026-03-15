package com.cloud.baowang.play.api.api.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.agent.*;
import com.cloud.baowang.play.api.vo.order.*;
import com.cloud.baowang.play.api.vo.order.client.OrderRecordClientReqVO;
import com.cloud.baowang.play.api.vo.order.client.OrderRecordClientRespVO;
import com.cloud.baowang.play.api.vo.order.report.*;
import com.cloud.baowang.play.api.vo.user.PlayUserBetAmountSumVO;
import com.cloud.baowang.play.api.vo.user.PlayUserWinLossParamVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

@FeignClient(contextId = "play-order-record-api", value = ApiConstants.NAME)
@Tag(name = "注单记录相关api")
public interface OrderRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/order-record/api/";

    @Operation(summary = "注单列表查询-中控后台")
    @PostMapping(PREFIX + "admin/page")
    ResponseVO<Page<OrderRecordPageRespVO>> adminPage(@RequestBody OrderRecordAdminResVO dto);

    @Operation(summary = "注单列表查询-中控后台总计查询")
    @PostMapping(PREFIX + "admin/total")
    ResponseVO<OrderRecordAdminTotalRespVO> adminTotal(@RequestBody OrderRecordAdminResVO dto);

    @Operation(summary = "注单列表查询-中控后台导出条数")
    @PostMapping(PREFIX + "admin/export/count")
    ResponseVO<Long> orderExportCount(@RequestBody OrderRecordAdminResVO vo);

    @Operation(summary = "注单详情")
    @PostMapping(PREFIX + "admin/info")
    ResponseVO<OrderRecordAdminInfoRespVO> orderInfo(@RequestBody OrderRecordAdminInfoResVO vo);

    @Operation(summary = "查询会员的最后下注信息")
    @PostMapping(PREFIX + "getLastOrderRecord")
    ResponseVO<OrderInfoVO> getLastOrderRecord(@RequestParam("userId") String userId);

    @Operation(summary = "客户端用户投注记录")
    @PostMapping(PREFIX + "client/orderRecord")
    ResponseVO<OrderRecordClientRespVO> clientOrderRecord(@RequestBody OrderRecordClientReqVO vo);

    @Operation(summary = "批量查询订单")
    @PostMapping(PREFIX + "getOrderListByOrderIds")
    List<OrderRecordVO> getOrderListByOrderIds(@RequestBody GeOrderListVO vo);

    @Operation(summary = "批量查询订单key userAccount，value 有效投注")
    @PostMapping(PREFIX + "getAgentUserRecordByUserWinLoss")
    List<HashMap> getAgentUserRecordByUserWinLoss(@RequestBody PlayAgentUserTeamParam vo);

    @Operation(summary = "统计时间段内的流水")
    @PostMapping(PREFIX + "getTotalAmountByUserId")
    BigDecimal getTotalAmountByUserId(@RequestParam("userId") String userId, @RequestParam("startTime") Long startTime, @RequestParam("endTime") Long endTime);

    // 代理客户端 首页游戏输赢 查询最新的5条注单
    @PostMapping(PREFIX + "getNewest5OrderRecord")
    ResponseVO<List<GetNewest5OrderRecordVO>> getNewest5OrderRecord(@RequestBody GetNewest5OrderRecordParam param);

    @Operation(summary = ("代理端会员游戏记录"))
    @PostMapping(PREFIX + "getAgentUserPageList")
    ResponseVO<Page<AgentUserOrderRecordPageVO>> getAgentUserPageList(@RequestBody AgentUserOrderRecordReqVO vo);

    @Operation(summary = ("返回代理下级会员投注统计"))
    @PostMapping(PREFIX + "getAgentClientOrder")
    ResponseVO<AgentBetOrderResVO> getAgentClientOrder(@RequestBody AgentUserOrderRecordReqVO vo);

    @Operation(summary = ("统计代理所有下级的有效投注"))
    @PostMapping(PREFIX + "getUserOrderAmountByAgent")
    List<PlayUserBetAmountSumVO> getUserOrderAmountByAgent(@RequestBody PlayAgentWinLossParamVO vo);

    @PostMapping(PREFIX + "getDistinctByTimeSiteCodeCurrencyCode")
    @Operation(summary = "根据时间范围,站点code,币种去重统计投注人数")
    Long getDistinctByTimeSiteCodeCurrencyCode(@RequestParam("date") String date,
                                               @RequestParam("siteCode") String siteCode,
                                               @RequestParam("currencyCode") String currencyCode);

    @PostMapping(PREFIX + "venueWinLoseRecalculatePage")
    @Operation(summary = "场馆盈亏重算分页查询数据源")
    Page<VenueWinLoseRecalculateVO> venueWinLoseRecalculatePage(@RequestBody VenueWinLoseRecalculateReqVO vo);

    @Operation(summary = ("统计单个会员的有效投注"))
    @PostMapping(PREFIX + "getUserOrderAmountByUserId")
    List<PlayUserBetAmountSumVO> getUserOrderAmountByUserId(@RequestBody PlayUserWinLossParamVO vo);

    @Operation(summary = ("统计代理下投注人数"))
    @PostMapping(PREFIX + "getBetUserCount")
    Integer getBetUserCount(@RequestBody PlayAgentWinLossParamVO vo);

    @PostMapping(PREFIX + "winLoseRecalculatePage")
    @Operation(summary = "会员盈亏重算分页查询数据源")
    Page<WinLoseRecalculateVO> winLoseRecalculatePage(@RequestBody WinLoseRecalculateReqVO vo);

    @GetMapping(PREFIX+"getUserNewBetOrder")
    @Operation(summary = "获取会员最新下注时间")
    Long getUserNewBetOrder(@RequestParam("userId") String userId);

    @GetMapping(PREFIX+"getUserAmountRecordByTime")
    @Operation(summary = "获取会员某个时间段内已结算打码量")
    List<BigDecimal> getUserAmountRecordByTime(@RequestParam("userId") String userId,
                                               @RequestParam("lastWithdrawTime") Long lastWithdrawTime,
                                               @RequestParam("endTime")Long endTime);

    @Operation(summary = "批量查询会员未结算订单")
    @PostMapping(PREFIX + "getNotSettleOrderListByUserIds")
    List<OrderRecordVO> getNotSettleOrderListByUserIds(@RequestBody UserIdsVO vo);

    @Operation(summary = "站点批量查询会员未结算订单")
    @PostMapping(PREFIX + "getNotSettleOrderListBySiteCode")
    List<OrderRecordVO> getNotSettleOrderListBySiteCode(@RequestParam("siteCode") String siteCode);


    @Operation(summary = "根据三方订单号查询订单详情")
    @PostMapping(PREFIX + "getByThirdOrderId")
    OrderRecordVO getByThirdOrderId(@RequestParam("thirdOrderId")String thirdOrderId);


    @Operation(summary = "查询会员未结算订单")
    @PostMapping(PREFIX + "getNotSettleOrderListByUserId")
    List<OrderRecordVO> getNotSettleOrderListByUserId(@RequestParam("userId") String userId);

    @Operation(summary = "查询免费旋转金额list")
    @PostMapping(PREFIX + "winLoseRecalculateFreeSpinPage")
    List<WinLoseRecalculateFeelSpinVO> winLoseRecalculateFreeSpinPage(@RequestBody WinLoseRecalculateReqVO vo);
}

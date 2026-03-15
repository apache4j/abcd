package com.cloud.baowang.play.api.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.order.OrderRecordApi;
import com.cloud.baowang.play.api.vo.agent.*;
import com.cloud.baowang.play.api.vo.order.*;
import com.cloud.baowang.play.api.vo.order.client.OrderRecordClientReqVO;
import com.cloud.baowang.play.api.vo.order.client.OrderRecordClientRespVO;
import com.cloud.baowang.play.api.vo.order.report.*;
import com.cloud.baowang.play.api.vo.user.PlayUserBetAmountSumVO;
import com.cloud.baowang.play.api.vo.user.PlayUserWinLossParamVO;
import com.cloud.baowang.play.service.OrderRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * 注单记录service
 */
@Slf4j
@RestController
@AllArgsConstructor
public class OrderRecordApiImpl implements OrderRecordApi {
    private final OrderRecordService orderRecordService;

    @Override
    public ResponseVO<Page<OrderRecordPageRespVO>> adminPage(OrderRecordAdminResVO dto) {
        return orderRecordService.adminPage(dto);
    }

    @Override
    public ResponseVO<OrderRecordAdminTotalRespVO> adminTotal(OrderRecordAdminResVO dto) {
        return orderRecordService.adminTotal(dto);
    }

    @Override
    public ResponseVO<Long> orderExportCount(OrderRecordAdminResVO dto) {
        return orderRecordService.orderExportCount(dto);
    }

    @Override
    public ResponseVO<OrderRecordAdminInfoRespVO> orderInfo(OrderRecordAdminInfoResVO vo) {
        return orderRecordService.orderInfo(vo);
    }

    @Override
    public ResponseVO<OrderInfoVO> getLastOrderRecord(String userId) {
        return orderRecordService.getLastOrderRecord(userId);
    }

    @Override
    public ResponseVO<OrderRecordClientRespVO> clientOrderRecord(OrderRecordClientReqVO vo) {
        return orderRecordService.clientOrderRecord(vo);
    }

    @Override
    public List<OrderRecordVO> getOrderListByOrderIds(GeOrderListVO vo) {
        return orderRecordService.getOrderListByOrderIds(vo);
    }

    @Override
    public List<HashMap> getAgentUserRecordByUserWinLoss(PlayAgentUserTeamParam vo) {
        return orderRecordService.getAgentUserRecordByUserWinLoss(vo);
    }

    @Override
    public BigDecimal getTotalAmountByUserId(String userAccount, Long startTime, Long endTime) {
        return orderRecordService.getTotalAmountByUserId(userAccount,startTime,endTime);
    }

    @Override
    public ResponseVO<List<GetNewest5OrderRecordVO>> getNewest5OrderRecord(GetNewest5OrderRecordParam param) {
        return orderRecordService.getNewest5OrderRecord(param);
    }

    @Override
    public ResponseVO<Page<AgentUserOrderRecordPageVO>> getAgentUserPageList(AgentUserOrderRecordReqVO vo) {
        return orderRecordService.getAgentUserPageList(vo);
    }

    @Override
    public ResponseVO<AgentBetOrderResVO> getAgentClientOrder(AgentUserOrderRecordReqVO vo) {
        return orderRecordService.getAgentClientOrder(vo);

    }

    @Override
    public List<PlayUserBetAmountSumVO> getUserOrderAmountByAgent(PlayAgentWinLossParamVO vo) {
        return orderRecordService.getUserOrderAmountByAgent(vo);
    }

    @Override
    public Long getDistinctByTimeSiteCodeCurrencyCode(String date, String siteCode, String currencyCode) {
        return orderRecordService.getDistinctByTimeSiteCodeCurrencyCode(date,siteCode,currencyCode);
    }

    @Override
    public Page<VenueWinLoseRecalculateVO> venueWinLoseRecalculatePage(VenueWinLoseRecalculateReqVO vo) {
        return orderRecordService.venueWinLoseRecalculatePage(vo);
    }

    @Override
    public List<PlayUserBetAmountSumVO> getUserOrderAmountByUserId(PlayUserWinLossParamVO vo) {
        return orderRecordService.getUserOrderAmountByUserId(vo);
    }

    @Override
    public Integer getBetUserCount(PlayAgentWinLossParamVO vo) {
        return orderRecordService.getBetUserCount(vo);
    }

    @Override
    public Page<WinLoseRecalculateVO> winLoseRecalculatePage(WinLoseRecalculateReqVO vo) {
        return orderRecordService.winLoseRecalculatePage(vo);
    }

    @Override
    public List<WinLoseRecalculateFeelSpinVO> winLoseRecalculateFreeSpinPage(WinLoseRecalculateReqVO vo) {
        return orderRecordService.winLoseRecalculateFreeSpinPage(vo);
    }

    @Override
    public Long getUserNewBetOrder(String userId) {
        return orderRecordService.getUserNewBetOrder(userId);
    }

    @Override
    public List<BigDecimal> getUserAmountRecordByTime(String userId, Long lastWithdrawTime,Long endTime) {
        return orderRecordService.getUserAmountRecordByTime(userId,lastWithdrawTime,endTime);
    }

    @Override
    public List<OrderRecordVO> getNotSettleOrderListByUserIds(UserIdsVO vo) {
        return orderRecordService.getNotSettleOrderListByUserIds(vo);
    }

    @Override
    public List<OrderRecordVO> getNotSettleOrderListByUserId(String userId) {
        return orderRecordService.getNotSettleOrderListByUserId(userId);
    }

    @Override
    public List<OrderRecordVO> getNotSettleOrderListBySiteCode(String siteCode) {
        return orderRecordService.getNotSettleOrderListBySiteCode(siteCode);
    }

    @Override
    public OrderRecordVO getByThirdOrderId(String thirdOrderId) {
        return orderRecordService.getByThirdOrderId(thirdOrderId);
    }
}

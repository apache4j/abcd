package com.cloud.baowang.play.api.order;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.order.OrderAbnormalRecordApi;
import com.cloud.baowang.play.api.enums.ClassifyEnum;
import com.cloud.baowang.play.api.vo.AbnormalOrder.OrderAbnormalDetailLabelVO;
import com.cloud.baowang.play.api.vo.AbnormalOrder.OrderAbnormalDetailVO;
import com.cloud.baowang.play.api.vo.AbnormalOrder.OrderAbnormalInfoLabel;
import com.cloud.baowang.play.api.vo.AbnormalOrder.OrderAbnormalRecordAdminResVO;
import com.cloud.baowang.play.api.vo.AbnormalOrder.OrderAbnormalRecordPageRespVO;
import com.cloud.baowang.play.api.vo.AbnormalOrder.OrderHistoryVO;
import com.cloud.baowang.play.api.vo.order.OrderInfoLabel;
import com.cloud.baowang.play.api.vo.venue.GameInfoVO;
import com.cloud.baowang.play.game.base.VenueOrderInfoService;
import com.cloud.baowang.play.game.factory.GameServiceFactory;
import com.cloud.baowang.play.po.OrderAbnormalRecordPO;
import com.cloud.baowang.play.po.OrderRecordPO;
import com.cloud.baowang.play.repositories.OrderAbnormalRecordRepository;
import com.cloud.baowang.play.service.GameInfoService;
import com.cloud.baowang.play.service.OrderRecordService;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * 异常注单记录service
 */
@RestController
@AllArgsConstructor
public class OrderAbnormalRecordApiImpl implements OrderAbnormalRecordApi {

    private final OrderAbnormalRecordRepository orderAbnormalRecordRepository;
    private final GameServiceFactory gameServiceFactory;
    private final OrderRecordService orderRecordService;
    private final GameInfoService gameInfoService;

    @Override
    public ResponseVO<Page<OrderAbnormalRecordPageRespVO>> adminAbnormalPage(OrderAbnormalRecordAdminResVO vo) {
        Page<OrderAbnormalRecordPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        if(ObjectUtil.isNotEmpty(vo.getOrderField())) {
            if (vo.getOrderField().equals("betAmount")) {
                vo.setOrderField("bet_amount");
            } else if (vo.getOrderField().equals("winLossAmount")) {
                vo.setOrderField("win_loss_amount");
            }
        }
        Page<OrderAbnormalRecordPO> poPage = orderAbnormalRecordRepository.getPage(page, vo);
        Page<OrderAbnormalRecordPageRespVO> voPage = new Page<>();
        
        List<OrderAbnormalRecordPageRespVO> voList = new ArrayList<>();
        if (poPage != null && !poPage.getRecords().isEmpty()) {
            // vip段位&等级
            Map<Integer, String> vipGradeMap = orderRecordService.adaptiveVIPGrade(vo.getSiteCode());
            Map<Integer, String> vipRankMap = orderRecordService.adaptiveVIPRank(vo.getSiteCode());
            poPage.getRecords().forEach(info -> {
                OrderAbnormalRecordPageRespVO respVO = new OrderAbnormalRecordPageRespVO();
                BeanUtils.copyProperties(info, respVO);
                respVO.setVipRankText(vipRankMap.get(info.getVipRank()));
                respVO.setVipGradeText(vipGradeMap.get(info.getVipGradeCode()));

                if(ObjectUtil.isNotEmpty(info.getVipGradeCode())) {
                    respVO.setZhVipGradeText("VIP" + (info.getVipGradeCode() - 1));
                }

                String gameNo = respVO.getGameNo();
                if (StrUtil.isNotBlank(gameNo)) {
                    gameNo = gameNo.replace(CommonConstant.COMMA, "\n");
                    respVO.setGameNo(gameNo);
                }
                voList.add(respVO);
            });
            BeanUtils.copyProperties(poPage, voPage);
            voPage.setRecords(voList);

            return ResponseVO.success(voPage);
        }

        return ResponseVO.success(new Page<>());
    }

    @Override
    public OrderAbnormalDetailLabelVO findOrderAbnormalDetailByOrderId(IdVO idVO) {
        OrderAbnormalDetailLabelVO labelVO = new OrderAbnormalDetailLabelVO();
        OrderAbnormalRecordPO po = orderAbnormalRecordRepository.selectById(idVO.getId());

        List<OrderHistoryVO> historyVOList = JSON.parseArray(po.getHistory(), OrderHistoryVO.class);

        for (OrderHistoryVO historyVO : historyVOList) {
            OrderAbnormalDetailVO orderRecordDetailVO = historyVO.getOrderAbnormalDetailVO();
            Integer orderClassify = orderRecordDetailVO.getOrderClassify();
            if (orderClassify == ClassifyEnum.RESETTLED.getCode()) {
                orderClassify = ClassifyEnum.SETTLED.getCode();
            }

            //根据场馆来解析订单详细信息
            List<Map<String, Object>> gameDetailList = getGameDetailByVenue(orderRecordDetailVO);
            historyVO.setGameDetailList(gameDetailList);

            List<GameInfoVO> gameInfoVOS = gameInfoService.queryGameAccessParamListByVenueCode(List.of(VenuePlatformConstants.SH, VenuePlatformConstants.ACELT));
            Map<String, GameInfoVO> shGameInfoMap = Optional.ofNullable(gameInfoVOS)
                    .map(s -> s.stream().collect(Collectors.toMap(GameInfoVO::getAccessParameters, p -> p, (k1, k2) -> k2)))
                    .orElse(Maps.newHashMap());
            OrderRecordPO record = new OrderRecordPO();
            BeanUtils.copyProperties(orderRecordDetailVO, record);
            orderRecordService.getAdapterTransI18n(record, shGameInfoMap);
            orderRecordDetailVO.setOrderInfo(record.getOrderInfo());
            orderRecordDetailVO.setPlayType(record.getPlayType());
            orderRecordDetailVO.setGameName(record.getGameName());
            orderRecordDetailVO.setResultList(record.getResultList());
            orderRecordDetailVO.setPlayType(record.getPlayType());
            //沙巴体育的投注类型
            if (po.getVenueCode().equals(VenuePlatformConstants.SBA)||po.getVenueCode().equals(VenuePlatformConstants.CMD)) {
                orderRecordDetailVO.setRoomTypeName(gameServiceFactory.getGameInfoService(po.getVenueCode()).getBetType(po.getParlayInfo()));
            }
        }
        labelVO.setHistoryVOList(historyVOList);

        VenueEnum venueEnum = VenueEnum.nameOfCode(po.getVenueCode());
        VenueTypeEnum typeEnum = venueEnum.getType();
        if (Objects.nonNull(typeEnum)) {
            OrderAbnormalInfoLabel.OrderInfoLabelEnum orderLabelEnum = OrderAbnormalInfoLabel.OrderInfoLabelEnum.getByType(typeEnum);
            if (Objects.nonNull(orderLabelEnum)) {
                labelVO.setLabel(orderLabelEnum.getLabel());
                labelVO.setTableLabel(orderLabelEnum.getTableLabel());
            }
        }

        return labelVO;
    }

    @Override
    public ResponseVO<Long> getTotalCount(OrderAbnormalRecordAdminResVO vo) {
        return ResponseVO.success(orderAbnormalRecordRepository.getTotalCount(vo));
    }

    List<Map<String, Object>> getGameDetailByVenue(OrderAbnormalDetailVO orderRecordDetailVO) {
        if (!orderRecordDetailVO.getVenueType().equals(VenueTypeEnum.SPORTS.getCode()) &&
                !orderRecordDetailVO.getVenueType().equals(VenueTypeEnum.ACELT.getCode()) &&
                !orderRecordDetailVO.getVenueType().equals(VenueTypeEnum.ELECTRONIC_SPORTS.getCode())&&
                !orderRecordDetailVO.getVenueType().equals(VenueTypeEnum.SH.getCode())&&
                !orderRecordDetailVO.getVenueType().equals(VenueTypeEnum.MARBLES.getCode())) {
            //只有体育电竞弹珠彩票才会有表格数据
            return null;
        }

        VenueOrderInfoService venueOrderInfoService = gameServiceFactory.getGameInfoService(orderRecordDetailVO.getVenueCode());


        JSONObject jsonObject = JSONObject.parseObject(orderRecordDetailVO.getParlayInfo());
        Map<String, Object> parlayMap = jsonObject.getInnerMap();

        return venueOrderInfoService.getOrderInfo(parlayMap);
    }
}

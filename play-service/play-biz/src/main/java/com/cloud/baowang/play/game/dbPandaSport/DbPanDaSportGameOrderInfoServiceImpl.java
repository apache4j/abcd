package com.cloud.baowang.play.game.dbPandaSport;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.play.api.enums.OrderWinOrLossStatusEnum;
import com.cloud.baowang.play.api.enums.dbPanDaSport.DbPanDaSportBetResultEnum;
import com.cloud.baowang.play.api.enums.dbPanDaSport.DbPanDaSportMatchTypeEnum;
import com.cloud.baowang.play.api.enums.dbPanDaSport.DbPanDaSportSerialTypeEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.dbPanDaSport.orderRecord.DbPanDaSportOrderDetail;
import com.cloud.baowang.play.api.vo.dbPanDaSport.orderRecord.DbPanDaSportOrderRecordRes;
import com.cloud.baowang.play.api.vo.order.client.OrderMultipleBetVO;
import com.cloud.baowang.play.api.vo.order.client.SportOrderClientResVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.VenueOrderInfoService;
import com.cloud.baowang.play.po.OrderRecordPO;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Service(ServiceType.GAME_INFO_THIRD_API_SERVICE + VenuePlatformConstants.DB_PANDA_SPORT)
public class DbPanDaSportGameOrderInfoServiceImpl implements VenueOrderInfoService {


    @Override
    public String getGameVenueCode() {
        return VenuePlatformConstants.DB_PANDA_SPORT;
    }


    private Integer getWinLossStatus(Integer winLossStatus) {
        if (winLossStatus == null) {
            return null;
        }
        if (winLossStatus == DbPanDaSportBetResultEnum.WIN.getCode() || winLossStatus == DbPanDaSportBetResultEnum.HALF_WIN.getCode()) {
            return OrderWinOrLossStatusEnum.WIN.getCode();
        }

        if (winLossStatus == DbPanDaSportBetResultEnum.LOSS.getCode() || winLossStatus == DbPanDaSportBetResultEnum.HALF_LOSS.getCode()) {
            return OrderWinOrLossStatusEnum.LOSE.getCode();
        }

        if (winLossStatus == DbPanDaSportBetResultEnum.PUSH.getCode()) {
            return OrderWinOrLossStatusEnum.DRAW.getCode();
        }

        return null;
    }


    public SportOrderClientResVO getSportData(OrderRecordPO record, String lang, VenueInfoVO venueInfoVO) {
        SportOrderClientResVO data = new SportOrderClientResVO();
        BeanUtils.copyProperties(record, data);
        List<OrderMultipleBetVO> orderMultipleBetList = new ArrayList<>();

        DbPanDaSportOrderRecordRes betList = JSONObject.parseObject(record.getParlayInfo(), DbPanDaSportOrderRecordRes.class);

        if (ObjectUtil.isNotEmpty(betList)) {
            List<DbPanDaSportOrderDetail> detailList = betList.getDetailList();
            for (DbPanDaSportOrderDetail item : detailList) {
                OrderMultipleBetVO betVO = new OrderMultipleBetVO();
                betVO.setOrderId(String.valueOf(item.getBetNo()));
                betVO.setEventInfo(item.getMatchName());//联赛名字
                String matchInfo = item.getMatchInfo();
                betVO.setTeamInfo(matchInfo);
                betVO.setBetContent(item.getPlayOptionName());
                betVO.setBetAmount(item.getBetAmount());
                betVO.setOdds(String.valueOf(item.getOddsValue()));
                betVO.setWinlossStatus(getWinLossStatus(Integer.valueOf(item.getBetResult())));

                DbPanDaSportBetResultEnum dbPanDaSportBetResultEnum = DbPanDaSportBetResultEnum.fromCode(Integer.valueOf(item.getBetResult()));
                if (dbPanDaSportBetResultEnum != null) {
                    betVO.setOrderClassify(dbPanDaSportBetResultEnum.getPlatOrderStatus().getClassifyCode());
                }
                orderMultipleBetList.add(betVO);
            }
        }
        data.setOrderMultipleBetList(orderMultipleBetList);
        return data;
    }


    private String matchInfo(DbPanDaSportOrderDetail item) {
        StringBuilder stringBuilder = new StringBuilder();
        String matchName = item.getMatchName();
        String matchInfo = item.getMatchInfo();
        Long matchId = item.getMatchId();

        stringBuilder.append("联赛名称:").append(matchName).append("\n")
                .append("对阵表:").append(matchInfo).append("\n")
                .append("赛事ID:").append(matchId);
        return stringBuilder.toString();
    }


    private String marketInfo(DbPanDaSportOrderDetail item) {
        DbPanDaSportMatchTypeEnum matchTypeEnum = DbPanDaSportMatchTypeEnum.fromCode(item.getMatchType());
        if (matchTypeEnum == null) {
            return null;
        }
        return "大小盘阶段: " + matchTypeEnum.getDescription();
    }


    private String orderInfo(DbPanDaSportOrderDetail item){
        return "投注: " + item.getPlayOptionName() + "\n" +
                "赔率: " + item.getOddsValue();
    }


    private String orderStatus(DbPanDaSportOrderDetail item){
        DbPanDaSportBetResultEnum dbPanDaSportBetResultEnum = DbPanDaSportBetResultEnum.fromCode(Integer.valueOf(item.getBetResult()));
        if(dbPanDaSportBetResultEnum == null){
            return null;
        }
        return "注单结果: " + dbPanDaSportBetResultEnum.getPlatOrderStatus().getName();
    }



    private List<Map<String, Object>> getOrderInfoDetail(List<DbPanDaSportOrderDetail> detailList,Long settleTime) {
        List<Map<String, Object>> resultList = Lists.newArrayList();

        for (DbPanDaSportOrderDetail item : detailList) {
            Map<String, Object> sportsMap = Maps.newHashMap();
            sportsMap.put("matchInfo", matchInfo(item));//比赛详情
            sportsMap.put("marketInfo", marketInfo(item));//盘口详情
            sportsMap.put("orderInfo", orderInfo(item));//投注详情
            sportsMap.put("settleTime", settleTime);//结算时间
            sportsMap.put("orderResult", orderStatus(item));//结算时间
            resultList.add(sportsMap);
        }
        return resultList;
    }


    public String getBetType(String json) {

        if (ObjectUtil.isEmpty(json)) {
            return "单关";
        }
        JSONObject jsonObject = JSONObject.parseObject(json);

        if (!jsonObject.containsKey("seriesType") || jsonObject.get("seriesType") == null) {
            return "单关";
        }
//        String roomType = recordPO.getRoomType();

        DbPanDaSportSerialTypeEnum dbPanDaSportSerialTypeEnum = DbPanDaSportSerialTypeEnum.fromCode(jsonObject.getInteger("seriesType"));
        if(dbPanDaSportSerialTypeEnum == null){
            return "单关";
        }
        return dbPanDaSportSerialTypeEnum.getDesc();
    }



    @Override
    public List<Map<String, Object>> getOrderInfo(Map<String, Object> parlayMap) {
        List<DbPanDaSportOrderDetail> detailList = JSONUtil.toList(parlayMap.get("detailList").toString(), DbPanDaSportOrderDetail.class);

        Long settleTime = null;
        if(parlayMap.containsKey("settleTime")){
            settleTime = (Long) parlayMap.get("settleTime");
        }
        return getOrderInfoDetail(detailList,settleTime);
    }


    public String getOrderRecordInfo(OrderRecordPO recordPO) {
        return footballRecordInfo(recordPO);
    }

    private String footballRecordInfo(OrderRecordPO recordPO) {
        StringBuilder stringBuilder = new StringBuilder();
        try {

            DbPanDaSportOrderRecordRes recordRes = JSONObject.parseObject(recordPO.getParlayInfo(), DbPanDaSportOrderRecordRes.class);
            Long betTime = recordPO.getBetTime();
            String timezone = CurrReqUtils.getTimezone();

            List<DbPanDaSportOrderDetail> detailList = recordRes.getDetailList();
            String roomType = recordPO.getRoomType();
            DbPanDaSportSerialTypeEnum dbPanDaSportSerialTypeEnum = DbPanDaSportSerialTypeEnum.fromCode(Integer.valueOf(roomType));
            if (dbPanDaSportSerialTypeEnum == null) {
                log.info("DB体育,roomType 为空");
                return null;
            }

            for (DbPanDaSportOrderDetail item : detailList) {
                DbPanDaSportMatchTypeEnum matchTypeEnum = DbPanDaSportMatchTypeEnum.fromCode(item.getMatchType());
                stringBuilder.append(item.getSportName()).append("(")
                        .append(TimeZoneUtils.convertTimestampToString(betTime, timezone, TimeZoneUtils.patten_yyyyMMddHHmmss))
                        .append(")")
                        .append("\n")
                        .append(item.getMatchName()).append("\n")
                        .append(item.getMatchInfo())
                        .append("(")
                        .append("赛事ID: ")
                        .append(item.getMatchId())
                        .append(")").append("\n").append("<span style=\"color: red;\">").append(item.getPlayOptionName()).append("</span>")
                        .append("@").append(item.getOddsValue()).append("\n");
                if (matchTypeEnum != null) {
                    stringBuilder.append(matchTypeEnum.getDescription()).append(" 玩法:").append(item.getPlayName()).append("\n");
                    ;
                    if (matchTypeEnum.getCode().equals(DbPanDaSportMatchTypeEnum.VIRTUAL.getCode())) {
                        stringBuilder.append(matchTypeEnum.getDescription()).append("\n");
                    } else {
                        stringBuilder.append("正常赛事").append("\n");
                    }
                }
            }
            //单关
            if (!DbPanDaSportSerialTypeEnum.SINGLE.equals(dbPanDaSportSerialTypeEnum)) {
                stringBuilder.append(dbPanDaSportSerialTypeEnum.getDesc());
            }

        } catch (Exception e) {
            log.info("DB体育,注单详情异常:{}", recordPO, e);
        }
        return stringBuilder.toString();
    }


}

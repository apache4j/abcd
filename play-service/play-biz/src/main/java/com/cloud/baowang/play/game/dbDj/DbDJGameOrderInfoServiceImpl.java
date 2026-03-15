package com.cloud.baowang.play.game.dbDj;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.play.api.enums.dbDj.*;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.dbDj.DbDJOrderRecordDetailRes;
import com.cloud.baowang.play.api.vo.order.client.EventOrderClientResVO;
import com.cloud.baowang.play.api.vo.order.client.OrderMultipleBetVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.VenueOrderInfoService;
import com.cloud.baowang.play.po.OrderRecordPO;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Service(ServiceType.GAME_INFO_THIRD_API_SERVICE + VenuePlatformConstants.DB_DJ)
public class DbDJGameOrderInfoServiceImpl implements VenueOrderInfoService {

    @Override
    public String getGameVenueCode() {
        return VenuePlatformConstants.DB_DJ;
    }

    //英雄召唤注单详情 比赛详情
    private String getSeriesMatchInfo(Map<String, Object> parlayMap){
        boolean langType = getLang();
        StringBuilder stringBuilder = new StringBuilder();
        String matchName = "";//联赛名称
        String matchInfo = "";//对阵表
        Long matchId = 0L;//赛事ID
        if (langType) {
            if (parlayMap.containsKey("play_name_cn")) {
                matchName = (String) parlayMap.get("play_name_cn");
            }

            if (parlayMap.containsKey("play_name_cn")) {
                matchInfo = (String) parlayMap.get("play_name_cn");
            }
        } else {
            if (parlayMap.containsKey("play_name_en")) {
                matchName = (String) parlayMap.get("play_name_en");
            }

            if (parlayMap.containsKey("play_name_en")) {
                matchInfo = (String) parlayMap.get("play_name_en");
            }
        }
        //期号
        if (parlayMap.containsKey("ticket_plan_no")) {
            matchId = (Long) parlayMap.get("ticket_plan_no");
        }
        stringBuilder.append("联赛名称:").append(matchName).append("\n")
                .append("对阵表:").append(matchInfo).append("\n")
                .append("赛事ID:").append(matchId);
        return stringBuilder.toString();
    }


    private String getSeriesOrderInfos(Map<String, Object> parlayMap) {
        StringBuilder stringBuilder = new StringBuilder();
        String betContent = (String) parlayMap.get("bet_content");
        stringBuilder.append("投注:").append(betContent).append("\n").append("赔率:").append(parlayMap.get("odd"));
        return stringBuilder.toString();
    }

    //英雄召唤注单详情
    private List<Map<String, Object>> getSeriesOrderInfoDetail(Map<String, Object> parlayMap) {
        Integer settleStatus = (Integer) parlayMap.get("settle_status");
        DbDjYxZhSettleStatusEnum statusEnum = DbDjYxZhSettleStatusEnum.fromCode(settleStatus);
        if(statusEnum == null){
            return null;
        }
        long settleTime = Long.parseLong(String.valueOf(parlayMap.get("settle_time")))*1000L;
        List<Map<String, Object>> list = Lists.newArrayList();
        Map<String, Object> sportsMap = Maps.newHashMap();
        sportsMap.put("matchInfo", getSeriesMatchInfo(parlayMap));//比赛详情
        sportsMap.put("marketInfo", null);//盘口详情
        sportsMap.put("orderInfo", getSeriesOrderInfos(parlayMap));//投注详情
        sportsMap.put("settleTime", settleTime);//结算时间
        sportsMap.put("orderResult", getLang()?statusEnum.getDesc():statusEnum.getEnDesc());//状态
        list.add(sportsMap);
        return list;
    }



    //电子竞技注单详情
    private List<Map<String, Object>> getEleOrderInfoDetail(Map<String, Object> parlayMap) {
        List<Map<String, Object>> list = Lists.newArrayList();
        String orderResult = null;
        if(parlayMap.containsKey("bet_status")){
            DbDjOrderStatusEnum statusEnum = DbDjOrderStatusEnum.fromCode((Integer) parlayMap.get("bet_status"));
            if(statusEnum != null){
                orderResult = getLang()?statusEnum.getDesc():statusEnum.getEnDesc();
            }
        }
        //串关
        if(parlayMap.containsKey("detail") ){
            String detailJson = String.valueOf(parlayMap.get("detail"));
            List<DbDJOrderRecordDetailRes> detailList = JSONUtil.toList(detailJson, DbDJOrderRecordDetailRes.class);
            if(CollectionUtil.isNotEmpty(detailList)){
                for (DbDJOrderRecordDetailRes item : detailList) {
                    Map<String, Object> sportsMap = Maps.newHashMap();
                    sportsMap.put("matchInfo", matchInfo(item));//比赛详情
                    sportsMap.put("marketInfo", marketInfo(item));//盘口详情
                    sportsMap.put("orderInfo", orderInfo(item));//投注详情
                    sportsMap.put("settleTime", item.getSettle_time()*1000L);//结算时间
                    sportsMap.put("orderResult", orderResult);//状态
                    list.add(sportsMap);
                }
            }
        }else{
            Map<String, Object> sportsMap = Maps.newHashMap();
            DbDJOrderRecordDetailRes entity = JSONUtil.toBean(JSON.toJSONString(parlayMap),DbDJOrderRecordDetailRes.class);
            sportsMap.put("matchInfo", matchInfo(entity));//比赛详情
            sportsMap.put("marketInfo", marketInfo(entity));//盘口详情
            sportsMap.put("orderInfo", orderInfo(entity));//投注详情
            sportsMap.put("settleTime", String.valueOf(entity.getSettle_time()*1000L));//结算时间
            sportsMap.put("orderResult", orderResult);//状态
            list.add(sportsMap);
        }
        return list;
    }

    private String orderInfo(DbDJOrderRecordDetailRes item) {
        boolean langType = getLang();
        StringBuilder stringBuilder = new StringBuilder();
        String betContent = null;
        if (langType) {
            betContent = item.getOdd_name();
        } else {
            betContent = item.getOdd_en_name();
        }
        stringBuilder.append("投注:").append(betContent).append("\n").append("赔率:").append(item.getOdd());
        return stringBuilder.toString();
    }

    private String marketInfo(DbDJOrderRecordDetailRes item) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean langType = getLang();
        String matchName;//盘口名称

        if (langType) {
            matchName = item.getMarket_cn_name();
        } else {
            matchName = item.getMarket_en_name();
        }

        stringBuilder.append(matchName).append("阶段: ");

        DbDjMatchTypeEnum dbDjMatchTypeEnum = DbDjMatchTypeEnum.fromCode(item.getMatch_type());
        if (dbDjMatchTypeEnum != null) {
            stringBuilder.append(dbDjMatchTypeEnum.getName());
        }

        return stringBuilder.toString();
    }


    private String matchInfo(DbDJOrderRecordDetailRes item) {
        boolean langType = getLang();
        StringBuilder stringBuilder = new StringBuilder();
        String matchName;//联赛名称
        String matchInfo;//对阵表
        Long matchId;//赛事ID
        if (langType) {
            matchName = item.getTournament();
            matchInfo = item.getTeam_cn_names();
        } else {
            matchName = item.getTournament_en();
            matchInfo = item.getTeam_en_names();
        }
        matchId = item.getMatch_id();//赛事ID
        stringBuilder.append("联赛名称:").append(matchName).append("\n")
                .append("对阵表:").append(matchInfo).append("\n")
                .append("赛事ID:").append(matchId);
        return stringBuilder.toString();
    }


    @Override
    public List<Map<String, Object>> getOrderInfo(Map<String, Object> parlayMap) {
        //如果存在 series_name 这个字段的值代表玩的是 英雄召唤 的DBDJ
        if (parlayMap.containsKey("series_name")) {
            return getSeriesOrderInfoDetail(parlayMap);
        } else {
            return getEleOrderInfoDetail(parlayMap);
        }
    }

    /**
     * true = 中文
     * false = 英文
     */
    private Boolean getLang() {
        String lang = CurrReqUtils.getLanguage();
        return LanguageEnum.ZH_CN.getLang().equals(lang);
    }

    //英雄召唤
    private String getSeriesOrderInfo(Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean langType = getLang();

        String gameName = null;//电竞项目

        String ticketName = null;//赛事名称

        String betTime = null;//投注时间

        String gameNo = null;//期号

        String betContent = null;//投注

        BigDecimal odd = null;//赔率

        if (map.containsKey("bet_time")) {
            betTime = TimeZoneUtils.convertTimestampToString((Long) map.get("bet_time"), CurrReqUtils.getTimezone(), TimeZoneUtils.patten_yyyyMMddHHmmss);
        }
        if (map.containsKey("odd")) {
            odd = (BigDecimal) map.get("odd");
        }

        //期号
        if (map.containsKey("ticket_plan_no")) {
            gameNo = (String) map.get("ticket_plan_no");
        }

        if (langType) {
            if (map.containsKey("bet_content_cn")) {
                betContent = (String) map.get("bet_content_cn");
            }

            if (map.containsKey("ticket_name_cn")) {
                gameName = (String) map.get("ticket_name_cn");
            }

            if (map.containsKey("play_name_cn")) {
                ticketName = (String) map.get("play_name_cn");
            }

        } else {

            if (map.containsKey("bet_content_en")) {
                betContent = (String) map.get("bet_content_en");
            }

            if (map.containsKey("ticket_name_en")) {
                gameName = (String) map.get("ticket_name_en");
            }

            if (map.containsKey("play_name_en")) {
                ticketName = (String) map.get("play_name_en");
            }
        }


        stringBuilder.append(gameName).append("(").append(betTime).append(")").append("\n")
                .append("(").append(gameNo).append(")").append("\n")
                .append(toSetRedText(betContent)).append("@").append(odd).append("\n")
                .append(ticketName).append("\n");

        return stringBuilder.toString();
    }


    //电子竞技
    private String getEleOrderInfo(Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();

        boolean langType = getLang();

        String timezone = CurrReqUtils.getTimezone();

        String gameName = "eSports";//电竞项目

        String ticketName = null;//赛事名称

        String betTime = null;//投注时间

        Long gameNo = null;//期号

        String betContent = null;//投注

        String odd = null;//赔率

        String matchInfo;//对阵信息

        String parleyTypeName = null;//串关类型名称

        String tournament  = null;
        if (map.containsKey("tournament_en")) {
            tournament = (String) map.get("tournament_en");
        }

        if (map.containsKey("parley_type")) {
            Integer parleyType = (Integer) map.get("parley_type");
            DbDjParleyTypeEnum dbDjParleyTypeEnum = DbDjParleyTypeEnum.fromCode(parleyType);
            if (dbDjParleyTypeEnum != null) {
                parleyTypeName = dbDjParleyTypeEnum.getName();

            }
        }

        //串关
        if (map.containsKey("detail")) {
            JSONArray jsonArray = (JSONArray) map.get("detail");
            List<DbDJOrderRecordDetailRes> list = jsonArray.toJavaList(DbDJOrderRecordDetailRes.class);

            for (DbDJOrderRecordDetailRes item : list) {
                betTime = TimeZoneUtils.convertTimestampToString((Long) map.get("bet_time"), timezone, TimeZoneUtils.patten_yyyyMMddHHmmss);
                if (langType) {
                    ticketName = item.getTournament();
                    matchInfo = item.getTeam_cn_names();
                    betContent = item.getOdd_name();
                } else {
                    ticketName = item.getTournament_en();
                    matchInfo = item.getTeam_en_names();
                    betContent = item.getOdd_en_name();
                }

                stringBuilder.append(gameName).append("(").append(betTime).append(")").append("\n")
                        .append(ticketName).append("\n")
                        .append(matchInfo).append("(").append("赛事ID:").append(item.getMatch_id()).append(")").append("\n")
                        .append(toSetRedText(betContent)).append("@").append(item.getOdd()).append("\n");
                DbDjIsLiveEnum dbDjIsLiveEnum = DbDjIsLiveEnum.fromCode(item.getIs_live());
                if (dbDjIsLiveEnum != null) {
                    stringBuilder.append(dbDjIsLiveEnum.getDesc());

                    DbDjMatchTypeEnum dbDjMatchTypeEnum = DbDjMatchTypeEnum.fromCode(item.getMatch_type());
                    if (dbDjMatchTypeEnum != null) {
                        stringBuilder.append(" 玩法: ").append(dbDjMatchTypeEnum.getName()).append("\n");
                    }
                }
            }
            stringBuilder.append(parleyTypeName);
            return stringBuilder.toString();
        }

        if (map.containsKey("bet_time")) {
            betTime = TimeZoneUtils.convertTimestampToString((Long) map.get("bet_time"), CurrReqUtils.getTimezone(), TimeZoneUtils.patten_yyyyMMddHHmmss);
        }
        if (map.containsKey("odd")) {
            odd = (String) map.get("odd");
        }

        //赛事ID
        if (map.containsKey("match_id")) {
            gameNo = (Long) map.get("match_id");
        }


        if (langType) {
            if (map.containsKey("odd_name")) {//投注项名称(中文)
                betContent = (String) map.get("odd_name");
            }

            if (map.containsKey("market_cn_name")) {//联赛中文名
                gameName = (String) map.get("market_cn_name");
            }

            if (map.containsKey("team_cn_names")) {//队伍中文名称 主客队用 , 拼接
                ticketName = (String) map.get("team_cn_names");
            }

        } else {

            if (map.containsKey("odd_en_name")) {//投注项名称(英文)
                betContent = (String) map.get("odd_en_name");
            }

            if (map.containsKey("market_en_name")) {//盘口英文名
                gameName = (String) map.get("market_en_name");
            }

            if (map.containsKey("team_en_names")) {//队伍英文名称 主客队用 , 拼接
                ticketName = (String) map.get("team_en_names");
            }
        }
        stringBuilder.append(tournament).append("\n")
                .append(parleyTypeName).append("\n")
                .append(gameName).append("\n")
                .append(betTime).append("\n")
                .append("(").append(gameNo).append(")").append("\n")
                .append(toSetRedText(betContent)).append("@").append(odd).append("\n")
                .append(ticketName).append("\n");
        return stringBuilder.toString();

    }

    public String getOrderRecordInfo(OrderRecordPO recordPO) {

        try {
            Map<String, Object> map = JSONObject.parseObject(recordPO.getParlayInfo(), Map.class);

            //如果存在 series_name 这个字段的值代表玩的是 英雄召唤 的DBDJ
            if (map.containsKey("series_name")) {
                return getSeriesOrderInfo(map);
            } else {
                return getEleOrderInfo(map);
            }
        } catch (Exception e) {
            log.error("{},注单详情解析错误", VenueEnum.DB_DJ.getVenueName(), e);
            return null;
        }
    }


    public String toSetRedText(String text) {
        if (ObjectUtil.isEmpty(text)) {
            return null;
        }
        text = "<span style=\"color: red;\">" + text + "</span>";
        return text;
    }

    public EventOrderClientResVO getESportData(OrderRecordPO recordPO) {
        EventOrderClientResVO resVO = new EventOrderClientResVO();

        BeanUtils.copyProperties(recordPO, resVO);

        Map<String, Object> map = JSONObject.parseObject(recordPO.getParlayInfo(), Map.class);

        boolean langType = getLang();

        String ticketName = null;//赛事信息

        String teamInfo = null;//玩法

        BigDecimal odds = null;//赔率

        String betContent = null;//投注内容

        boolean multipleBet = false;//是否串关 true:是 false:否

        //如果存在 series_name 这个字段的值代表玩的是 英雄召唤 的DBDJ
        if (map.containsKey("series_name")) {

            if (map.containsKey("odd")) {
                odds = (BigDecimal) map.get("odd");
            }

            if (langType) {
                if (map.containsKey("ticket_name_cn")) {
                    ticketName = (String) map.get("ticket_name_cn");
                }
                if (map.containsKey("play_name_cn")) {
                    teamInfo = (String) map.get("play_name_cn");
                }
                if (map.containsKey("bet_content_cn")) {
                    betContent = (String) map.get("bet_content_cn");
                }

            } else {
                if (map.containsKey("ticket_name_en")) {
                    ticketName = (String) map.get("ticket_name_en");
                }

                if (map.containsKey("play_name_en")) {
                    teamInfo = (String) map.get("play_name_en");
                }
                if (map.containsKey("bet_content_en")) {
                    betContent = (String) map.get("bet_content_en");
                }
            }

            resVO.setBetContent(betContent);
            resVO.setTeamInfo(teamInfo);
            resVO.setEventInfo(ticketName);
            resVO.setOdds(String.valueOf(odds));
        }else {

            List<OrderMultipleBetVO> orderMultipleBetList = Lists.newArrayList();
            if(map.containsKey("order_type")){
                Integer orderType = (Integer) map.get("order_type");
                if(orderType > 1){// = 串关 注单类型 1-普通注单 2-普通串关注单 3-局内串关注单, 4-复合玩法注单
                    multipleBet = true;
                }
            }
            if (map.containsKey("detail")) {
                JSONArray jsonArray = (JSONArray) map.get("detail");
                List<DbDJOrderRecordDetailRes> list = jsonArray.toJavaList(DbDJOrderRecordDetailRes.class);
                for (DbDJOrderRecordDetailRes item : list) {
                    OrderMultipleBetVO orderMultipleBetVO = new OrderMultipleBetVO();
                    String detailTicketName = null;
                    String detailMatchInfo = null;
                    String detailBetContent = null;
                    if (langType) {
                        detailTicketName = item.getTournament();
                        detailMatchInfo = item.getTeam_cn_names();
                        detailBetContent = item.getOdd_name();
                    } else {
                        detailTicketName = item.getTournament_en();
                        detailMatchInfo = item.getTeam_en_names();
                        detailBetContent = item.getOdd_en_name();
                    }

                    orderMultipleBetVO.setOrderId(item.getOrder_id().toString());
                    orderMultipleBetVO.setEventInfo(detailTicketName);
                    orderMultipleBetVO.setTeamInfo(detailMatchInfo);
                    orderMultipleBetVO.setBetContent(detailBetContent);
                    orderMultipleBetVO.setOdds(item.getOdd());

                    DbDjOrderStatusEnum statusEnum = DbDjOrderStatusEnum.fromCode(item.getStatus());
                    if (statusEnum != null) {
                        orderMultipleBetVO.setOrderClassify(OrderStatusEnum.getClassifyCodeByCode(statusEnum.getPlatCurrencyStatus()));
                    }
                    orderMultipleBetList.add(orderMultipleBetVO);
                }
            }

            resVO.setMultipleBet(multipleBet);
            resVO.setOrderMultipleBetList(orderMultipleBetList);

        }
        return resVO;
    }


    public String getBetType(String json) {

        if (ObjectUtil.isEmpty(json)) {
            return "单关";
        }

        JSONObject jsonObject = JSONObject.parseObject(json);

        //英雄召唤注单 只有单关
        if (jsonObject.containsKey("series_name")) {
            return "单关";
        }

        DbDjOrderTypeEnum dbPanDaSportSerialTypeEnum = DbDjOrderTypeEnum.fromCode(jsonObject.getInteger("order_type"));
        if(dbPanDaSportSerialTypeEnum == null){
            return "单关";
        }
        return dbPanDaSportSerialTypeEnum.getDesc();
    }

}

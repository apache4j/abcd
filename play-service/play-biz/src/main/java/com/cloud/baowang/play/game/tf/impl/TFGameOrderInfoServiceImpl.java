//package com.cloud.baowang.play.game.tf.impl;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.map.MapUtil;
//import cn.hutool.core.util.ObjectUtil;
//import com.alibaba.fastjson.JSONObject;
//import com.cloud.baowang.play.api.enums.venue.VenueEnum;
//import com.cloud.baowang.common.core.utils.CurrReqUtils;
//import com.cloud.baowang.common.core.utils.DateUtils;
//import com.cloud.baowang.common.core.utils.TimeZoneUtils;
//import com.cloud.baowang.play.constants.ServiceType;
//import com.cloud.baowang.play.game.base.VenueOrderInfoBaseService;
//import com.cloud.baowang.play.game.base.VenueOrderInfoService;
//import com.cloud.baowang.play.game.tf.resp.TfOrderInfoVO;
//import com.cloud.baowang.play.po.OrderRecordPO;
//import com.google.common.collect.Maps;
//import lombok.AllArgsConstructor;
//import org.apache.commons.compress.utils.Lists;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.stereotype.Service;
//
//import java.time.Instant;
//import java.time.OffsetDateTime;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
//@AllArgsConstructor
//@Service(ServiceType.GAME_INFO_THIRD_API_SERVICE + VenueCodeConstants.TF)
//public class TFGameOrderInfoServiceImpl extends VenueOrderInfoBaseService implements VenueOrderInfoService {
//
//    @Override
//    public String getGameVenueCode() {
//        return VenueEnum.TF.getVenueCode();
//    }
//
//    private String getSimpleMatchInfo(Map<String, Object> parlyMap){
//        String competitionName = parlyMap.get("competition_name").toString();
//        String eventName = parlyMap.get("event_name").toString();
//        String eventId = parlyMap.get("event_id").toString();
//        return String.format(MATCH_INFO,competitionName,eventName, eventId);
//
//    }
//
//    private String getMarketInfo(Map<String, Object> parlyMap, String collusion, String round){
//        String betTypeName = parlyMap.getOrDefault("bet_type_name","").toString();
//        String marketOption = parlyMap.getOrDefault("market_option","").toString();
//        String mapNum = parlyMap.getOrDefault("map_num","").toString();
//        String gameMarketName = parlyMap.getOrDefault("game_market_name","").toString();
//        String ticketType = parlyMap.get("ticket_type").toString();
//        String type = null;
//        if (ticketType == null){
//            type = "";
//        } else if (ticketType.equalsIgnoreCase("db")){
//            type = "早盘";
//        } else if (ticketType.equalsIgnoreCase("live")) {
//            type = "滚球";
//        }
//        TfOrderInfoVO vo = new TfOrderInfoVO();
//        vo.setBetTypeName(betTypeName);
//        String value = marketOptionName(marketOption)+mapNum(mapNum)+betTypeName(vo)+gameMarketName;
//
//        return String.format(MARKET_INFO,gameMarketName,type);
//    }
//
//    private String getEsportOrderInfo(Map<String, Object> parlyMap, String totalOdd){
//        String handicap = parlyMap.getOrDefault("handicap","").toString();
//        String odd = parlyMap.get("member_odds").toString();
//        String teams = parlyMap.get("bet_selection").toString();
//        String eventName = parlyMap.getOrDefault("event_name","").toString();
//        String betSelection = parlyMap.getOrDefault("bet_selection","").toString();
//        String betTypeName = parlyMap.get("bet_type_name").toString();
//        return String.format(ORDER_INFO,(getHome(betTypeName,teams)+" "+handicap+" "+splitEventName(eventName,betSelection)),odd);
//    }
//
//    private String getOrderResult(Map<String, Object> parlyMap){
//        String result = "";
//        String resCode = parlyMap.getOrDefault("result_status","").toString();
//        if (resCode.equalsIgnoreCase("win")){
//            result = "赢";
//        }else if (resCode.equalsIgnoreCase("lose") || resCode.equalsIgnoreCase("loss")){
//            result = "输";
//        } else if (resCode.equalsIgnoreCase("drawl")) {
//            result = "和";
//        }else if (resCode.equalsIgnoreCase("cancelled")) {
//            result = "取消";
//        }
//        if(StringUtils.isEmpty(resCode)){
//            return "";
//        }
//        return String.format(ORDER_RESULT,result,parlyMap.getOrDefault("result",""));
//    }
//
//
//    @Override
//    public List<Map<String, Object>> getOrderInfo(Map<String, Object> parlayMap) {
//        List<Map<String, Object>> resultList = Lists.newArrayList();
//        boolean isList = false;
//        List<Map<String, Object>> list = null;
//        if (parlayMap.containsKey("tickets")) {
//            list = (List<Map<String, Object>>) parlayMap.get("tickets");
//            isList = list.size() > 1;
//        }
//        if (!isList) {
//            Map<String, Object> eSportsMap = Maps.newHashMap();
//            // 比赛详情
//            String matchInfo = getSimpleMatchInfo(parlayMap);
//            eSportsMap.put("matchInfo", matchInfo);
//            // 盘口详情
//            String marketInfo = getMarketInfo(parlayMap, "","1");
//            eSportsMap.put("marketInfo", marketInfo);
//            // 注单详情
//            String orderInfo = getEsportOrderInfo(parlayMap,null);
//            eSportsMap.put("orderInfo", orderInfo);
//            // 结算时间 时间戳特殊处理
//            String settlementDateTimeStr = parlayMap.getOrDefault("settlement_datetime","0").toString();
//            //Long settleTime = TimeZoneUtils.parseDate4TimeZoneToTime(settlementDateTimeStr, TimeZoneUtils.patten_yMdTHmsSSSSSSZ, TimeZone.getTimeZone(CurrReqUtils.getTimezone()));
//            Long settleTime =convertUtcToTimestamp(settlementDateTimeStr);
//            eSportsMap.put("settleTime", null == settleTime ? null : String.valueOf(settleTime));
//            // 注单结果
//            String orderResult = getOrderResult(parlayMap);
//            eSportsMap.put("orderResult", orderResult);
//            resultList.add(eSportsMap);
//        } else {
//            String  totalOdds = parlayMap.get("member_odds").toString();
//            int size = list.size();
//            for (int i = 0; i < size; i++) {
//                Map<String, Object> map = list.get(i);
//                Map<String, Object> eSportsMap = Maps.newHashMap();
//                String matchInfo = getSimpleMatchInfo(map);
//                eSportsMap.put("matchInfo", matchInfo);
//                // 盘口详情
//                String marketInfo = getMarketInfo(map,size+"串1",String.valueOf(i+1));
//                eSportsMap.put("marketInfo", marketInfo);
//                // 注单详情
//
//                String orderInfo = getEsportOrderInfo(map,totalOdds);
//                eSportsMap.put("orderInfo", orderInfo);
//                // 结算时间(单关如果没有内层结算时间，则取外层结算时间) 时间戳特殊处理
//                String settlementDateTimeStr = map.getOrDefault("settlement_datetime","0").toString();
//                // Long settleTime = TimeZoneUtils.parseDate4TimeZoneToTime(settlementDateTimeStr, TimeZoneUtils.patten_yMdTHmsSSSSSSZ, TimeZone.getTimeZone(CurrReqUtils.getTimezone()));
//                Long settleTime =convertUtcToTimestamp(settlementDateTimeStr);
//                eSportsMap.put("settleTime", null == settleTime ? null : String.valueOf(settleTime));
//                // 注单结果
//                String orderResult = getOrderResult(map);
//                eSportsMap.put("orderResult", orderResult);
//                resultList.add(eSportsMap);
//            }
//        }
//        return resultList;
//    }
//
//    public static void main(String[] args) {
//
//        OrderRecordPO recordPO = new OrderRecordPO();
//        String str = "{\"amount\":100.0,\"bet_selection\":\"客队\",\"bet_type_name\":\"AH\",\"combo\":false,\"competition_name\":\"2023 DOTA2 MONKEY BOY\",\"currency\":\"RMB\",\"date_created\":\"2025-04-23T09:30:39.356967Z\",\"euro_odds\":2.06,\"event_datetime\":\"2025-04-23T15:59:59Z\",\"event_id\":122892288,\"event_name\":\"3 Aegises 2 Anchors vs 425\",\"game_market_name\":\"让分局\",\"game_type_id\":2,\"game_type_name\":\"DOTA 2\",\"handicap\":1.5,\"id\":\"CFQCD312854J3039XHQ001B\",\"is_combo\":false,\"is_unsettled\":false,\"malay_odds\":-0.94,\"market_option\":\"match\",\"member_code\":\"Utest_12590690\",\"member_odds\":2.06,\"member_odds_style\":\"euro\",\"member_win_loss_f\":0.0,\"modified_datetime\":\"2025-04-23T09:30:40.019475Z\",\"odds\":-0.94,\"request_source\":\"desktop-browser\",\"settlement_status\":\"confirmed\",\"ticket_type\":\"db\",\"unsettled\":false}";
//        recordPO.setParlayInfo(str);
//        TFGameOrderInfoServiceImpl impl = new TFGameOrderInfoServiceImpl();
//        System.out.println(impl.getOrderRecordInfo(recordPO));
//        JSONObject jsonObject = JSONObject.parseObject("{\"amount\":10.0,\"combo\":true,\"currency\":\"RMB\",\"date_created\":\"2025-03-22T06:40:02.829286Z\",\"euro_odds\":107.32,\"event_datetime\":\"2025-03-27T15:59:59Z\",\"event_id\":113044709,\"game_type_id\":0,\"id\":\"CFPCC310935G4002OXL9EBC\",\"is_combo\":true,\"is_unsettled\":true,\"member_code\":\"Utest_56849711\",\"member_odds\":107.32,\"member_odds_style\":\"euro\",\"modified_datetime\":\"2025-03-27T09:21:50.428158Z\",\"odds\":107.32,\"request_source\":\"desktop-browser\",\"settlement_status\":\"confirmed\",\"tickets\":[{\"bet_selection\":\"主队\",\"bet_type_name\":\"WIN\",\"combo\":false,\"competition_name\":\"2023 DOTA2 MONKEY BOY\",\"date_created\":\"2025-03-22T06:40:02.871993Z\",\"euro_odds\":1.46,\"event_datetime\":\"2025-03-27T15:59:59Z\",\"event_id\":113044709,\"event_name\":\"212 vs 2EZ Gaming\",\"game_market_name\":\"独赢\",\"game_type_id\":2,\"game_type_name\":\"DOTA 2\",\"id\":\"CFPCC310935G4002GNG9AD7\",\"is_combo\":false,\"is_unsettled\":true,\"malay_odds\":0.46,\"market_option\":\"match\",\"member_odds\":1.46,\"member_odds_style\":\"euro\",\"member_win_loss_f\":-10.0,\"odds\":0.46,\"request_source\":\"desktop-browser\",\"result\":\"1:2\",\"result_status\":\"LOSS\",\"settlement_datetime\":\"2025-03-27T09:21:49.750834Z\",\"settlement_status\":\"settled\",\"ticket_type\":\"db\",\"unsettled\":true},{\"bet_selection\":\"客队\",\"bet_type_name\":\"WIN\",\"combo\":false,\"competition_name\":\"2023 DOTA2 MONKEY BOY\",\"date_created\":\"2025-03-22T06:40:02.896018Z\",\"euro_odds\":2.28,\"event_datetime\":\"2025-03-26T15:59:59Z\",\"event_id\":113044280,\"event_name\":\"212 vs 2EZ Gaming\",\"game_market_name\":\"独赢\",\"game_type_id\":2,\"game_type_name\":\"DOTA 2\",\"id\":\"CFPCC310935G4002DIX8329\",\"is_combo\":false,\"is_unsettled\":false,\"malay_odds\":-0.78,\"market_option\":\"match\",\"member_odds\":2.28,\"member_odds_style\":\"euro\",\"member_win_loss_f\":-10.0,\"odds\":-0.78,\"request_source\":\"desktop-browser\",\"result\":\"2:0\",\"result_status\":\"LOSS\",\"settlement_datetime\":\"2025-03-26T06:04:02.389979Z\",\"settlement_status\":\"settled\",\"ticket_type\":\"db\",\"unsettled\":false},{\"bet_selection\":\"主队\",\"bet_type_name\":\"WIN\",\"combo\":false,\"competition_name\":\"2023 DOTA2 MONKEY BOY\",\"date_created\":\"2025-03-22T06:40:02.967626Z\",\"euro_odds\":1.46,\"event_datetime\":\"2025-03-26T15:59:59Z\",\"event_id\":113044456,\"event_name\":\"2EZ Gaming vs 322 As An Opportunity\",\"game_market_name\":\"独赢\",\"game_type_id\":2,\"game_type_name\":\"DOTA 2\",\"id\":\"CFPCC310935G4002IFE7D4D\",\"is_combo\":false,\"is_unsettled\":false,\"malay_odds\":0.46,\"market_option\":\"match\",\"member_odds\":1.46,\"member_odds_style\":\"euro\",\"member_win_loss_f\":0.0,\"odds\":0.46,\"request_source\":\"desktop-browser\",\"settlement_status\":\"confirmed\",\"ticket_type\":\"db\",\"unsettled\":false},{\"bet_selection\":\"主队\",\"bet_type_name\":\"WIN\",\"combo\":false,\"competition_name\":\"2023 DOTA2 MONKEY BOY\",\"date_created\":\"2025-03-22T06:40:02.943754Z\",\"euro_odds\":1.46,\"event_datetime\":\"2025-03-26T15:59:59Z\",\"event_id\":113044515,\"event_name\":\"3 Aegises 2 Anchors vs 425\",\"game_market_name\":\"独赢\",\"game_type_id\":2,\"game_type_name\":\"DOTA 2\",\"id\":\"CFPCC310935G4002TQC3B83\",\"is_combo\":false,\"is_unsettled\":false,\"malay_odds\":0.46,\"market_option\":\"match\",\"member_odds\":1.46,\"member_odds_style\":\"euro\",\"member_win_loss_f\":4.6,\"odds\":0.46,\"request_source\":\"desktop-browser\",\"result\":\"2:1\",\"result_status\":\"WIN\",\"settlement_datetime\":\"2025-03-25T02:17:33.962718Z\",\"settlement_status\":\"settled\",\"ticket_type\":\"db\",\"unsettled\":false},{\"bet_selection\":\"主队\",\"bet_type_name\":\"WIN\",\"combo\":false,\"competition_name\":\"2023 DOTA2 MONKEY BOY\",\"date_created\":\"2025-03-22T06:40:02.991253Z\",\"euro_odds\":1.46,\"event_datetime\":\"2025-03-26T15:59:59Z\",\"event_id\":113044372,\"event_name\":\"2EZ Gaming vs 3 Aegises 2 Anchors\",\"game_market_name\":\"独赢\",\"game_type_id\":2,\"game_type_name\":\"DOTA 2\",\"id\":\"CFPCC310935G4002INE5BC9\",\"is_combo\":false,\"is_unsettled\":false,\"malay_odds\":0.46,\"market_option\":\"match\",\"member_odds\":1.46,\"member_odds_style\":\"euro\",\"member_win_loss_f\":4.6,\"odds\":0.46,\"request_source\":\"desktop-browser\",\"result\":\"2:0\",\"result_status\":\"WIN\",\"settlement_datetime\":\"2025-03-26T06:03:39.174973Z\",\"settlement_status\":\"settled\",\"ticket_type\":\"db\",\"unsettled\":false},{\"bet_selection\":\"主队\",\"bet_type_name\":\"WIN\",\"combo\":false,\"competition_name\":\"2023 CSGO Dog Pig\",\"date_created\":\"2025-03-22T06:40:02.847747Z\",\"euro_odds\":1.46,\"event_datetime\":\"2025-03-26T15:59:59Z\",\"event_id\":113039618,\"event_name\":\"00Prospects vs 100 Thieves\",\"game_market_name\":\"独赢\",\"game_type_id\":1,\"game_type_name\":\"CS:GO\",\"id\":\"CFPCC310935G4002MXI56BD\",\"is_combo\":false,\"is_unsettled\":true,\"malay_odds\":0.46,\"market_option\":\"match\",\"member_odds\":1.46,\"member_odds_style\":\"euro\",\"member_win_loss_f\":-10.0,\"odds\":0.46,\"request_source\":\"desktop-browser\",\"result\":\"1:2\",\"result_status\":\"LOSS\",\"settlement_datetime\":\"2025-03-27T09:02:25.464213Z\",\"settlement_status\":\"settled\",\"ticket_type\":\"db\",\"unsettled\":true},{\"bet_selection\":\"主队\",\"bet_type_name\":\"WIN\",\"combo\":false,\"competition_name\":\"2023 DOTA2 MONKEY BOY\",\"date_created\":\"2025-03-22T06:40:02.919824Z\",\"euro_odds\":1.46,\"event_datetime\":\"2025-03-26T05:30:00Z\",\"event_id\":113044214,\"event_name\":\"16 Anos Melhor Idade vs 2EZ Gaming\",\"game_market_name\":\"独赢\",\"game_type_id\":2,\"game_type_name\":\"DOTA 2\",\"id\":\"CFPCC310935G4002XNE1863\",\"is_combo\":false,\"is_unsettled\":false,\"malay_odds\":0.46,\"market_option\":\"match\",\"member_odds\":1.46,\"member_odds_style\":\"euro\",\"member_win_loss_f\":0.0,\"odds\":0.46,\"request_source\":\"desktop-browser\",\"settlement_status\":\"confirmed\",\"ticket_type\":\"db\",\"unsettled\":false},{\"bet_selection\":\"客队\",\"bet_type_name\":\"WIN\",\"combo\":false,\"competition_name\":\"2023 DOTA2 MONKEY BOY\",\"date_created\":\"2025-03-22T06:40:03.014931Z\",\"euro_odds\":2.28,\"event_datetime\":\"2025-03-24T15:59:59Z\",\"event_id\":113043700,\"event_name\":\"3 Aegises 2 Anchors vs 425\",\"game_market_name\":\"独赢\",\"game_type_id\":2,\"game_type_name\":\"DOTA 2\",\"id\":\"CFPCC310935G4003CKTA1F1\",\"is_combo\":false,\"is_unsettled\":false,\"malay_odds\":-0.78,\"market_option\":\"match\",\"member_odds\":2.28,\"member_odds_style\":\"euro\",\"member_win_loss_f\":12.8,\"odds\":-0.78,\"request_source\":\"desktop-browser\",\"result\":\"0:2\",\"result_status\":\"WIN\",\"settlement_datetime\":\"2025-03-25T00:58:18.082941Z\",\"settlement_status\":\"settled\",\"ticket_type\":\"db\",\"unsettled\":false},{\"bet_selection\":\"主队\",\"bet_type_name\":\"WIN\",\"combo\":false,\"competition_name\":\"2023 DOTA2 MONKEY BOY\",\"date_created\":\"2025-03-22T06:40:03.039569Z\",\"euro_odds\":1.46,\"event_datetime\":\"2025-03-24T15:59:59Z\",\"event_id\":113043616,\"event_name\":\"2EZ Gaming vs 322 As An Opportunity\",\"game_market_name\":\"独赢\",\"game_type_id\":2,\"game_type_name\":\"DOTA 2\",\"id\":\"CFPCC310935G4003SNW2112\",\"is_combo\":false,\"is_unsettled\":false,\"malay_odds\":0.46,\"market_option\":\"match\",\"member_odds\":1.46,\"member_odds_style\":\"euro\",\"member_win_loss_f\":0.0,\"odds\":0.46,\"request_source\":\"desktop-browser\",\"settlement_status\":\"confirmed\",\"ticket_type\":\"db\",\"unsettled\":false},{\"bet_selection\":\"主队\",\"bet_type_name\":\"WIN\",\"combo\":false,\"competition_name\":\"2023 DOTA2 MONKEY BOY\",\"date_created\":\"2025-03-22T06:40:03.062981Z\",\"euro_odds\":1.46,\"event_datetime\":\"2025-03-23T15:59:59Z\",\"event_id\":113043216,\"event_name\":\"2EZ Gaming vs 322 As An Opportunity\",\"game_market_name\":\"独赢\",\"game_type_id\":2,\"game_type_name\":\"DOTA 2\",\"id\":\"CFPCC310935G4003FAQB86E\",\"is_combo\":false,\"is_unsettled\":false,\"malay_odds\":0.46,\"market_option\":\"match\",\"member_odds\":1.46,\"member_odds_style\":\"euro\",\"member_win_loss_f\":0.0,\"odds\":0.46,\"request_source\":\"desktop-browser\",\"settlement_status\":\"confirmed\",\"ticket_type\":\"db\",\"unsettled\":false}],\"unsettled\":true}");
//        Map<String, Object> parlayMap = jsonObject.getInnerMap();
//        System.out.println(impl.getOrderInfo(parlayMap));
//
//    }
//
//
//
//    public String getOrderRecordInfo(OrderRecordPO recordPO) {
//        // CurrReqUtils.getTimezone();
//        if(Objects.isNull(recordPO) || StringUtils.isEmpty(recordPO.getParlayInfo())){
//            return "";
//        }
//        String str = "";
//        try{
//            TfOrderInfoVO tfOrderInfoVO = JSONObject.parseObject(recordPO.getParlayInfo(), TfOrderInfoVO.class);
//
//            // 投注时间
//            str += StringUtils.isEmpty(tfOrderInfoVO.getGameTypeName())?"":tfOrderInfoVO.getGameTypeName()+"("+ DateUtils.formatDateByZoneId(convertUtcToTimestamp(tfOrderInfoVO.getDateCreated()),DateUtils.FULL_FORMAT_1, CurrReqUtils.getTimezone())+")\n";
//            // 对阵信息
//            str += StringUtils.isEmpty(tfOrderInfoVO.getCompetitionName())?"":tfOrderInfoVO.getCompetitionName()+"\n";
//            // 赛事名称
//            String s= StringUtils.isEmpty(tfOrderInfoVO.getEventName())?"":tfOrderInfoVO.getEventName();
//            // 赛事ID
//            if(StringUtils.isNotEmpty(s)) {
//                s += Objects.isNull(tfOrderInfoVO.getEventId()) ? "" : "(赛事ID："+tfOrderInfoVO.getEventId()+")" + "\n";
//            }
//            str += s;
//            // 投注信息@赔率
//            str += toSetRedText(StringUtils.isEmpty(tfOrderInfoVO.getBetSelection())?"":getHome(tfOrderInfoVO.getBetTypeName(), tfOrderInfoVO.getBetSelection())
//                    +  (Objects.isNull(tfOrderInfoVO.getHandicap())?"": tfOrderInfoVO.getHandicap())
//                    + splitEventName(tfOrderInfoVO.getEventName(),tfOrderInfoVO.getBetSelection()))
//                    +"@" + tfOrderInfoVO.getMemberOdds() + "\n";
//            // 是否滚球
//            str += StringUtils.isEmpty(ticketType(tfOrderInfoVO))?"":ticketType(tfOrderInfoVO);
//            // 玩法
//            //String tt = marketOptionName(tfOrderInfoVO.getMarketOption())+mapNum(tfOrderInfoVO.getMapNum())+betTypeName(tfOrderInfoVO)+(StringUtils.isEmpty(tfOrderInfoVO.getGameMarketName())?"":tfOrderInfoVO.getGameMarketName());
//            String tt = (StringUtils.isEmpty(tfOrderInfoVO.getGameMarketName())?"":tfOrderInfoVO.getGameMarketName());
//            /*if(StringUtils.isNotEmpty(tfOrderInfoVO.getBetTypeName()) && tfOrderInfoVO.getBetTypeName().equals("AH")){
//                tt += tfOrderInfoVO.getHandicap();
//            }*/
//            if(StringUtils.isNotEmpty(tt)) {
//                tt += "\n";
//                str += " 玩法："+ tt;
//            }
//            // 投注比分
//
//            // 是否串关
//            if(tfOrderInfoVO.isCombo()){
//                List<TfOrderInfoVO> tickets = tfOrderInfoVO.getTickets();
//                if(CollUtil.isNotEmpty(tickets)){
//                    int size = tickets.size();
//                    int i = 0;
//                    for (TfOrderInfoVO ticket : tickets) {
//                        str += StringUtils.isEmpty(ticket.getGameTypeName())?"":ticket.getGameTypeName()+"("+ DateUtils.formatDateByZoneId(convertUtcToTimestamp(ticket.getDateCreated()),DateUtils.FULL_FORMAT_1, CurrReqUtils.getTimezone())+")\n";
//                        // 对阵信息
//                        str +=  StringUtils.isEmpty(ticket.getCompetitionName())?"":ticket.getCompetitionName()+"\n";
//                        // 赛事名称
//                        String s2 = StringUtils.isEmpty(ticket.getEventName())?"":ticket.getEventName();
//                        // 赛事ID
//                        if(StringUtils.isNotEmpty(s2)) {
//                            s2 += Objects.isNull(ticket.getEventId()) ? "" : "(赛事ID："+ticket.getEventId()+")" + "\n";
//                        }
//                        str += s2;
//                        // 投注信息@赔率
//                        str += toSetRedText(StringUtils.isEmpty(ticket.getBetSelection())?"":getHome(ticket.getBetTypeName(),ticket.getBetSelection())
//                                + (Objects.isNull(ticket.getHandicap())?"": ticket.getHandicap())
//                                + splitEventName(ticket.getEventName(),ticket.getBetSelection()))
//                                +"@" + ticket.getMemberOdds() + "\n";
//
//                        //  玩法
//                        // 是否滚球
//                        str += StringUtils.isEmpty(ticketType(ticket))?"":ticketType(ticket);
//                        // 玩法
//                        //String tt2 = marketOptionName(ticket.getMarketOption())+mapNum(ticket.getMapNum())+betTypeName(ticket)+(StringUtils.isEmpty(ticket.getGameMarketName())?"":ticket.getGameMarketName());
//                        String tt2 = (StringUtils.isEmpty(ticket.getGameMarketName())?"":ticket.getGameMarketName());
//                        /*if(StringUtils.isNotEmpty(ticket.getBetTypeName()) && ticket.getBetTypeName().equals("AH")){
//                            tt2 += ticket.getHandicap();
//                        }*/
//                        if(StringUtils.isNotEmpty(tt2)) {
//                            tt2 += "\n";
//                            str +=  " 玩法："+  tt2;
//                        }
//                        // 串关
//                        if(size -1 == i) {
//                            str += "串关";
//                        }
//                        i++;
//                    }
//                }
//            }
//            if(str.length() > 0 && str.lastIndexOf("/n") != -1){
//                str = str.substring(0,str.length());
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return str;
//    }
//
//    private String getHome(String betTypeName, String betSelection){
//        if(StringUtils.isEmpty(betTypeName) || StringUtils.isEmpty(betSelection)){
//            return "";
//        }
//        if(betTypeName.equalsIgnoreCase("WIN")) {
//            if (betSelection.equalsIgnoreCase("home")) {
//                return "主";
//            } else if (betSelection.equalsIgnoreCase("away")) {
//                return "客";
//            }
//        }
//        if(betTypeName.equalsIgnoreCase("1X2") || betTypeName.equalsIgnoreCase("SP1X2")) {
//            if (betSelection.equalsIgnoreCase("home")) {
//                return "主";
//            } else if (betSelection.equalsIgnoreCase("away")) {
//                return "客";
//            }else if (betSelection.equalsIgnoreCase("draw")) {
//                return "和";
//            }
//        }
//        if(betTypeName.equalsIgnoreCase("AH") || betTypeName.equalsIgnoreCase("SPWINMAP")
//                || betTypeName.equalsIgnoreCase("WINMAP")
//                || betTypeName.equalsIgnoreCase("SPHA") || betTypeName.equalsIgnoreCase("SPAH")){
//            if (betSelection.equalsIgnoreCase("home")) {
//                return "主";
//            } else if (betSelection.equalsIgnoreCase("away")) {
//                return "客";
//            }
//        }
//        if(betTypeName.equalsIgnoreCase("OU") || betTypeName.equalsIgnoreCase("SPOU")) {
//            if (betSelection.equalsIgnoreCase("over")) {
//                return "大";
//            } else if (betSelection.equalsIgnoreCase("under")) {
//                return "小";
//            }
//        }
//        if(betTypeName.equalsIgnoreCase("SPYN")) {
//            if (betSelection.equalsIgnoreCase("yes")) {
//                return "是";
//            } else if (betSelection.equalsIgnoreCase("no")) {
//                return "否";
//            }
//        }
//        if(betTypeName.equalsIgnoreCase("SPOE") || betTypeName.equalsIgnoreCase("OE")) {
//            if (betSelection.equalsIgnoreCase("odd")) {
//                return "单";
//            } else if (betSelection.equalsIgnoreCase("even")) {
//                return "双";
//            }
//        }
//        if(betTypeName.equalsIgnoreCase("SPMOR")) {
//            return "Special Proposition Multi Outright";
//        }
//        if(betTypeName.equalsIgnoreCase("SPOEU")) {
//            return "Special Proposition Over Equal Under";
//        }
//        if(betTypeName.equalsIgnoreCase("SPMM")) {
//            return "Special Proposition Min Max";
//        }
//        if(betTypeName.equalsIgnoreCase("SPRLE")) {
//            return "Special Proposition Range Less Than Equal ";
//        }
//        if(betTypeName.equalsIgnoreCase("SP777")) {
//            return "Special Proposition 777";
//        }
//        if(betTypeName.equalsIgnoreCase("SPAD")) {
//            return "Special Proposition Attack Defend";
//        }
//        return betSelection;
//    }
//
//    private String ticketType(TfOrderInfoVO tfOrderInfoVO){
//        if(StringUtils.isNotEmpty(tfOrderInfoVO.getTicketType())){
//            if(tfOrderInfoVO.getTicketType().equals("db")){
//                return "早盘";
//            }
//            if(tfOrderInfoVO.getTicketType().equals("live")){
//                return "滚球";
//            }
//        }
//        return "";
//    }
//
//    private String marketOptionName(String marketOption){
//        if(StringUtils.isNotEmpty(marketOption)){
//            if(marketOption.equals("match")){
//                return "总局";
//            }else if(marketOption.equals("outright")){
//                return "冠军盘";
//            }else if(marketOption.equals("map")){
//                return "局";
//            }
//        }
//        return "";
//    }
//
//    private String mapNum(String mapNum){
//        if(StringUtils.isNotEmpty(mapNum)){
//            if(mapNum.equals("MAP 1")){
//                return "第一局";
//            }else if (mapNum.equals("Q1")){
//                return "第一节";
//            }
//            else if (mapNum.equals("R1")){
//                return "第一局";
//            }
//            else if (mapNum.equals("Q1")){
//                return "上半场";
//            }else if (mapNum.equals("SECOND HALF")){
//                return "下半场";
//            }
//        }
//
//        return "";
//    }
//
//    private String betTypeName(TfOrderInfoVO tfOrderInfoVO) {
//        if(StringUtils.isEmpty(tfOrderInfoVO.getBetTypeName())){
//            return "";
//        }
//        if(tfOrderInfoVO.getBetTypeName().equals("WIN")){
//            return "主盘口独赢";
//        }else if(tfOrderInfoVO.getBetTypeName().equals("1X2")){
//            return "独赢";
//        }
//        else if(tfOrderInfoVO.getBetTypeName().equals("AH")){
//            return "让分局";
//        }else if(tfOrderInfoVO.getBetTypeName().equals("OU")){
//            return "大小";
//        }else if(tfOrderInfoVO.getBetTypeName().equals("OE")){
//            return "单双";
//        }else if(tfOrderInfoVO.getBetTypeName().equals("SPWINMAP")){
//            return "局独赢";
//        }else if(tfOrderInfoVO.getBetTypeName().equals("WINMAP")){
//            return "局独赢比分";
//        }else if(tfOrderInfoVO.getBetTypeName().equals("SPHA")){
//            return "特别主客";
//        }else if(tfOrderInfoVO.getBetTypeName().equals("SPYN")){
//            return "特别是否";
//        }else if(tfOrderInfoVO.getBetTypeName().equals("SPOE")){
//            return "特别单双";
//        }else if(tfOrderInfoVO.getBetTypeName().equals("SPOU")){
//            return "特别大小";
//        }else if(tfOrderInfoVO.getBetTypeName().equals("SP1X2")){
//            return "特别1X2";
//        }else if(tfOrderInfoVO.getBetTypeName().equals("OR")){
//            return "冠军盘";
//        }else if(tfOrderInfoVO.getBetTypeName().equals("SPOR")){
//            return "特别多项";
//        }else if(tfOrderInfoVO.getBetTypeName().equals("SPXX")){
//            return "特别双项";
//        }else if(tfOrderInfoVO.getBetTypeName().equals("SPMOR")){
//            return "Special Proposition Multi Outright";
//        }else if(tfOrderInfoVO.getBetTypeName().equals("SPOEU")){
//            return "Special Proposition Over Equal Under";
//        }else if(tfOrderInfoVO.getBetTypeName().equals("SPMM")){
//            return "Special Proposition Min Max";
//        }else if(tfOrderInfoVO.getBetTypeName().equals("SPRLE")){
//            return "Special Proposition Range Less Than Equal";
//        }else if(tfOrderInfoVO.getBetTypeName().equals("SP777")){
//            return "Special Proposition 777";
//        }else if(tfOrderInfoVO.getBetTypeName().equals("SPAD")){
//            return "Special Proposition Attack Defend";
//        }
//        return "";
//    }
//
//
//    public static Long convertUtcToTimestamp(String utcDateTime) {
//        try{
//            if(StringUtils.isEmpty(utcDateTime)){
//                return null;
//            }
//            // 解析时间字符串为OffsetDateTime（假设输入是UTC时间）
//            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
//            OffsetDateTime offsetDateTime = OffsetDateTime.parse(utcDateTime, formatter);
//
//            // 转换为Instant（UTC时区）
//            Instant instant = offsetDateTime.toInstant();
//
//            // 返回时间戳（秒级）
//            return instant.toEpochMilli();
//        }catch (Exception e){
//            e.printStackTrace();
//            // 时间格式转换异常
//            try{
//                // 使用ISO 8601标准的日期时间格式，能够处理微秒精度和 UTC 时区
//                DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
//
//                // 解析时间字符串为 ZonedDateTime
//                ZonedDateTime zonedDateTime = ZonedDateTime.parse(utcDateTime, formatter);
//
//                // 转换为 Unix 时间戳（秒）
//                long timestampInSeconds = zonedDateTime.toEpochSecond();
//                System.out.println("时间戳（秒）: " + timestampInSeconds);
//
//                // 转换为 Unix 时间戳（毫秒）
//                long timestampInMillis = zonedDateTime.toInstant().toEpochMilli();
//                System.out.println("时间戳（毫秒）: " + timestampInMillis);
//                return timestampInMillis;
//            }catch (Exception ex){
//                ex.printStackTrace();
//            }
//            return null;
//        }
//    }
//
//    public static String toSetRedText(String text) {
//        if (ObjectUtil.isEmpty(text)) {
//            return "";
//        }
//        text = "<span style=\"color: red;\">" + text + "</span>";
//        return text;
//    }
//
//    public static String splitEventName(String eventName, String betSection) {
//        List<String> list = new ArrayList<>(2);
//        try{
//            if(StringUtils.isNotEmpty(eventName) && eventName.contains("vs")){
//                String[] split = eventName.split("vs");
//                if(split.length == 2 && betSection.equals("主队")){
//                    return split[0];
//                }
//                if(split.length == 2 && betSection.equals("客队")){
//                    return split[1];
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return eventName;
//    }
//}

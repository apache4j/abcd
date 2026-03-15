package com.cloud.baowang.play.game.db.acelt;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.play.game.acelt.enums.AceLtBetResultStatusEnums;
import com.cloud.baowang.play.po.OrderRecordPO;
import com.cloud.baowang.play.util.OrderRecordInfoTitleUtil;
import com.google.common.collect.Maps;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class DBAceLtOrderParseUtil {

    /**
     * 获取名称
     * 游戏名称
     */
    public static String getGameNameInfo(final Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();
        if (map.containsKey("ticketName")) {
            OrderRecordInfoTitleUtil.setAceGameNameTitle(stringBuilder);
            stringBuilder.append(map.get("ticketName")).append("\n");
        }

        if (map.containsKey("ticketPlanNo")) {
            OrderRecordInfoTitleUtil.setAceTableNumberTitle(stringBuilder);
            stringBuilder.append(map.get("ticketPlanNo")).append("\n");
        }

        return stringBuilder.toString();
    }


    /**
     * 获取注单单关，串关投注单信息
     *
     * @param map
     * @return
     */
    public static String getOrderInfo(final Map<String, Object> map) {
        return map.get("ticketResult") + "@" + map.get("odd");
    }

    public static Object getPlayNameInfo(Map<String, Object> map) {
        if (map.containsKey("playName")) {
            return map.get("playName");
        }
        return null;
    }

    public static String buildOdds(String odds){
        Map<String, String> oddMap = JSON.parseObject(odds, Map.class);
        StringBuilder sb = new StringBuilder();
        for (Object value : oddMap.values()) {
            if (value != null) {
                 BigDecimal odd = new BigDecimal(value.toString());
                sb.append("@").append(odd.setScale(2, RoundingMode.DOWN));
            }
        }
        return sb.toString();
    }

    public static Object betOrderInfo(Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();
        if (map.containsKey("betContent") && map.get("betContent") != null) {
            String betStr = (String) map.get("betContent");
            OrderRecordInfoTitleUtil.setBetTitle(stringBuilder);
            stringBuilder.append(betStr).append("\n");
        }

        if (map.containsKey("odd")) {
            String odd = String.valueOf(map.get("odd"));
            Map<String, String> oddMap = JSON.parseObject(odd, Map.class);
            StringBuilder sb = new StringBuilder();
            for (Object value : oddMap.values()) {
                sb.append("@").append(value);
            }
            OrderRecordInfoTitleUtil.setOddsTitle(stringBuilder);
            stringBuilder.append(sb).append("\n");
        }

        if (map.containsKey("betMultiple")) {
            OrderRecordInfoTitleUtil.setMultipleTitle(stringBuilder);
            stringBuilder.append(map.get("betMultiple")).append("\n");
        }

        if (map.containsKey("betNums")) {
            OrderRecordInfoTitleUtil.setBetCountTitle(stringBuilder);
            stringBuilder.append(map.get("betNums")).append("\n");
        }

        return stringBuilder.toString();
    }

    public static Object getSettleTime(Map<String, Object> map) {
        if (map.containsKey("updateAt")) {
              String updateAtStr = String.valueOf(map.get("updateAt")) ;
              return fromDateToTimestamp(updateAtStr);
        }
        return null;
    }

    public static Long fromDateToTimestamp(String dateStr) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        LocalDateTime localDateTime = LocalDateTime.parse(dateStr, formatter);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Shanghai"));

        return zonedDateTime.toInstant().toEpochMilli();
    }

    public static String getStatus(Map<String, Object> map) {
        return AceLtBetResultStatusEnums.nameOfCode((Integer) map.get("state"));
    }



    /**
     * db彩票的注单详情
     */
    public static List<Map<String, Object>> getDetailOrderInfo(Map<String, Object> map) {
        List<Map<String, Object>> resultList = Lists.newArrayList();
        Map<String, Object> weMap = Maps.newHashMap();
        // 彩种详情
        weMap.put("aceltInfo", DBAceLtOrderParseUtil.getGameNameInfo(map));
        // 玩法详情
        weMap.put("playNameInfo", DBAceLtOrderParseUtil.getPlayNameInfo(map));
        // 注单详情
        weMap.put("betOrderInfo", DBAceLtOrderParseUtil.betOrderInfo(map));
        // 结算时间
        weMap.put("settleTime", DBAceLtOrderParseUtil.getSettleTime(map));


        StringBuilder betOrderResult = new StringBuilder();

        if (map.containsKey("betStatus")) {
            Integer state = (Integer) map.get("betStatus");
            //1：待开奖；2：未中奖；3：已中奖；4：挂起；5：已结算。
            OrderRecordInfoTitleUtil.setWinOrLoseResultTitle(betOrderResult);
            if (state == 2) {
                OrderRecordInfoTitleUtil.setLoseTitle(betOrderResult);
            } else if (state == 3) {
                OrderRecordInfoTitleUtil.setWinTitle(betOrderResult);
            }
            betOrderResult.append("\n");
        }


        //开奖结果
        if (map.containsKey("ticketResult")) {
            String lotteryNum = (String) map.get("ticketResult");
            if (ObjectUtil.isNotEmpty(lotteryNum)) {
                OrderRecordInfoTitleUtil.setLotteryResultsTitle(betOrderResult);
                betOrderResult.append(lotteryNum);
            }
        }
        weMap.put("betOrderResult", betOrderResult.toString());
        resultList.add(weMap);
        return resultList;
    }


    public static String getOrderRecordInfo(OrderRecordPO recordPO) {

        StringBuilder stringBuilder = new StringBuilder();

        try {

            Map<String, Object> map = JSONObject.parseObject(recordPO.getParlayInfo(), Map.class);
            if (map.containsKey("ticketName")) {
                String gameName = (String) map.get("ticketName");
                if (ObjectUtil.isNotEmpty(gameName)) {
                    OrderRecordInfoTitleUtil.setAceGameNameTitle(stringBuilder);
                    stringBuilder.append(gameName).append("\n");
                }
            }

            if (map.containsKey("ticketPlanNo")) {
                String issueNo = (String) map.get("ticketPlanNo");
                if (ObjectUtil.isNotEmpty(issueNo)) {
                    OrderRecordInfoTitleUtil.setAceTableNumberTitle(stringBuilder);
                    stringBuilder.append(issueNo).append("\n");
                }
            }

            if (map.containsKey("playName")) {
                OrderRecordInfoTitleUtil.setPlayTypeTitle(stringBuilder);
                stringBuilder.append(DBAceLtOrderParseUtil.getPlayNameInfo(map)).append("\n");
            }

            if (map.containsKey("betContent") && map.get("betContent") != null) {
                String betStr = (String) map.get("betContent");
                OrderRecordInfoTitleUtil.setBetTitle(stringBuilder);
                stringBuilder.append(betStr).append("\n");
            }

            if (map.containsKey("odd")) {
                String odd = String.valueOf(map.get("odd"));
                Map<String, String> oddMap = JSON.parseObject(odd, Map.class);
                StringBuilder sb = new StringBuilder();
                for (Object value : oddMap.values()) {
                    sb.append("@").append(value);
                }
                OrderRecordInfoTitleUtil.setOddsTitle(stringBuilder);
                stringBuilder.append(sb).append("\n");
            }



            if (map.containsKey("ticketResult")) {
                String lotteryNum = (String) map.get("ticketResult");
                if (ObjectUtil.isNotEmpty(lotteryNum)) {
                    OrderRecordInfoTitleUtil.setLotteryResultsTitle(stringBuilder);
                    stringBuilder.append(lotteryNum).append("\n");
                }
            }
        } catch (Exception e) {
            return null;
        }
        return stringBuilder.toString();
    }

    public static String toSetRedText(String text) {
        if (ObjectUtil.isEmpty(text)) {
            return null;
        }
        text = "<span style=\"color: red;\">" + text + "</span>";
        return text;
    }
}

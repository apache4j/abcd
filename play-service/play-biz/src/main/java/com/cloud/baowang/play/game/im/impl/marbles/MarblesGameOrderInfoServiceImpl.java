package com.cloud.baowang.play.game.im.impl.marbles;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.VenueOrderInfoService;
import com.cloud.baowang.play.po.OrderRecordPO;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service(ServiceType.GAME_INFO_THIRD_API_SERVICE + VenuePlatformConstants.MARBLES)
public class MarblesGameOrderInfoServiceImpl  implements VenueOrderInfoService {

    @Override
    public String getGameVenueCode() {
        return VenuePlatformConstants.MARBLES;
    }

    @Override
    public List<Map<String, Object>> getOrderInfo(Map<String, Object> map) {
        List<Map<String, Object>> resultList = Lists.newArrayList();
        Map<String, Object> weMap = Maps.newHashMap();
        // 彩种详情
        weMap.put("aceltInfo", getGameNameInfo(map));
        // 玩法详情
        if(map.containsKey("BetOn")){
            weMap.put("playNameInfo", (String) map.get("BetOn"));
        }
        // 注单详情
        StringBuilder stringBuilder = new StringBuilder();
        String betDetails = (String) map.get("BetDetails");
        Map<String,String> data= changeMap(betDetails);
        if (map.containsKey("BetType")) {
            String nums = (String) map.get("BetType");
            if (ObjectUtil.isNotEmpty(nums)) {
                stringBuilder.append("下注:");
                stringBuilder.append(nums);
            }
            if (data.containsKey("Odds")) {
                String curOdd = String.valueOf(data.get("Odds"));
                if (ObjectUtil.isNotEmpty(curOdd)) {
                    stringBuilder.append("\n");
                    stringBuilder.append("赔率：").append(curOdd);
                }
            }
        }
        if (ObjectUtils.isNotEmpty(stringBuilder)){
            weMap.put("betOrderInfo", stringBuilder.toString());
        }


        if (data.containsKey("Result")) {
            String lotteryNum = (String) data.get("Result");
            if (ObjectUtil.isNotEmpty(lotteryNum)) {
                stringBuilder.append("开奖结果:");
                stringBuilder.append(lotteryNum).append("\n");
            }
        }

        // 结算时间
        if (map.containsKey("SettlementDate") && ObjectUtils.isNotEmpty(map.get("SettlementDate"))){
            weMap.put("settleTime",changTime(map.get("SettlementDate").toString()));
        }
        StringBuilder betOrderResult = new StringBuilder();
        //开奖结果
        if (data.containsKey("Result")) {
            String lotteryNum = (String) data.get("Result");
            if (ObjectUtil.isNotEmpty(lotteryNum)) {
                betOrderResult.append("开奖结果: ").append(lotteryNum);
            }
        }
        weMap.put("betOrderResult", betOrderResult.toString());
        resultList.add(weMap);
        return resultList;
    }
    /**
     * 获取名称
     * 游戏名称
     */
    public static String getGameNameInfo(final Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();
        if (map.containsKey("ChineseGameName")) {
            stringBuilder.append("游戏名称:").append(map.get("ChineseGameName")).append("\n");
        }

        if (map.containsKey("GameNo")) {
            stringBuilder.append("期号:").append(map.get("GameNo"));
        }

        return stringBuilder.toString();
    }

    public static long changTime(String timeStr){
        // 定义格式化器（匹配输入格式）
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss XXX");
        // 解析字符串为 ZonedDateTime
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(timeStr, formatter);
        // 转换为 Unix 时间戳（毫秒级）
        return zonedDateTime.toInstant().toEpochMilli();
    }

    public String getOrderRecordInfo(OrderRecordPO recordPO) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            Map<String, Object> map = JSONObject.parseObject(recordPO.getParlayInfo(), Map.class);
            if (map.containsKey("ChineseGameName")) {
                String gameName = (String) map.get("ChineseGameName");
                if (ObjectUtil.isNotEmpty(gameName)) {
                    stringBuilder.append(gameName).append("\n");
                }
            }
            if (map.containsKey("GameNo")) {
                String issueNo = (String) map.get("GameNo");
                if (ObjectUtil.isNotEmpty(issueNo)) {
                    stringBuilder.append("期号:");
                    stringBuilder.append(issueNo).append("\n");
                }
            }
            if (map.containsKey("BetOn")) {
                stringBuilder.append("玩法:");
                stringBuilder.append((String) map.get("BetOn")).append("\n");
            }
            String betDetails = (String) map.get("BetDetails");
            Map<String,String> data= changeMap(betDetails);
            if (map.containsKey("BetType")) {
                String nums = (String) map.get("BetType");
                if (ObjectUtil.isNotEmpty(nums)) {
                    stringBuilder.append("下注:");
                    stringBuilder.append(toSetRedText(nums));
                }
                if (data.containsKey("Odds")) {
                    String curOdd = String.valueOf(data.get("Odds"));
                    if (ObjectUtil.isNotEmpty(curOdd)) {
                        stringBuilder.append("@").append(curOdd).append("\n");
                    }
                }
            }
            if (data.containsKey("Result")) {
                String lotteryNum = (String) data.get("Result");
                if (ObjectUtil.isNotEmpty(lotteryNum)) {
                    stringBuilder.append("开奖结果:");
                    stringBuilder.append(lotteryNum).append("\n");
                }
            }
        } catch (Exception e) {
            return null;
        }
        return stringBuilder.toString();
    };

    private Map<String,String> changeMap(String str){
        Map<String, String> betDataMap = new HashMap<>();

        // 按分号分割各部分
        String[] parts = str.split(";\\s*");
        for (String part : parts) {
            // 分割键值对
            String[] keyValue = part.split(":\\s*", 2);
            if (keyValue.length != 2) continue;
            String key = keyValue[0].trim();
            String value = keyValue[1].trim();
            betDataMap.put(key,value);
        }
        return betDataMap;
    }

    public static String toSetRedText(String text) {
        if (ObjectUtil.isEmpty(text)) {
            return null;
        }
        text = "<span style=\"color: red;\">" + text + "</span>";
        return text;
    }

    public static void main(String[] args) {
        OrderRecordPO recordPO = new OrderRecordPO();
        String str = "{\"BetAmount\":10.0000,\"BetDate\":\"2025-05-01 14:28:03 +08:00\",\"BetDetails\":\"Odds: 1.65; Result: 9,5,1,7,4,10,2,6,3,8; Bet result: 1. Win\",\"BetId\":\"6671868\",\"BetOn\":\"两面;冠军;冠军\",\"BetType\":\"单\",\"ChineseGameName\":\"幸运农场 - 娱乐版\",\"Currency\":\"CNY\",\"DateCreated\":\"2025-05-01 14:28:03 +08:00\",\"GameId\":\"imlotto60006\",\"GameName\":\"Pirelli Speedway - Casual\",\"GameNo\":\"9650792\",\"GameNoId\":\"1\",\"LastUpdatedDate\":\"2025-05-01 14:30:10 +08:00\",\"Odds\":\"\",\"Platform\":\"NA\",\"PlayerId\":\"Utest_46378544\",\"PlayerWinLoss\":0.0000,\"Provider\":\"MM_LOTTERY\",\"ProviderPlayerId\":\"im2iy_utest_46378544\",\"ResultDate\":\"2025-05-01 14:30:09 +08:00\",\"SettlementDate\":\"2025-05-01 14:30:09 +08:00\",\"Status\":\"Settled\",\"Tray\":\"\",\"ValidBet\":10.0000,\"WinLoss\":6.5000}";
        recordPO.setParlayInfo(str);
        MarblesGameOrderInfoServiceImpl impl = new MarblesGameOrderInfoServiceImpl();
        System.out.println(impl.getOrderRecordInfo(recordPO));
        JSONObject jsonObject = JSONObject.parseObject("{\"BetAmount\":10.0000,\"BetDate\":\"2025-05-01 14:28:03 +08:00\",\"BetDetails\":\"Odds: 1.65; Result: 9,5,1,7,4,10,2,6,3,8; Bet result: 1. Win\",\"BetId\":\"6671868\",\"BetOn\":\"两面;冠军;冠军\",\"BetType\":\"单\",\"ChineseGameName\":\"幸运农场 - 娱乐版\",\"Currency\":\"CNY\",\"DateCreated\":\"2025-05-01 14:28:03 +08:00\",\"GameId\":\"imlotto60006\",\"GameName\":\"Pirelli Speedway - Casual\",\"GameNo\":\"9650792\",\"GameNoId\":\"1\",\"LastUpdatedDate\":\"2025-05-01 14:30:10 +08:00\",\"Odds\":\"\",\"Platform\":\"NA\",\"PlayerId\":\"Utest_46378544\",\"PlayerWinLoss\":0.0000,\"Provider\":\"MM_LOTTERY\",\"ProviderPlayerId\":\"im2iy_utest_46378544\",\"ResultDate\":\"2025-05-01 14:30:09 +08:00\",\"SettlementDate\":\"2025-05-01 14:30:09 +08:00\",\"Status\":\"Settled\",\"Tray\":\"\",\"ValidBet\":10.0000,\"WinLoss\":6.5000}");
        Map<String, Object> parlayMap = jsonObject.getInnerMap();
        System.out.println(impl.getOrderInfo(parlayMap));

    }

}

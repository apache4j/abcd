package com.cloud.baowang.play.game.dg2.vo;

import com.alibaba.fastjson2.JSON;
import java.util.HashMap;
import java.util.Map;

public class BjlParser {

    /**
     * 解析开牌结果
     */
    public static BjlResult parseResult(String source) {
        Map<String, Object> map = JSON.parseObject(source, Map.class);

        // 取牌
        Map<String, String> pokerMap = (Map<String, String>) map.get("poker");
        String bankerStr = pokerMap.get("banker");
        String playerStr = pokerMap.get("player");

        // 计算点数
        int bankerPoint = cardPoint(bankerStr);
        int playerPoint = cardPoint(playerStr);

        // 解析 result 数组中的大小
        String result = (String) map.get("result"); // 例如 "1,2,9"
        String[] arr = result.split(",");
        String size = null;
        if (arr.length > 1 && !"-1".equals(arr[1])) {
            size = arr[1].equals("1") ? "Small" : "Big";
        }

        // 封装
        BjlResult bjlResult = new BjlResult();
        bjlResult.setBankerPoint(bankerPoint);
        bjlResult.setPlayerPoint(playerPoint);
        bjlResult.setPoker(pokerMap);
        bjlResult.setSize(size);

        return bjlResult;
    }

    /**
     * 解析下注明细
     */
    public static BetDetail parseBetDetail(String source) {
        Map<String, Object> map = JSON.parseObject(source, Map.class);

        BetDetail betDetail = new BetDetail();
        if (map.containsKey("info")) {
            betDetail.setInfo((String) map.get("info"));
        }

        Map<String, Object> bets = new HashMap<>(map);
        bets.remove("info");
        betDetail.setBets(bets);

        return betDetail;
    }

    /**
     * 计算牌点数
     */
    private static int cardPoint(String cards) {
        if (cards == null || cards.isEmpty()) {
            return 0;
        }
        String[] arr = cards.split("-");
        int total = 0;
        for (String c : arr) {
            int num = Integer.parseInt(c);
            int value = num % 13;
            if (value >= 10) {
                value = 0;
            }
            total += (value == 0 ? 10 : value);
        }
        return total % 10;
    }
}

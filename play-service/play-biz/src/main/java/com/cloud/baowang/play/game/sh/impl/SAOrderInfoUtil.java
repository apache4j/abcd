package com.cloud.baowang.play.game.sh.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.play.game.sa.*;
import com.cloud.baowang.play.po.OrderRecordPO;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class SAOrderInfoUtil {

    /**
     * 获取出结果牌
     *
     * @param resultDetail 数据源的JSON
     * @param gameType     游戏大类
     */
    public static String getSaResultString(JSONObject resultDetail, String gameType) {
        StringBuilder stringBuilder = new StringBuilder();
        if (resultDetail == null) {
            return null;
        }
        //至尊轮盘 是独立的解析规则
        if (SAGameTypeEnum.ULTRAROULETTE.getCode().equals(gameType)) {
            if (resultDetail.containsKey("Point")) {
                Integer point = (Integer) resultDetail.get("Point");
                stringBuilder.append(point);//结果牌
            }

            if (resultDetail.containsKey("Point")) {
                Integer multiplier = (Integer) resultDetail.get("Multiplier");
                stringBuilder.append(" ").append(multiplier);//结果牌 倍数
            }

            if (resultDetail.containsKey("ResultDetail")) {
                JSONObject resultDetailJson = JSONUtil.parseObj(resultDetail.getStr("ResultDetail"));
                stringBuilder.append("\n");
                for (String key : resultDetailJson.keySet()) {
                    Object result = resultDetailJson.get(key);
                    if (result instanceof Boolean) {
                        Boolean bool = resultDetailJson.getBool(key);
                        if (!bool) {
                            continue;
                        }
                        //游戏类型 + sa_game_detail 获取到游戏类型说明集合
                        stringBuilder.append(I18nMessageUtil.getSystemParamAndTrans(gameType + "_" + CommonConstant.SA_GAME_DETAIL, key))
                                .append("、");
                    }
                }
            }


            stringBuilder.append("\n");
            if (resultDetail.containsKey("MultiplierDetails")) {
                JSONObject multiplierDetails = JSONUtil.parseObj(resultDetail.getStr("MultiplierDetails"));
                if (multiplierDetails.containsKey("MultiplierEntry")) {

                    Object multiplierEntryObj = multiplierDetails.get("MultiplierEntry");
                    JSONArray multiplierEntry = new JSONArray();
                    if (multiplierEntryObj instanceof JSONArray) {
                        multiplierEntry = multiplierDetails.getJSONArray("MultiplierEntry");
                    } else {
                        JSONObject multiplierEntryJson = JSONUtil.parseObj(multiplierDetails.getStr("MultiplierEntry"));
                        multiplierEntry.add(multiplierEntryJson);
                    }

                    for (int i = 0; i < multiplierEntry.size(); i++) {
                        JSONObject obj = JSONUtil.parseObj(multiplierEntry.getStr(i));
                        Integer betType = obj.getInt("BetType");
                        if (betType != null) {
                            String betTypeMsg = I18nMessageUtil.getSystemParamAndTrans(String.format(CommonConstant.SA_BET_TYPE, gameType), String.valueOf(betType));
                            if (betTypeMsg != null) {
                                Integer multiplier = obj.getInt("Multiplier");
                                if (multiplier != null) {
                                    stringBuilder.append(betTypeMsg).append(multiplier).append("、");
                                }
                            }
                        }
                    }
                }
            }
        } else if (SAGameTypeEnum.BAC.getCode().equals(gameType)) {
            JSONObject baccaratResult = JSONUtil.parseObj(resultDetail.getStr("BaccaratResult"));

            //闲
            stringBuilder.append(I18nMessageUtil.getSystemParamAndTrans(gameType + "_" + CommonConstant.SA_GAME_DETAIL,
                    SAGameBacResultEnum.BRPlayerWin.getCode())).append(":");

            String playerCard1 = getSaBaccaratResultResult(JSONUtil.parseObj(baccaratResult.getStr("PlayerCard1")));
            if (playerCard1 != null) {
                stringBuilder.append(playerCard1);
                if(ObjectUtil.isNotEmpty(playerCard1)){
                    stringBuilder.append(",");
                }
            }
            String playerCard2 = getSaBaccaratResultResult(JSONUtil.parseObj(baccaratResult.getStr("PlayerCard2")));
            if (playerCard2 != null) {
                stringBuilder.append(playerCard2);
                if(ObjectUtil.isNotEmpty(playerCard2)){
                    stringBuilder.append(",");
                }
            }
            String playerCard3 = getSaBaccaratResultResult(JSONUtil.parseObj(baccaratResult.getStr("PlayerCard3")));
            if (playerCard3 != null) {
                stringBuilder.append(playerCard3);
                if(ObjectUtil.isNotEmpty(playerCard3)){
                    stringBuilder.append(",");
                }
            }

            //庄
            stringBuilder.append(I18nMessageUtil.getSystemParamAndTrans(SAGameTypeEnum.BAC.getCode() + "_" + CommonConstant.SA_GAME_DETAIL,
                    SAGameBacResultEnum.BRBankerWin.getCode()));

            String bankerCard1 = getSaBaccaratResultResult(JSONUtil.parseObj(baccaratResult.getStr("BankerCard1")));
            if (bankerCard1 != null) {
                stringBuilder.append(bankerCard1);
                if(ObjectUtil.isNotEmpty(bankerCard1)){
                    stringBuilder.append(",");
                }
            }
            String bankerCard2 = getSaBaccaratResultResult(JSONUtil.parseObj(baccaratResult.getStr("BankerCard2")));
            if (bankerCard2 != null) {
                stringBuilder.append(bankerCard2);
                if(ObjectUtil.isNotEmpty(bankerCard2)){
                    stringBuilder.append(",");
                }
            }

            String bankerCard3 = getSaBaccaratResultResult(JSONUtil.parseObj(baccaratResult.getStr("BankerCard3")));
            if (bankerCard3 != null) {
                stringBuilder.append(bankerCard3);
                if(ObjectUtil.isNotEmpty(bankerCard3)){
                    stringBuilder.append(",");
                }
            }

            stringBuilder.append("\n");
            JSONObject absResultDetail = JSONUtil.parseObj(baccaratResult.getStr("ResultDetail"));

            stringBuilder.append(getSAResultBool(absResultDetail, gameType));
        } else if (gameType.equals(SAGameTypeEnum.DTX.getCode())) {
            JSONObject detailJson = JSONUtil.parseObj(resultDetail.getStr("ResultDetail"));
            stringBuilder.append(getSAResultBool(detailJson, gameType));

            JSONObject dragonCardJson = JSONUtil.parseObj(resultDetail.getStr("DragonCard"));
            //龙

            stringBuilder.append(I18nMessageUtil.getSystemParamAndTrans(gameType + "_" + CommonConstant.SA_GAME_DETAIL,
                            SAGameDtxResultEnum.DTR_DRAGON_WIN.getCode())).append(":")
                    .append(getSaBaccaratResultResult(dragonCardJson)).append(",");


            //虎
            JSONObject tigerCardJson = JSONUtil.parseObj(resultDetail.getStr("TigerCard"));

            stringBuilder.append(I18nMessageUtil.getSystemParamAndTrans(gameType + "_" + CommonConstant.SA_GAME_DETAIL,
                            SAGameDtxResultEnum.DTR_TIGER_WIN.getCode())).append(":")
                    .append(getSaBaccaratResultResult(tigerCardJson));


        } else if (gameType.equals(SAGameTypeEnum.POKDENG.getCode())) {

            JSONObject blackjackResult = JSONUtil.parseObj(resultDetail.getStr("PokDengResult"));

            JSONObject resultDetailJson = JSONUtil.parseObj(blackjackResult.getStr("ResultDetail"));

            for (String key : resultDetailJson.keySet()) {

                //总点数
                if (key.endsWith("Point")) {

                    // 去掉 Point 得到前缀
                    String prefix = key.replace("Point", "");

                    Integer point = resultDetailJson.getInt(key);

                    SaPokdengPointEnum saPokdengPointEnum = SaPokdengPointEnum.fromCode(point);
                    if (saPokdengPointEnum == null) {
                        continue;
                    }
                    if (point > 0) {
                        stringBuilder.append(I18nMessageUtil.getSystemParamAndTrans(gameType + "_" + CommonConstant.SA_GAME_DETAIL, key))
                                .append(saPokdengPointEnum.getDesc()).append("、");
                    }


                    String pairKey = prefix + "Pair";
                    if (resultDetailJson.containsKey(pairKey)) {
                        Boolean bool = resultDetailJson.getBool(pairKey);
                        if (bool) {
                            stringBuilder.append(I18nMessageUtil.getSystemParamAndTrans(gameType + "_" + CommonConstant.SA_GAME_DETAIL, pairKey)).append("、");
                        }
                    }


                    String resultKey = prefix + "Result";
                    if (resultDetailJson.containsKey(resultKey)) {
                        Integer result = resultDetailJson.getInt(resultKey);
                        SAPokdengBoolResultEnum resultEnum = SAPokdengBoolResultEnum.fromCode(result);
                        if (resultEnum != null) {
                            stringBuilder.append(I18nMessageUtil.getSystemParamAndTrans(gameType + "_" + CommonConstant.SA_GAME_DETAIL, resultEnum.getName())).append("、");
                        }
                    }
                }
            }
        } else if (gameType.equals(SAGameTypeEnum.BLACKJACK.getCode())) {


            // 从结果中获取 BlackjackResult
            JSONObject blackjackResult = JSONUtil.parseObj(resultDetail.getStr("BlackjackResult"));

            JSONObject resultDetailJson = JSONUtil.parseObj(blackjackResult.getStr("ResultDetail"));
            Map<String, List<String>> playersMap = Maps.newHashMap();

            //key=结果牌,v=对应玩家结果 0=下注结果,1=分牌结果
            playersMap.put("Player1", List.of("BJR1_S1", "BJR1_S2"));
            playersMap.put("Player2", List.of("BJR2_S1", "BJR2_S2"));
            playersMap.put("Player3", List.of("BJR3_S1", "BJR3_S2"));
            playersMap.put("Player5", List.of("BJR5_S1", "BJR5_S2"));
            playersMap.put("Player6", List.of("BJR6_S1", "BJR6_S2"));
            playersMap.put("Player7", List.of("BJR7_S1", "BJR7_S2"));
            playersMap.put("Player8", List.of("BJR8_S1", "BJR8_S2"));
            for (String playKey : blackjackResult.keySet()) {
                for (Map.Entry<String, List<String>> item : playersMap.entrySet()) {

                    List<String> playIndexList = item.getValue();
                    //不是分牌
                    if (playKey.startsWith(item.getKey()) && !playKey.endsWith("Split")) {

                        //结果牌投注详细
                        String betRes = playIndexList.get(0);
                        //结果详细:
                        stringBuilder.append(getSaBlackjackResult(resultDetailJson, betRes));

                        //抽取通用卡牌拼接
                        JSONObject playerJson = JSONUtil.parseObj(blackjackResult.getStr(playKey));
                        appendCards(stringBuilder, playerJson);
                        stringBuilder.append("\n");
                    }

                    //分牌
                    if (playKey.startsWith(item.getKey()) && playKey.endsWith("Split")) {

                        //分牌 结果牌投注详细
                        String betRes = playIndexList.get(1);
                        //分牌 结果详细:
                        stringBuilder.append(getSaBlackjackResult(resultDetailJson, betRes));

                        //抽取通用卡牌拼接
                        JSONObject playerJson = JSONUtil.parseObj(blackjackResult.getStr(playKey));
                        appendCards(stringBuilder, playerJson);
                        stringBuilder.append("\n");
                    }
                }
            }


            if (blackjackResult.containsKey("Banker")) {
                //庄 多语言
                stringBuilder.append(I18nMessageUtil.getSystemParamAndTrans(SAGameTypeEnum.BAC.getCode() + "_" + CommonConstant.SA_GAME_DETAIL,
                        SAGameBacResultEnum.BRBankerWin.getCode()));

                if (resultDetailJson.containsKey("BJR_BankerPoint")) {
                    Integer point = resultDetailJson.getInt("BJR_BankerPoint");
                    if (point > 0) {
                        stringBuilder.append(" ").append(point).append(" ");
                    }
                }


                stringBuilder.append(":");
                JSONObject jsonBanker = JSONUtil.parseObj(blackjackResult.getStr("Banker"));
                appendCards(stringBuilder, jsonBanker);
            }

        } else if (gameType.equals(SAGameTypeEnum.TEENPATTI2020.getCode())) {
            JSONObject teenJson = JSONUtil.parseObj(resultDetail.getStr("TeenPatti2020Result"));

            stringBuilder.append("A: ");
            for (String key : teenJson.keySet()) {
                if (key.startsWith("PlayerACard")) {
                    JSONObject playJson = JSONUtil.parseObj(teenJson.getStr(key));
                    String car = getSaBaccaratResultResult(playJson);
                    if (car != null) {
                        stringBuilder.append(car).append("、");
                    }
                }
            }


            stringBuilder.append("B: ");
            for (String key : teenJson.keySet()) {
                if (key.startsWith("PlayerBCard")) {
                    JSONObject playJson = JSONUtil.parseObj(teenJson.getStr(key));
                    String car = getSaBaccaratResultResult(playJson);
                    if (car != null) {
                        stringBuilder.append(car).append("、");
                    }
                }
            }

            stringBuilder.append("\n");
            JSONObject resultDetailJson = JSONUtil.parseObj(teenJson.getStr("ResultDetail"));
            String result = getSAResultBool(resultDetailJson, gameType);
            if (ObjectUtil.isNotEmpty(result)) {
                stringBuilder.append(result);
            }

            stringBuilder.append("\n");
            for (int i = 1; i <= 5; i++) {
                String key = "TP20RSixCard" + i;
                if (!resultDetailJson.containsKey(key)) {
                    continue;
                }
                JSONObject playJson = JSONUtil.parseObj(resultDetailJson.getStr(key));
                String car = getSaBaccaratResultResult(playJson);
                if (car != null) {
                    stringBuilder.append(car).append("、");
                }
            }

        } else {
            String result = getSAResultBool(resultDetail, gameType);
            if (ObjectUtil.isNotEmpty(result)) {
                stringBuilder.append(result);
            }
        }
        return stringBuilder.toString();
    }


    /**
     * 黑杰克 结果牌解析
     */
    private static String getSaBlackjackResult(JSONObject resultDetailJson,String resultStartKey) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String key : resultDetailJson.keySet()) {
            if (key.startsWith(resultStartKey)) {
                Object resultValue = resultDetailJson.get(key);
                if (resultValue instanceof Boolean) {
                    Boolean bool = resultDetailJson.getBool(key);
                    if (!bool) {
                        continue;
                    }
                    //游戏类型 + sa_game_detail 获取到游戏类型说明集合
                    stringBuilder.append(I18nMessageUtil.getSystemParamAndTrans(SAGameTypeEnum.BLACKJACK.getCode() + "_" + CommonConstant.SA_GAME_DETAIL, key));
                } else {
                    Integer code = resultDetailJson.getInt(key);
                    stringBuilder.append(I18nMessageUtil.getSystemParamAndTrans(SAGameTypeEnum.BLACKJACK.getCode() + "_" + CommonConstant.SA_GAME_DETAIL, key + "_" + code));
                }
                stringBuilder.append(":");
            }
        }
        return stringBuilder.toString();
    }


    /**
     * 抽取通用卡牌拼接
     */
    private static void appendCards(StringBuilder sb, JSONObject json) {
        List<String> keys = new ArrayList<>(json.keySet());
        keys.sort(Comparator.comparingInt(k -> Integer.parseInt(k.replace("Card", ""))));
        for (String key : keys) {
            if (key.startsWith("Card")) {
//                JSONObject card = json.getJSONObject(key);
                JSONObject card = JSONUtil.parseObj(json.getStr(key));
                sb.append(getSaBaccaratResultResult(card));
            }
        }
    }


    /**
     * SA的 Bool结果映射
     */
    private static String getSAResultBool(JSONObject resultDetail, String gameType) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : resultDetail.keySet()) {
            Object result = resultDetail.get(key);
            if (result instanceof Boolean) {
                Boolean bool = resultDetail.getBool(key);
                if (!bool) {
                    continue;
                }

                String detail = I18nMessageUtil.getSystemParamAndTrans(gameType + "_" + CommonConstant.SA_GAME_DETAIL, key);

                if (detail == null) {
                    return null;
                }
                //游戏类型 + sa_game_detail 获取到游戏类型说明集合
                stringBuilder.append(detail)
                        .append("、");
            } else if (result instanceof Integer) {
                Integer code = resultDetail.getInt(key);

                //针对 博丁 的 部分玩法特殊处理.他们返回的是总点数
                if (SAGameTypeEnum.POKDENG.getCode().equals(gameType)) {
                    SAPokdengResultEnum saPokdengResultEnum = SAPokdengResultEnum.valueOf(key);
                    if (saPokdengResultEnum.getType() == Integer.class) {
                        stringBuilder.append(I18nMessageUtil.getSystemParamAndTrans(gameType + "_" + CommonConstant.SA_GAME_DETAIL, key));
                        stringBuilder.append(":").append(code);
                    }
                } else {
                    String detail = I18nMessageUtil.getSystemParamAndTrans(gameType + "_" + CommonConstant.SA_GAME_DETAIL, key + "_" + code);
                    if (detail == null) {
                        continue;
                    }
                    stringBuilder.append(detail);
                }
                stringBuilder.append("、");
            }
        }
        return stringBuilder.toString();
    }


    //SA结果牌
    private static String getSaBaccaratResultResult(JSONObject baccaratResult) {
        if (baccaratResult == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        SAGameCarSuitEnum carSuitEnum = SAGameCarSuitEnum.byCode(baccaratResult.getInt("Suit"));
        if (carSuitEnum != null) {
            stringBuilder.append(carSuitEnum.getIcon()).append(" ");
        }

        SAGameCarRankEnum bankRank1 = SAGameCarRankEnum.byCode(baccaratResult.getInt("Rank"));
        if (bankRank1 != null) {
            stringBuilder.append(bankRank1.getDescription()).append(" ");
        }
        return stringBuilder.toString();
    }


    public static String getSaResultList(OrderRecordPO recordPO) {
        try {
            String gameType = recordPO.getPlayType();
            String orderInfo = recordPO.getOrderInfo();
            if (orderInfo != null) {
//                JSONObject jsonObject = new JSONObject(orderInfo, true);
                JSONObject jsonObject = JSONUtil.parseObj(orderInfo);
                return getSaResultString(jsonObject, gameType);
            }
        } catch (Exception e) {
            log.error("SA真人详情异常:{}",recordPO.getOrderId());
        }
        return null;
    }


}




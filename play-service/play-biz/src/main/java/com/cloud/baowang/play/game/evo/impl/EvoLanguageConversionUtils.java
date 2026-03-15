package com.cloud.baowang.play.game.evo.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.play.game.evo.enums.EvoGameCarSuitEnum;
import com.cloud.baowang.play.game.evo.enums.EvoGameTypeEnum;
import com.cloud.baowang.play.game.evo.enums.EvoOrderStatusEnum;
import com.cloud.baowang.play.game.sh.enums.SHBetResultEnum;
import com.cloud.baowang.play.po.OrderRecordPO;
import com.cloud.baowang.play.util.OrderRecordInfoTitleUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unchecked")
@Slf4j
public class EvoLanguageConversionUtils {


    public static String getSaResultList(OrderRecordPO recordPO) {
        Map<String, Object> map = JSONObject.parseObject(recordPO.getParlayInfo(), Map.class);
        return gameResultInfo(map);
    }


    /**
     * 该方法用于视讯的结果牌逻辑,
     *
     * @param dataSource 三方数据源
     * @return 多语言翻译
     */
    public static String conversionBetResult(String dataSource) {
        if (StringUtils.isBlank(dataSource)) {
            return dataSource;
        }

        // 正则表达式匹配大于等于 4 位的数字
        Pattern pattern = Pattern.compile("\\b\\d{4,}\\b");
        Matcher matcher = pattern.matcher(dataSource);

        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String matchedNumber = matcher.group();
            String replacement = processNumber(matchedNumber);
            matcher.appendReplacement(result, replacement);
        }

        matcher.appendTail(result);

        return result.toString();
    }


    private static String processNumber(String number) {
        SHBetResultEnum shBetResultEnum = SHBetResultEnum.ofCode(number);
        if (shBetResultEnum == null) {
            return number;
        }
        //参数是对应system_param表的type,code字段
        return I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_BET_RESULT, number);
    }

    /**
     * 游戏结果解析
     */
    public static String gameResultInfo(Map<String, Object> map) {

        String gameType = (String) map.get("gameType");

        if (map.containsKey("result")) {
            Map<String, Object> result = (Map<String, Object>) map.get("result");
            StringBuilder stringBuilder = new StringBuilder();
            String status =  (String) map.get("status");
            if (Objects.equals("Resolved",status)){
                JSONArray resolveddata = (JSONArray) map.get("participants");
                if (ObjectUtils.isNotEmpty(resolveddata)){
                    JSONObject objects = (JSONObject) resolveddata.get(0);
                    if (ObjectUtils.isNotEmpty(objects)){
                        JSONObject hands =(JSONObject) objects.get("hands");
                        if (ObjectUtils.isNotEmpty(hands)){{
                            JSONObject hand1 =  (JSONObject) hands.get("hand1");
                            JSONArray cards =  (JSONArray) hand1.get("cards");
                            List<String> cards1 =  cards.toJavaList(String.class);
                            result.put("status","Resolved");
                            result.put("cards",cards1);
                         }
                        }
                    }
                }
            }
            EvoGameTypeEnum evoGameTypeEnum = EvoGameTypeEnum.ofCode(gameType);
            if (evoGameTypeEnum == null) {
                return stringBuilder.toString();
            }
            switch (evoGameTypeEnum) {
                case AMERICANROULETTE, RNG_AMERICAN_ROULETTE, AMERICAN_RED_DOOR_ROULETTE, REDDOORROULETTE,
                     ROULETTE, INSTANTROULETTE, RNG_ROULETTE:
                    return parseRouletteOutcomes(result);
                case BACBO:
                    return bacboResult(result);

                case BALLOONRACE:
                    return parseBalloonRace(result);

                case BLACKJACK, RNG_BLACKJACK:
                    return parseBlackjack(result);

                case CSP:
                    return parseCsp(result);

                case LIGHTNINGSCALABLEBJ, RNG_LIGHTNINGSCALABLEBJ,
                     FREEBET, SCALABLEBLACKJACK, POWERSCALABLEBLACKJACK:
                    return parseLightningScalableBJ(result);

                case DRAGONTIGER, RNG_DRAGONTIGER:
                    return parseDragonTiger(result);

                case EXTRACHILLIEPICSPINS:
                    return parseExtraChilliePicSpins(map);

                case CRAPS, RNG_CRAPS:
                    return parseCraps(result);

                case LIGHTNINGDICE, SICBO:
                    return parseLightningDice(result);

                case RNG_DEALNODEAL:
                    return parseRngDeallocate(map);

                case CRAZYTIME:
                    return parseCrazyTime(map);

                case THB, DHP, ETH, HOLDEM, TCP, TRP, UTH:
                    return parseThb(result);

                case BACCARAT, RNG_BACCARAT, BACCARATEZ:
                    return baccaratResult(result);

                case ANDARBAHAR:
                    return parseAndarbahar(result);

                case CASHORCRASH:
                    return parseCashOrCrash(result);

                case FANTAN:
                    return parseFantan(result);

                case MONOPOLY:
                    return parseMonopoly(result);

                case MONEYWHEEL, RNG_MONEYWHEEL:
                    return parseMoneyWheel(result);

                case RNG_TOPCARD, TOPCARD:
                    return parseRngtopcard(result);

                case STOCKMARKET:
                    return parseStockmarket(map);

                case MEGABALL, RNG_MEGABALL, CRAZYPACHINKO:
                    return parseBetOderNo(map);
                default:
                    Map<String, Object> backerMap = (Map<String, Object>) result.get("backer");
                    if (backerMap != null) {
                        stringBuilder.append("banker");
                        // 解析 banker
                        appendCards(stringBuilder, (Map<String, Object>) result.get("banker"));
                        stringBuilder.append("\n");
                    }
                    Map<String, Object> player = (Map<String, Object>) result.get("backer");
                    if (player != null) {
                        // 解析 player
                        stringBuilder.append("player");
                        appendCards(stringBuilder, (Map<String, Object>) result.get("player"));
                        stringBuilder.append("\n");
                    }
                    return stringBuilder.toString();

            }

            /*switch (gameType) {
                // EvoGameTypeEnum
                case "americanroulette", "rng-american-roulette", "americanreddoorroulette", "reddoorroulette",
                     "roulette", "instantroulette", "rng-roulette":
                    return parseRouletteOutcomes(result);
                case "bacbo":
                    // 解析 bacbo
                    return bacboResult(result);
                case "balloonrace":
                    return parseBalloonRace(result);
                case "blackjack", "rng-blackjack":
                    return parseBlackjack(result);
                case "csp":
                    return parseCsp(result);
                case "lightningscalablebj", "rng-lightningscalablebj",
                     "freebet", "scalableblackjack", "powerscalableblackjack":
                    return parseLightningScalableBJ(result);
                case "dragontiger", "rng-dragontiger": //龙虎
                    return parseDragonTiger(result);
                case "extrachilliepicspins":
                    return parseExtraChilliePicSpins(map);
                case "craps", "rng-craps":
                    return parseCraps(result);
                case "lightningdice", "sicbo":
                    return parseLightningDice(result);
                case "rng-dealnodeal":
                    return parseRngDeallocate(map);
                *//*case "americanreddoorroulette","reddoorroulette":
                    return parseRedDoorRoulette(result);*//*
                case "crazytime":
                    return parseCrazyTime(map);
                case "thb", "dhp", "eth", "holdem", "tcp", "trp", "uth":
                    return parseThb(result);
                case "baccarat", "rng-baccarat", "baccaratez":
                    return baccaratResult(result);
                case "andarbahar":
                    return parseAndarbahar(result);
                case "cashorcrash":
                    return parseCashOrCrash(result);
                case "fantan":
                    return parseFantan(result);
                case "monopoly":
                    return parseMonopoly(result);
                case "moneywheel", "rng-moneywheel":
                    return parseMoneyWheel(result);

                case "rng-topcard", "topcard":
                    return parseRngtopcard(result);
                case "stockmarket":
                    return parseStockmarket(map);
                case "megaball", "rng-megaball":
                    return parseBetOderNo(map);

                default:
                    Map<String, Object> backerMap = (Map<String, Object>) result.get("backer");
                    if (backerMap != null) {
                        stringBuilder.append("banker");
                        // 解析 banker
                        appendCards(stringBuilder, (Map<String, Object>) result.get("banker"));
                        stringBuilder.append("\n");
                    }
                    Map<String, Object> player = (Map<String, Object>) result.get("backer");
                    if (player != null) {
                        // 解析 player
                        stringBuilder.append("player");
                        appendCards(stringBuilder, (Map<String, Object>) result.get("player"));
                        stringBuilder.append("\n");
                    }
                    return stringBuilder.toString();
            }*/

        }
        return "";
    }

    private static String parseBetOderNo(Map<String, Object> map) {
        return map.get("thirdOrderId").toString();
    }

    /**
     *
     */
    private static String parseStockmarket(Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();
        Map<String, Object> result = (Map<String, Object>) map.get("result");
        Object percentage = result.get("percentage");
        if (percentage != null) {
            stringBuilder.append(percentage).append("%").append(",");
        }
        List<Map<String, Object>> participants = (List<Map<String, Object>>) map.get("participants");
        String thirdOrderId = map.get("thirdOrderId").toString();
        if (participants != null && !participants.isEmpty()) {
            // 找到bet
            for (Map<String, Object> participant : participants) {
                List<Map<String, Object>> betArr = (List<Map<String, Object>>) participant.get("bets");
                boolean isFind = false;
                for (Map<String, Object> bet : betArr) {
                    String orderID = String.valueOf(bet.get("transactionId")) + "_" + String.valueOf(bet.get("code"));
                    if (orderID.equals(thirdOrderId)) {
                        isFind = true;
                    }
                }
                if (isFind) {

                    String index = String.valueOf(participant.get("index"));
                    stringBuilder.append(index);
                }
            }


        }
        return stringBuilder.toString();

    }

    /**
     * 第一人称顶牌
     * result.aSPot.card
     * result.aSpot.score
     * result.bSpot.card
     * result.bSpot.score
     * A<8>8D;B<7>7H
     */
    private static String parseRngtopcard(Map<String, Object> result) {
        StringBuilder stringBuilder = new StringBuilder();
        Map<String, Object> aSpot = (Map<String, Object>) result.get("aSpot");
        Map<String, Object> bSpot = (Map<String, Object>) result.get("bSpot");
        if (aSpot == null || bSpot == null) {
            return "";
        }

        // A 部分
        stringBuilder.append("A");
        Object scoreA = aSpot.get("score");
        if (scoreA != null) {
            stringBuilder.append("<").append(scoreA).append(">");
        }
        Object cardA = aSpot.get("card");
        if (cardA != null && ObjectUtil.isNotEmpty(cardA.toString())) {
            stringBuilder.append(appendCards(Collections.singletonList(cardA.toString())));
        }

        stringBuilder.append(";"); // 分隔 A 和 B

        // B 部分
        stringBuilder.append("B");
        Object scoreB = bSpot.get("score");
        if (scoreB != null) {
            stringBuilder.append("<").append(scoreB).append(">");
        }
        Object cardB = bSpot.get("card");
        if (cardB != null && ObjectUtil.isNotEmpty(cardB.toString())) {
            stringBuilder.append(appendCards(Collections.singletonList(cardB.toString())));
        }

        return stringBuilder.toString();
    }


    /**
     * 解析 moneywheel
     * result.outcomes
     */
    private static String parseMoneyWheel(Map<String, Object> result) {
        List<String> outcome = (List<String>) result.get("outcomes");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < outcome.size(); i++) {
            stringBuilder.append(outcome.get(i)).append(",");
        }
        if (!stringBuilder.isEmpty()) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    /**
     * 解析 monopoly
     * result.outcome.wheelResult
     */
    private static String parseMonopoly(Map<String, Object> result) {
        Map<String, Object> outcome = (Map<String, Object>) result.get("outcome");
        if (outcome != null) {
            Object wheelResult = outcome.get("wheelResult");
            return wheelResult + "";
        }
        return "";
    }


    /**
     * 解析番摊结果
     * <p>
     * 规则：
     * 1 , Small, Odd
     * 2 , Small, Even
     * 3 , Big, Odd
     * 4 , Big, Even
     */
    private static String parseFantan(Map<String, Object> result) {
        Object buttonsCount = result.get("buttonsCount");
        if (buttonsCount == null) {
            return "";
        }

        int count = Integer.parseInt(buttonsCount.toString());
        String size = (count == 1 || count == 2) ? "Small" : "Big";
        String parity = (count % 2 == 0) ? "Even" : "Odd";

        return count + " , " + size + " , " + parity;
    }


    /**
     * 解析 dhp
     * result.player.cards.player
     * result.player.cards.dealer
     * result.player.cards.flop
     * result.player.cards.river
     */
    /*private static String parseDhp(Map<String, Object> result) {

    }*/

    /**
     * 解析 csp
     * result.cards.player
     * result.cards.dealer
     * Player AC,KB,8H,6H,4C
     * Dealer TD,7S,5SQC,2S
     */
    private static String parseCsp(Map<String, Object> result) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> cspMap = (Map<String, Object>) result.get("cards");
        List<String> player = (List<String>) cspMap.get("player");
        List<String> dealer = (List<String>) cspMap.get("dealer");
        if (player == null || dealer == null) {
            return "";
        }
        if (ObjectUtil.isNotEmpty(player)) {
            sb.append("Player ");
            sb.append(appendCards(player));
            sb.append("\n");
        }
        if (ObjectUtil.isNotEmpty(dealer)) {
            sb.append("Dealer ");
            sb.append(appendCards(dealer));
        }
        return sb.toString();
    }

    /**
     * Level 2(1.6x)
     * <p>
     * result.drawnBalls.last.payoutLevel
     * result.drawnBalls.last.reachedMultiplier
     */
    private static String parseCashOrCrash(Map<String, Object> result) {
        List<Map<String, Object>> drawnBalls = (List<Map<String, Object>>) result.get("drawnBalls");
        if (drawnBalls == null || drawnBalls.isEmpty()) {
            return "";
        }
        Map<String, Object> lastDrawnBall = drawnBalls.get(drawnBalls.size() - 1);

        Object payoutLevel = lastDrawnBall.get("payoutLevel");
        Object reachedMultiplier = lastDrawnBall.get("reachedMultiplier");
        return "Level " + payoutLevel + "(" + reachedMultiplier + "x)";
    }

    /**
     * 安德拉巴哈
     * Joker:QH Andar:QH Played Cards:23
     * result.joker
     * result.andar[].last/result.bahar[].last
     * result.cardsDealt
     */
    private static String parseAndarbahar(Map<String, Object> result) {
        StringBuilder sb = new StringBuilder();
        String joker = (String) result.get("joker");
        if (joker != null) {
            sb.append("joker: ").append(appendCards(Arrays.asList(joker))).append(" ");

        }
        String outcome = (String) result.get("outcome");
        if (outcome != null) {
            sb.append(outcome).append(" ");
            if (StringUtils.equalsAnyIgnoreCase(outcome, "Andar")) {
                List<String> cards = (List<String>) result.get("andar");
                if (cards != null) {
                    String lastCard = cards.get(cards.size() - 1);
                    sb.append(appendCards(Collections.singletonList(lastCard))).append(" ");
                }

            } else {
                List<String> cards = (List<String>) result.get("bahar");
                if (cards != null) {
                    String lastCard = cards.get(cards.size() - 1);
                    sb.append(appendCards(Collections.singletonList(lastCard))).append(" ");
                }
            }
        }
        Object cardsDealtObj = result.get("cardsDealt");
        if (cardsDealtObj != null) {
            sb.append("Played Cards:").append(cardsDealtObj);
        }
        return sb.toString();

    }

    /**
     * Texas Hold'em Bonus Poker 是一种扑克类赌场游戏，基于德州扑克
     */
    private static String parseThb(Map<String, Object> result) {
        StringBuilder sb = new StringBuilder();

        Map<String, Object> cards = (Map<String, Object>) result.get("cards");
        if (cards == null) {
            return "";
        }

        // 获取玩家手牌
        List<String> playerCards = (List<String>) cards.get("player");
        if (playerCards != null && !playerCards.isEmpty()) {
            String playerCardStr = appendCards(playerCards);
            sb.append("player: ").append(playerCardStr).append(";");

        }

        // 获取庄家手牌
        List<String> dealerCards = (List<String>) cards.get("dealer");
        if (dealerCards != null && !dealerCards.isEmpty()) {
            sb.append("dealer: ").append(appendCards(dealerCards)).append(";");
        }

        // 获取公共牌
        List<String> flop = (List<String>) cards.get("flop");
        if (flop != null && !flop.isEmpty()) {
            sb.append("Flop: ").append(appendCards(flop)).append(";");
        }

        String turn = (String) cards.get("turn");
        if (turn != null && !turn.isEmpty()) {
            sb.append("Turn: ").append(appendCards(List.of(turn))).append(";");
        }

        List<String> river = (List<String>) cards.get("river");
        if (river != null && !river.isEmpty()) {
            sb.append("River: ").append(appendCards(river)).append(";");
        }
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }


    /**
     * 超级骰宝 解析 1，1，2 Total:4
     */
    private static String parseSicbo(Map<String, Object> result) {
        StringBuilder stringBuilder = new StringBuilder();
        Integer first = (Integer) result.get("first");
        Integer second = (Integer) result.get("second");
        Integer third = (Integer) result.get("third");

        return stringBuilder.toString();
    }

    /**
     * if offers[].decision == "NoDeal" → 结果取 lastBox 金额。
     * if offers[].decision == "Swap" → 结果取 交换后箱子金额。
     * if  offers == nil → 结果为空或固定展示 "Qualification Only"
     * if  offers[].decision == "Deal" → 结果取 银行报价（offer -> Value）。
     */
    private static String parseRngDeallocate(Map<String, Object> map) {
        StringBuilder parseDeallocate = new StringBuilder();

        List<Map<String, Object>> participants = (List<Map<String, Object>>) map.get("participants");
        if (CollectionUtil.isEmpty(participants)) {
            return "";
        }
        String thirdOrderId = map.get("thirdOrderId").toString();
        boolean nodeal = false;
        boolean swap = false;
        boolean offerIsNull = false;
        List<Integer> dealArr = new ArrayList<>();
        // 盒子金额
        Map<Integer, String> offerMap = new HashMap<>();
        if (participants != null && !participants.isEmpty()) {
            // 找到bet
            for (Map<String, Object> participant : participants) {
                List<Map<String, Object>> betArr = (List<Map<String, Object>>) participant.get("bets");
                boolean isFind = false;
                for (Map<String, Object> bet : betArr) {
                    String orderID = String.valueOf(bet.get("transactionId")) + "_" + String.valueOf(bet.get("code"));
                    if (orderID.equals(thirdOrderId)) {
                        isFind = true;
                    }
                }
                if (isFind) {
                    List<Map<String, Object>> offers = (List<Map<String, Object>>) participant.get("offers");
                    Map<String, Object> boxes = (Map<String, Object>) participant.get("boxes");
                    if (CollectionUtil.isNotEmpty(offers)) {
                        for (Map.Entry<String, Object> entry : boxes.entrySet()) {
                            try {
                                int keyInt = Integer.parseInt(entry.getKey());
                                String value = String.valueOf(entry.getValue());
                                offerMap.put(keyInt, value);
                            } catch (Exception e) {
                                // 如果 key 不是数字，跳过
                                continue;
                            }
                        }
                    }

                    if (offers != null && !offers.isEmpty()) {
                        for (int i = 0; i < offers.size(); i++) {
                            Map<String, Object> offer = offers.get(i);
                            String decision = (String) offer.get("decision");
                            Object offerValue = offer.get("offer");
                            if ("NoDeal".equals(decision)) {
                                nodeal = true;
                            } else if ("Swap".equals(decision)) {
                                swap = true;

                            } else if ("Deal".equals(decision)) {
                                dealArr.add(i);
                                parseDeallocate.append("Deal");
                                if (offerValue != null) {
                                    parseDeallocate.append("<").append(offerValue).append(">");
                                }

                            }
                        }
                    } else {
                        offerIsNull = true;
                        parseDeallocate.append("Qualification Only");
                    }
                }
            }
        }
        Map<String, Object> result = (Map<String, Object>) map.get("result");
        if (result != null) {
            Object lastBox1 = result.get("lastBox");
            String lastBox = null;
            if (lastBox1 != null) {
                lastBox = result.get("lastBox").toString();
            }
            if (ObjectUtil.isNotEmpty(lastBox)) {
                try {
                    String boxValue = offerMap.get(Integer.parseInt(lastBox));
                    if (ObjectUtil.isNotEmpty(boxValue)) {
                        if (nodeal && dealArr.isEmpty()) {
                            parseDeallocate.append("Nodeal").append("<").append(lastBox).append(":").append(boxValue).append(">, ");
                        }
                        if (swap && dealArr.isEmpty()) {
                            parseDeallocate.append("Swap").append("<").append(lastBox).append(boxValue).append(":").append(">, ");
                        }
                    }
                } catch (Exception e) {
                    log.error("解析RngDeallocate异常", e);
                }
            }
        }
        return parseDeallocate.toString();
    }

    /**
     * 闪电可扩展二十一点
     * 示例输出：
     * 玩家：21点，庄家：21点，
     */
    private static String parseLightningScalableBJ(Map<String, Object> result) {
        if (result == null || result.isEmpty()) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        String status = (String) result.get("status");
        List<String> list =null;
        if (Objects.equals("Resolved",status)){
            list = (List<String>) result.get("cards");
        }else{
            list = (List<String>) result.get("dealtToPlayer");
        }

        if (ObjectUtil.isNotEmpty(list)) {
            stringBuilder.append("Player：").append(appendCardString(list)).append("\n");
        }
        Map<String, Object> dealerHand = (Map<String, Object>) result.get("dealerHand");
        if (ObjectUtil.isNotEmpty(dealerHand)) {
            List<String> list2 = (List<String>) dealerHand.get("cards");
            if (ObjectUtil.isNotEmpty(list2)) {
                stringBuilder.append("Dealer：").append(appendCardString(list2)).append("\n");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 解析 LightningDice 骰子结果
     * 示例输出： "3,5,6,Total 14"
     */
    @SuppressWarnings("unchecked")
    private static String parseLightningDice(Map<String, Object> result) {
        if (result == null || result.isEmpty()) {
            return "";
        }


        List<Integer> diceValues = new ArrayList<>();
        int total = 0;

        // 统一处理 first, second, third
        for (String key : Arrays.asList("first", "second", "third")) {
            Object value = result.get(key);
            if (value instanceof Number) {
                int val = ((Number) value).intValue();
                diceValues.add(val);
                total += val;
            }
        }

        if (diceValues.isEmpty()) {
            return "";
        }

        return String.join(",",
                diceValues.stream().map(String::valueOf).toList()
        ) + ",Total " + total;
    }


    /**
     * data.result.outcome.topSlot.wheelSector
     * participants.totalMultiplier
     */
    private static String parseCrazyTime(Map<String, Object> map) {
        StringBuilder crazyTime = new StringBuilder();
        Map<String, Object> result = (Map<String, Object>) map.get("result");
        if (result != null) {
            Map<String, Object> outcome = (Map<String, Object>) result.get("outcome");
            if (outcome != null) {
                Map<String, Object> wheelResult = (Map<String, Object>) outcome.get("wheelResult");
                if (wheelResult != null) {
                    String wheelSector = (String) wheelResult.get("wheelSector");
                    crazyTime.append(wheelSector).append(", ");
                }
            }
        }
        /*List<Map<String, Object>> participants = (List<Map<String, Object>>) map.get("participants");
        String thirdOrderId = map.get("thirdOrderId").toString();
        if (participants != null && !participants.isEmpty()) {
            // 找到bet

            for (Map<String, Object> participant : participants) {
                List<Map<String, Object>> betArr = (List<Map<String, Object>>) participant.get("bets");
                boolean isFind = false;
                for (Map<String, Object> bet : betArr) {
                    String orderID = String.valueOf(bet.get("transactionId")) + "_" + String.valueOf(bet.get("code"));
                    if (orderID.equals(thirdOrderId)) {
                        isFind = true;
                    }
                }

                if (isFind) {

                    String totalMultiplier = String.valueOf(participant.get("totalMultiplier"));
                    if (ObjectUtil.isNotEmpty(totalMultiplier) && !"null".equals(totalMultiplier)) {
                        crazyTime.append(totalMultiplier);
                    }
                }
            }


        }*/
        String crazyTimeStr = crazyTime.toString();
        if (StringUtils.isNotBlank(crazyTimeStr)) {
            if (crazyTimeStr.contains(",")) {
                crazyTimeStr = crazyTimeStr.substring(0, crazyTimeStr.length() - 2);
            }
        }
        return crazyTimeStr;

    }

    /**
     * data.result.outcomes[].number/color/type
     * data.result.outcomes[].number 1-36
     * data.result.outcomes[].color red/black
     * data.result.outcomes[].type even/odd
     *
     * @return
     */
    private static String parseRedDoorRoulette(Map<String, Object> result) {
        List<Map<String, Object>> outcomes = (List<Map<String, Object>>) result.get("outcomes");
        if (outcomes == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Map<String, Object> outcome : outcomes) {
            String number = (String) outcome.get("number");
            if (StringUtils.isNotBlank(number)) {
                stringBuilder.append(number).append(" ");
            }
            String color = (String) outcome.get("color");
            if (StringUtils.isNotBlank(color)) {
                stringBuilder.append(outcome.get("color")).append(" ");
            }
            String type = (String) outcome.get("type");
            if (StringUtils.isNotBlank(type)) {
                stringBuilder.append(type);
            }

        }
        return stringBuilder.toString();
    }

    /**
     * 真人骰宝/快骰
     * data.result.rolls.result.first/second
     * 2,4, Total 6
     */
    private static String parseCraps(Map<String, Object> result) {
        StringBuilder craps = new StringBuilder();
        List<Map<String, Object>> rolls = (List<Map<String, Object>>) result.get("rolls");
        if (rolls == null) {
            return "";
        }
        for (Map<String, Object> roll : rolls) {
            Map<String, Object> resultMap = (Map<String, Object>) roll.get("result");
            if (resultMap == null) {
                continue;
            }
            Integer first = (Integer) resultMap.get("first");
            Integer second = (Integer) resultMap.get("second");
            Integer total = first + second;
            craps.append(first).append(",").append(second).append(", Total ").append(total).append("\n");
        }
        return craps.toString();

    }

    /**
     * 老虎机结果解析
     * 返回注号码
     */
    private static String parseExtraChilliePicSpins(Map<String, Object> result) {
        return (String) result.get("thirdOrderId");
    }

    public static void main(String[] args) {
        String blackjack = "{\"gameType\":\"blackjack\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":11.992963,\"startedAt\":\"2025-08-22T09:04:46.665Z\",\"result\":{\"dealer\":{\"score\":24,\"cards\":[\"5C\",\"6S\",\"4D\",\"9H\"],\"isBlackjack\":false,\"bonusCards\":[]},\"burnedCards\":[],\"seats\":{\"Seat5\":{\"score\":18,\"cards\":[\"8H\",\"TD\"],\"decisions\":[{\"recordedAt\":\"2025-08-22T09:05:28.690Z\",\"type\":\"AutoStand\"}],\"bonusCards\":[],\"outcome\":\"Win\"}}},\"wager\":5.996481,\"settledAt\":\"2025-08-22T09:05:31.510Z\",\"dealer\":{\"uid\":\"tts0r9p_________\",\"name\":\"ROB_349\"},\"currency\":\"EUR\",\"id\":\"185e0b6df32a93978c8dc033\",\"table\":{\"name\":\"Blackjack A DNT\",\"id\":\"uwd2bl2khwcikjlz\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"185e0b6df32a93978c8dc033-tdiaqjhmodgaafwt\",\"currencyRateVersion\":\"tdhriitakcgqaaae\",\"os\":\"macOS\",\"channel\":\"desktop\",\"screenName\":\"俄罗斯转盘\",\"sessionId\":\"tdiaqjhmodgaafwttdiqjc4nkduqcitsbc86408f\",\"bets\":[{\"stake\":50,\"placedOn\":\"2025-08-22T09:05:06.716Z\",\"code\":\"BJ_PlaySeat5\",\"payout\":100,\"transactionId\":\"78c8a1b1-730b-404c-be04-ac3baa7ee009\"}],\"seats\":{\"Seat5\":{\"insurance\":false,\"doubleDown\":false,\"buyTo18\":false,\"betBehind\":false,\"splitHand\":false}},\"playMode\":\"RealMoney\",\"result\":{\"link\":\"/api/render/v2/html/ChBzdWJzd2luMjAwMDAwMDAxEhgxODVlMGI2ZGYzMmE5Mzk3OGM4ZGMwMzMaEHRkaWFxamhtb2RnYWFmd3QiDlV0ZXN0XzQ2Mzc4NTQ0KgwI2-WgxQYQgPeX8wEyCWV2b2x1dGlvbg\"},\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"83135300be2a45538444fcbf21397b83\",\"currency\":\"CNY\",\"configOverlays\":[],\"device\":\"Desktop\",\"playerId\":\"Utest_46378544\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"78c8a1b1-730b-404c-be04-ac3baa7ee009_BJ_PlaySeat5\"}";
        Map<String, Object> map = JSONObject.parseObject(blackjack, Map.class);
        String resultInfo = gameResultInfo(map);
        System.out.println("blackjack");
        System.out.println(resultInfo);
        System.out.println();
        String bacboResult = "{\"gameType\":\"bacbo\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":2.4002879999999998,\"startedAt\":\"2025-08-30T02:04:01.818Z\",\"result\":{\"bankerDice\":{\"score\":7,\"first\":5,\"second\":2},\"playerDice\":{\"score\":6,\"first\":5,\"second\":1},\"outcome\":\"BankerWon\"},\"wager\":3.6004319999999996,\"settledAt\":\"2025-08-30T02:04:24.095Z\",\"dealer\":{\"uid\":\"tts0rk7_________\",\"name\":\"ROB_727\"},\"currency\":\"EUR\",\"id\":\"186069203a9200d6942d59c5\",\"table\":{\"name\":\"Bac Bo\",\"id\":\"BacBo00000000001\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"186069203a9200d6942d59c5-td226fkviiyqacux\",\"currencyRateVersion\":\"td4eoataas4qaaac\",\"os\":\"macOS\",\"channel\":\"desktop\",\"screenName\":\"1答复\",\"sessionId\":\"td226fkviiyqacuxtd4ln4k3r7eadbz34ffe35d5\",\"bets\":[{\"stake\":10,\"placedOn\":\"2025-08-30T02:04:16.115Z\",\"code\":\"BacBo_Banker\",\"payout\":20,\"transactionId\":\"9f8f03ed-db07-4704-8fc9-660db5c45e1f\"},{\"stake\":10,\"placedOn\":\"2025-08-30T02:04:16.115Z\",\"code\":\"BacBo_Player\",\"payout\":0,\"transactionId\":\"9f8f03ed-db07-4704-8fc9-660db5c45e1f\"},{\"stake\":10,\"placedOn\":\"2025-08-30T02:04:16.115Z\",\"code\":\"BacBo_Tie\",\"payout\":0,\"transactionId\":\"9f8f03ed-db07-4704-8fc9-660db5c45e1f\"}],\"playMode\":\"RealMoney\",\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"40d61aa46475473a8271e9c1ca86fbe7\",\"currency\":\"CNY\",\"configOverlays\":[],\"device\":\"Desktop\",\"playerId\":\"Utest_45189822\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"9f8f03ed-db07-4704-8fc9-660db5c45e1f_BacBo_Banker\"}";
        Map<String, Object> bacbomap = JSONObject.parseObject(bacboResult, Map.class);
        System.out.println(gameResultInfo(bacbomap));
        String other = "{\"gameType\":\"rng-topcard\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":0.478418,\"startedAt\":\"2025-08-21T09:59:49.737Z\",\"result\":{\"bSpot\":{\"score\":6,\"card\":\"6H\"},\"aSpot\":{\"score\":11,\"card\":\"JH\"},\"outcome\":\"A\"},\"wager\":0.239209,\"settledAt\":\"2025-08-21T09:59:51.538Z\",\"dealer\":{\"uid\":\"no-dealer\",\"name\":\"No Dealer\"},\"currency\":\"EUR\",\"id\":\"185dbfd9c805ec4414828cb8\",\"table\":{\"name\":\"First Person Top Card\",\"id\":\"rng-topcard00001\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"185dbfd9c805ec4414828cb8\",\"currencyRateVersion\":\"tde63rtakcgqaaad\",\"os\":\"iOS\",\"channel\":\"mobile\",\"screenName\":\"5523\",\"sessionId\":\"tdf7iyeuodgaaef3tdgbdbrdkduqbad597d787cf\",\"bets\":[{\"stake\":2,\"placedOn\":\"2025-08-21T09:59:50.509Z\",\"code\":\"TC_A\",\"payout\":4,\"transactionId\":\"d7368e5b-c487-4324-800d-aee11f3cc256\"}],\"playMode\":\"RealMoney\",\"result\":{\"link\":\"/api/render/v2/html/ChBzdWJzd2luMjAwMDAwMDAxEhgxODVkYmZkOWM4MDVlYzQ0MTQ4MjhjYjgaEHRkZjdpeWV1b2RnYWFlZjMiDlV0ZXN0XzYxMTQ5NDY2KgwIl9ybxQYQgPXEgAIyCWV2b2x1dGlvbg\"},\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"9a25f5b807c148cbad25272aa7104a60\",\"currency\":\"CNY\",\"configOverlays\":[],\"device\":\"SmartPhone\",\"playerId\":\"Utest_61149466\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"d7368e5b-c487-4324-800d-aee11f3cc256_TC_A\"}";
        System.out.println(gameResultInfo(JSONObject.parseObject(other, Map.class)));
        String DragonTiger = "{\"gameType\":\"dragontiger\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":0,\"startedAt\":\"2025-09-01T07:19:59.185Z\",\"result\":{\"dragon\":{\"score\":6,\"card\":\"6S\"},\"tiger\":{\"score\":10,\"card\":\"TD\"},\"outcome\":\"Tiger\"},\"wager\":11.994896,\"settledAt\":\"2025-09-01T07:20:21.043Z\",\"dealer\":{\"uid\":\"tts0rde_________\",\"name\":\"ROB_482\"},\"currency\":\"EUR\",\"id\":\"1861178738612ed2ec739e06\",\"table\":{\"name\":\"Dragon Tiger DNT\",\"id\":\"DragonTiger00001\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"1861178738612ed2ec739e06-td226fkviiyqacux\",\"currencyRateVersion\":\"tebjhotaas4qaaae\",\"os\":\"macOS\",\"channel\":\"desktop\",\"screenName\":\"1答复\",\"sessionId\":\"td226fkviiyqacuxtecchlk4r7eaferpe05bf193\",\"bets\":[{\"stake\":60,\"placedOn\":\"2025-09-01T07:20:15.832Z\",\"code\":\"DT_Dragon\",\"payout\":0,\"transactionId\":\"e2a7f80d-210f-4fc5-85cc-9cb1bfd716d5\"},{\"stake\":20,\"placedOn\":\"2025-09-01T07:20:15.832Z\",\"code\":\"DT_SuitedTie\",\"payout\":0,\"transactionId\":\"e2a7f80d-210f-4fc5-85cc-9cb1bfd716d5\"},{\"stake\":20,\"placedOn\":\"2025-09-01T07:20:15.832Z\",\"code\":\"DT_Tie\",\"payout\":0,\"transactionId\":\"e2a7f80d-210f-4fc5-85cc-9cb1bfd716d5\"}],\"playMode\":\"RealMoney\",\"result\":{\"link\":\"/api/render/v2/html/ChBzdWJzd2luMjAwMDAwMDAxEhgxODYxMTc4NzM4NjEyZWQyZWM3MzllMDYaEHRkMjI2Zmt2aWl5cWFjdXgiDlV0ZXN0XzQ1MTg5ODIyKgsItZLVxQYQwMHAFDIJZXZvbHV0aW9u\"},\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"b856087920884dac8bd969ee45fcb28c\",\"currency\":\"CNY\",\"configOverlays\":[],\"device\":\"Desktop\",\"playerId\":\"Utest_45189822\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"e2a7f80d-210f-4fc5-85cc-9cb1bfd716d5_DT_Tie\"}";
        System.out.println(gameResultInfo(JSONObject.parseObject(DragonTiger, Map.class)));
        String thirdOrderId = "{\"gameType\":\"extrachilliepicspins\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":1.6792850000000001,\"startedAt\":\"2025-09-01T08:00:32.370Z\",\"result\":{\"gamePhases\":[{\"phase\":\"Feature\",\"bonus\":{\"symbolsCount\":1,\"freeSpins\":0,\"type\":\"ScatterSymbol\"},\"name\":\"Crate\",\"type\":\"Bonus\"},{\"phase\":\"BaseSpin\",\"reelsOffsets\":[7,56,86,117,150,17],\"reelsDensity\":[3,6,3,3,3,5],\"multiplier\":1,\"freeSpinsLeft\":0,\"winWays\":[{\"symbolId\":8,\"length\":3,\"positions\":[[1,0,0],[0,0,0,0,0,1],[0,0,0],[0,0,0],[0,0,0],[0,0,0,0,0]],\"paytableRatio\":0.1,\"extraReelPositions\":[[0,1,0,0]],\"waysCount\":1,\"winRatio\":0.1},{\"symbolId\":3,\"length\":3,\"positions\":[[0,1,1],[0,0,0,0,1,0],[0,0,1],[0,0,0],[0,0,0],[0,0,0,0,0]],\"paytableRatio\":0.3,\"extraReelPositions\":[[0,0,0,0]],\"waysCount\":2,\"winRatio\":0.6}],\"screen\":[[8,3,3],[5,9,5,5,3,8],[7,6,3],[10,5,5],[10,6,8],[5,9,6,6,6]],\"reelSet\":[1,1,1,1,1,1],\"type\":\"Initial\",\"winRatio\":0.7,\"extraReel\":[1,8,7,9]},{\"phase\":\"BaseSpin\",\"reelsOffsets\":[101,85,162,117,150,17],\"reelsDensity\":[3,6,3,3,3,5],\"multiplier\":1,\"freeSpinsLeft\":0,\"winWays\":[],\"screen\":[[3,10,8],[5,9,5,5,9,2],[7,6,6],[10,5,5],[10,6,8],[5,9,6,6,6]],\"reelSet\":[1,1,1,1,1,1],\"type\":\"Respin\",\"winRatio\":0.7,\"extraReel\":[1,7,9,9]},{\"phase\":\"Feature\",\"bonus\":{\"type\":\"Empty\"},\"name\":\"Crate\",\"type\":\"Bonus\"},{\"phase\":\"BaseSpin\",\"reelsOffsets\":[46,109,77,19,29,136],\"reelsDensity\":[4,3,4,2,3,3],\"multiplier\":1,\"freeSpinsLeft\":0,\"winWays\":[],\"screen\":[[10,3,7,10],[5,8,9],[9,6,9,4],[1,3],[6,2,6],[10,6,7]],\"reelSet\":[1,1,1,1,1,1],\"type\":\"Initial\",\"winRatio\":0,\"extraReel\":[9,10,5,9]},{\"phase\":\"Feature\",\"bonus\":{\"type\":\"Empty\"},\"name\":\"Crate\",\"type\":\"Bonus\"},{\"phase\":\"BaseSpin\",\"reelsOffsets\":[57,137,109,64,57,42],\"reelsDensity\":[4,2,2,2,2,5],\"multiplier\":1,\"freeSpinsLeft\":0,\"winWays\":[],\"screen\":[[6,10,7,3],[9,8],[4,10],[5,10],[10,6],[6,9,10,6,5]],\"reelSet\":[1,1,1,1,1,1],\"type\":\"Initial\",\"winRatio\":0,\"extraReel\":[2,8,9,8]},{\"phase\":\"Feature\",\"bonus\":{\"type\":\"Empty\"},\"name\":\"Crate\",\"type\":\"Bonus\"},{\"phase\":\"BaseSpin\",\"reelsOffsets\":[70,135,97,105,131,103],\"reelsDensity\":[3,3,4,3,6,2],\"multiplier\":1,\"freeSpinsLeft\":0,\"winWays\":[],\"screen\":[[7,3,10],[8,2,9],[10,6,4,9],[1,8,10],[3,9,3,4,10,3],[6,6]],\"reelSet\":[1,1,1,1,1,1],\"type\":\"Initial\",\"winRatio\":0,\"extraReel\":[9,3,8,1]},{\"phase\":\"Feature\",\"bonus\":{\"type\":\"BonusMultiplier\",\"value\":5},\"name\":\"Crate\",\"type\":\"Bonus\"},{\"phase\":\"BaseSpin\",\"reelsOffsets\":[45,66,77,44,152,70],\"reelsDensity\":[3,3,4,3,2,2],\"multiplier\":6,\"freeSpinsLeft\":0,\"winWays\":[],\"screen\":[[7,10,9],[8,1,7],[9,6,9,4],[1,5,10],[3,10],[7,1]],\"reelSet\":[1,1,1,1,1,1],\"type\":\"Initial\",\"winRatio\":0,\"extraReel\":[7,10,9,1]}],\"totalMultiplier\":6},\"wager\":11.994896,\"settledAt\":\"2025-09-01T08:03:13.466Z\",\"dealer\":{\"uid\":\"tts0r4e_________\",\"name\":\"ROB_158\"},\"currency\":\"EUR\",\"id\":\"186119bdbd8018b27f430a73\",\"table\":{\"name\":\"Extra Chilli Epic Spins\",\"id\":\"ExChEpicSpins001\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"186119bdbd8018b27f430a73-td226fkviiyqacux\",\"currencyRateVersion\":\"tebjhotaas4qaaae\",\"os\":\"macOS\",\"channel\":\"desktop\",\"screenName\":\"1答复\",\"sessionId\":\"td226fkviiyqacuxtecewwjpr7eafgmu9772da24\",\"gambleWheels\":[],\"bets\":[{\"stake\":100,\"placedOn\":\"2025-09-01T08:02:50.284Z\",\"code\":\"ECES_EpicSpin\",\"payout\":14,\"transactionId\":\"756458950784046194\"}],\"playMode\":\"RealMoney\",\"winRatio\":0.7,\"result\":{\"link\":\"/api/render/v2/html/ChBzdWJzd2luMjAwMDAwMDAxEhgxODYxMTliZGJkODAxOGIyN2Y0MzBhNzMaEHRkMjI2Zmt2aWl5cWFjdXgiDlV0ZXN0XzQ1MTg5ODIyKgwIwabVxQYQgLGa3gEyCWV2b2x1dGlvbg\"},\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"48a55d0aebc74530848d61cb474cebbc\",\"currency\":\"CNY\",\"configOverlays\":[],\"device\":\"Desktop\",\"playerId\":\"Utest_45189822\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"756458950784046194_ECES_EpicSpin\"}";
        System.out.println(gameResultInfo(JSONObject.parseObject(thirdOrderId, Map.class)));
        String craps = "{\"gameType\":\"craps\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":261.188858,\"startedAt\":\"2025-09-01T03:10:31.562Z\",\"result\":{\"rolls\":[{\"result\":{\"first\":3,\"second\":3},\"startedAt\":\"2025-09-01T03:10:28.840Z\",\"puck\":\"Point5\",\"rollId\":\"186109e9aa480bf6cb57dbae\"}]},\"wager\":120.548703,\"settledAt\":\"2025-09-01T03:10:58.701Z\",\"dealer\":{\"uid\":\"tts0rjo_________\",\"name\":\"ROB_708\"},\"currency\":\"EUR\",\"id\":\"186109ea4c95bac188ace72a\",\"table\":{\"name\":\"Craps DNT\",\"id\":\"Craps00000000001\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"186109ea4c95bac188ace72a-td226fkviiyqacux\",\"currencyRateVersion\":\"tebjhotaas4qaaae\",\"os\":\"macOS\",\"channel\":\"desktop\",\"screenName\":\"1答复\",\"sessionId\":\"td226fkviiyqacuxtebubn4mztcae7yv5d13a255\",\"bets\":[{\"stake\":1005,\"placedOn\":\"2025-09-01T03:10:47.544Z\",\"code\":\"Craps_Win6\",\"payout\":2177.5,\"transactionId\":\"4772c70c-8a25-47db-8817-1fc971c343e9\"}],\"playMode\":\"RealMoney\",\"result\":{\"link\":\"/api/render/v2/html/ChBzdWJzd2luMjAwMDAwMDAxEhgxODYxMDllYTRjOTViYWMxODhhY2U3MmEaEHRkMjI2Zmt2aWl5cWFjdXgiDlV0ZXN0XzQ1MTg5ODIyKgwIwp3UxQYQwNKhzgIyCWV2b2x1dGlvbg\"},\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"a62aa1c1909945f997ab15feaec41b22\",\"currency\":\"CNY\",\"configOverlays\":[],\"device\":\"Desktop\",\"playerId\":\"Utest_45189822\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"4772c70c-8a25-47db-8817-1fc971c343e9_Craps_Win6\"}";
        System.out.println(gameResultInfo(JSONObject.parseObject(craps, Map.class)));
        String RedDoorRoulette = "{\"currency\":\"EUR\",\"dealer\":{\"name\":\"Scratchy\",\"uid\":\"123abcdefg\"},\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"gameSubType\":\"reddoor_3f\",\"gameType\":\"americanreddoorroulette\",\"id\":\"158afoofa0b148486c20fbar\",\"participants\":[{\"betCoverage\":{\"simple\":0.78},\"bets\":[{\"code\":\"RoDZ_00Green\",\"description\":\"\",\"payout\":0,\"placedOn\":\"2024-11-01T12:47:56.253Z\",\"stake\":0.5,\"transactionId\":\"1234\"},{\"code\":\"RoDZ_0Green\",\"description\":\"\",\"payout\":0,\"placedOn\":\"2024-11-01T12:47:56.253Z\",\"stake\":0.5,\"transactionId\":\"1234\"},{\"code\":\"RoDZ_118\",\"description\":\"\",\"payout\":1,\"placedOn\":\"2024-11-01T12:47:56.253Z\",\"stake\":0.5,\"transactionId\":\"1234\"},{\"code\":\"RoDZ_Even\",\"description\":\"\",\"payout\":1,\"placedOn\":\"2024-11-01T12:47:56.253Z\",\"stake\":0.5,\"transactionId\":\"1234\"}],\"casinoId\":\"thebest00000000001\",\"casinoSessionId\":\"\",\"channel\":\"desktop\",\"configOverlays\":[],\"currency\":\"EUR\",\"currencyRateVersion\":\"slsqm6tbqrraaaal\",\"device\":\"Desktop\",\"os\":\"macOS\",\"playMode\":\"RealMoney\",\"playerGameId\":\"1803d8f9fcbb769eb162281d-mvs3s4p6knoqalll\",\"playerId\":\"1234abcdefg\",\"screenName\":\"Mighty Moe\",\"sessionId\":\"1234foobar\",\"subType\":\"reddoor_3f\"}],\"payout\":2,\"result\":{\"bonusBetSpots\":{\"1\":1,\"22\":1,\"32\":1,\"6\":3,\"9\":1},\"bonusRound\":{\"flapperResults\":[{\"color\":\"Green\",\"position\":\"Left\",\"totalMultiplier\":300},{\"color\":\"Blue\",\"position\":\"Top\",\"totalMultiplier\":600},{\"color\":\"Yellow\",\"position\":\"Right\",\"totalMultiplier\":150}],\"wheelLayout\":{\"id\":\"2\",\"wheelSectors\":[{\"id\":0,\"type\":\"Regular\",\"value\":40},{\"id\":1,\"type\":\"Regular\",\"value\":50},{\"id\":2,\"type\":\"Regular\",\"value\":30},{\"id\":3,\"type\":\"Regular\",\"value\":400},{\"id\":4,\"type\":\"Regular\",\"value\":40},{\"id\":5,\"type\":\"Regular\",\"value\":100},{\"id\":6,\"type\":\"Regular\",\"value\":50},{\"id\":7,\"type\":\"Wheel\",\"value\":2},{\"id\":8,\"type\":\"Regular\",\"value\":200},{\"id\":9,\"type\":\"Regular\",\"value\":50},{\"id\":10,\"type\":\"Regular\",\"value\":100},{\"id\":11,\"type\":\"Regular\",\"value\":30},{\"id\":12,\"type\":\"Regular\",\"value\":50},{\"id\":13,\"type\":\"Regular\",\"value\":100},{\"id\":14,\"type\":\"Regular\",\"value\":40},{\"id\":15,\"type\":\"Regular\",\"value\":400},{\"id\":16,\"type\":\"Wheel\",\"value\":2},{\"id\":17,\"type\":\"Regular\",\"value\":200},{\"id\":18,\"type\":\"Regular\",\"value\":40},{\"id\":19,\"type\":\"Regular\",\"value\":100},{\"id\":20,\"type\":\"Regular\",\"value\":200},{\"id\":21,\"type\":\"Regular\",\"value\":50},{\"id\":22,\"type\":\"Regular\",\"value\":30},{\"id\":23,\"type\":\"Wheel\",\"value\":2},{\"id\":24,\"type\":\"Regular\",\"value\":40},{\"id\":25,\"type\":\"Regular\",\"value\":50},{\"id\":26,\"type\":\"Regular\",\"value\":100},{\"id\":27,\"type\":\"Regular\",\"value\":30},{\"id\":28,\"type\":\"Regular\",\"value\":50},{\"id\":29,\"type\":\"Regular\",\"value\":100},{\"id\":30,\"type\":\"Regular\",\"value\":40},{\"id\":31,\"type\":\"Regular\",\"value\":100},{\"id\":32,\"type\":\"Regular\",\"value\":50},{\"id\":33,\"type\":\"Wheel\",\"value\":2},{\"id\":34,\"type\":\"Regular\",\"value\":200},{\"id\":35,\"type\":\"Regular\",\"value\":30},{\"id\":36,\"type\":\"Regular\",\"value\":50},{\"id\":37,\"type\":\"Regular\",\"value\":400},{\"id\":38,\"type\":\"Regular\",\"value\":100},{\"id\":39,\"type\":\"Regular\",\"value\":50},{\"id\":40,\"type\":\"Regular\",\"value\":40},{\"id\":41,\"type\":\"Regular\",\"value\":30},{\"id\":42,\"type\":\"Regular\",\"value\":100},{\"id\":43,\"type\":\"Regular\",\"value\":50},{\"id\":44,\"type\":\"Regular\",\"value\":40},{\"id\":45,\"type\":\"Regular\",\"value\":200},{\"id\":46,\"type\":\"Regular\",\"value\":30},{\"id\":47,\"type\":\"Regular\",\"value\":100},{\"id\":48,\"type\":\"Regular\",\"value\":50},{\"id\":49,\"type\":\"Wheel\",\"value\":2},{\"id\":50,\"type\":\"Regular\",\"value\":200},{\"id\":51,\"type\":\"Regular\",\"value\":100},{\"id\":52,\"type\":\"Regular\",\"value\":30},{\"id\":53,\"type\":\"Regular\",\"value\":50},{\"id\":54,\"type\":\"Regular\",\"value\":200},{\"id\":55,\"type\":\"Regular\",\"value\":40},{\"id\":56,\"type\":\"Regular\",\"value\":50},{\"id\":57,\"type\":\"Regular\",\"value\":400},{\"id\":58,\"type\":\"Regular\",\"value\":30},{\"id\":59,\"type\":\"Regular\",\"value\":50},{\"id\":60,\"type\":\"Regular\",\"value\":40},{\"id\":61,\"type\":\"Regular\",\"value\":200},{\"id\":62,\"type\":\"Regular\",\"value\":50},{\"id\":63,\"type\":\"Regular\",\"value\":100}]},\"wheelResults\":[],\"wheelSpinResults\":[{\"flappers\":[{\"color\":\"Green\",\"position\":\"Left\",\"wheelSector\":{\"id\":47,\"type\":\"Regular\",\"value\":300}},{\"color\":\"Blue\",\"position\":\"Top\",\"wheelSector\":{\"id\":50,\"type\":\"Regular\",\"value\":600}},{\"color\":\"Yellow\",\"position\":\"Right\",\"wheelSector\":{\"id\":53,\"type\":\"Regular\",\"value\":150}}],\"spinType\":\"Initial\"}]},\"outcomes\":[{\"color\":\"Black\",\"number\":\"6\",\"type\":\"Even\"}]},\"settledAt\":\"2024-11-01T12:49:24.747Z\",\"startedAt\":\"2024-11-01T12:47:30.039Z\",\"status\":\"Resolved\",\"table\":{\"id\":\"a1s2d3f4g5h6j7k8\",\"name\":\"americanreddoorroulette\"},\"wager\":2}";
        System.out.println("RedDoorRoulette");
        System.out.println(gameResultInfo(JSONObject.parseObject(RedDoorRoulette, Map.class)));
        //String crazyTime = "{\"id\":\"17c3014efe36e0c94fdd409a\",\"gameProvider\":\"evolution\",\"startedAt\":\"2024-04-04T06:55:22.115Z\",\"settledAt\":\"2024-04-04T06:56:02.423Z\",\"status\":\"Resolved\",\"gameType\":\"crazytime\",\"table\":{\"id\":\"CrazyTime0000001\",\"name\":\"Crazy Time\"},\"dealer\":{\"uid\":\"tts0ric_________\",\"name\":\"ROB_660\"},\"currency\":\"EUR\",\"participants\":[{\"casinoId\":\"0rnu7w1c8xhbuhtn\",\"playerId\":\"perftest000234\",\"screenName\":\"[AP] Nancy Moore\",\"playerGameId\":\"17c3014efe36e0c94fdd409a-mrvowxkiyjqaaboe\",\"sessionId\":\"mrvowxkiyjqaaboer2oztpkuj5yqgwvd3da7099b\",\"casinoSessionId\":\"9a62dea28a7cb5f68b34254764607cc3d047eb38\",\"currency\":\"EUR\",\"bets\":[{\"code\":\"CT_1\",\"stake\":0.5,\"payout\":0,\"placedOn\":\"2024-04-04T06:55:38.185Z\",\"transactionId\":\"1b0e01cb-b670-4223-8b91-2b4355e60a11\",\"description\":\"\"},{\"code\":\"CT_10\",\"stake\":0.5,\"payout\":0,\"placedOn\":\"2024-04-04T06:55:38.185Z\",\"transactionId\":\"1b0e01cb-b670-4223-8b91-2b4355e60a11\",\"description\":\"\"},{\"code\":\"CT_2\",\"stake\":0.5,\"payout\":0,\"placedOn\":\"2024-04-04T06:55:38.185Z\",\"transactionId\":\"1b0e01cb-b670-4223-8b91-2b4355e60a11\",\"description\":\"\"},{\"code\":\"CT_5\",\"stake\":0.5,\"payout\":0,\"placedOn\":\"2024-04-04T06:55:38.185Z\",\"transactionId\":\"1b0e01cb-b670-4223-8b91-2b4355e60a11\",\"description\":\"\"},{\"code\":\"CT_CashHunt\",\"stake\":0.5,\"payout\":0,\"placedOn\":\"2024-04-04T06:55:38.185Z\",\"transactionId\":\"1b0e01cb-b670-4223-8b91-2b4355e60a11\",\"description\":\"\"},{\"code\":\"CT_CoinFlip\",\"stake\":0.5,\"payout\":2.5,\"placedOn\":\"2024-04-04T06:55:38.185Z\",\"transactionId\":\"1b0e01cb-b670-4223-8b91-2b4355e60a11\",\"description\":\"\"},{\"code\":\"CT_CrazyTime\",\"stake\":0.5,\"payout\":0,\"placedOn\":\"2024-04-04T06:55:38.185Z\",\"transactionId\":\"1b0e01cb-b670-4223-8b91-2b4355e60a11\",\"description\":\"\"},{\"code\":\"CT_Pachinko\",\"stake\":50,\"payout\":0,\"placedOn\":\"2024-04-04T06:55:38.185Z\",\"transactionId\":\"1b0e01cb-b670-4223-8b91-2b4355e60a11\",\"description\":\"\"}],\"configOverlays\":[],\"playMode\":\"RealMoney\",\"channel\":\"desktop\",\"os\":\"Windows\",\"device\":\"Desktop\",\"skinId\":\"_default_\",\"brandId\":\"1\",\"maxPayout\":55555,\"currencyRateVersion\":\"r2tgrjtbm2raaaac\",\"totalMultiplier\":4},{\"casinoId\":\"0rnu7w1c8xhbuhtn\",\"playerId\":\"perftest000249\",\"screenName\":\"[AP] Kenneth Gonzales\",\"playerGameId\":\"17c3014efe36e0c94fdd409a-mrvoxaxoyjqaabpt\",\"sessionId\":\"mrvoxaxoyjqaabptr2oztpkyt2qqinpd2ba49e3a\",\"casinoSessionId\":\"26d47e6544e72255216566533f809b91cfce9f9d\",\"currency\":\"EUR\",\"bets\":[{\"code\":\"CT_1\",\"stake\":0.5,\"payout\":0,\"placedOn\":\"2024-04-04T06:55:38.186Z\",\"transactionId\":\"ad8ac74a-5e90-4d85-b29d-78a9ddb89aa3\",\"description\":\"\"},{\"code\":\"CT_10\",\"stake\":0.5,\"payout\":0,\"placedOn\":\"2024-04-04T06:55:38.186Z\",\"transactionId\":\"ad8ac74a-5e90-4d85-b29d-78a9ddb89aa3\",\"description\":\"\"},{\"code\":\"CT_2\",\"stake\":0.5,\"payout\":0,\"placedOn\":\"2024-04-04T06:55:38.186Z\",\"transactionId\":\"ad8ac74a-5e90-4d85-b29d-78a9ddb89aa3\",\"description\":\"\"},{\"code\":\"CT_5\",\"stake\":0.5,\"payout\":0,\"placedOn\":\"2024-04-04T06:55:38.186Z\",\"transactionId\":\"ad8ac74a-5e90-4d85-b29d-78a9ddb89aa3\",\"description\":\"\"},{\"code\":\"CT_CashHunt\",\"stake\":0.5,\"payout\":0,\"placedOn\":\"2024-04-04T06:55:38.186Z\",\"transactionId\":\"ad8ac74a-5e90-4d85-b29d-78a9ddb89aa3\",\"description\":\"\"},{\"code\":\"CT_CoinFlip\",\"stake\":0.5,\"payout\":2.5,\"placedOn\":\"2024-04-04T06:55:38.186Z\",\"transactionId\":\"ad8ac74a-5e90-4d85-b29d-78a9ddb89aa3\",\"description\":\"\"},{\"code\":\"CT_CrazyTime\",\"stake\":0.5,\"payout\":0,\"placedOn\":\"2024-04-04T06:55:38.186Z\",\"transactionId\":\"ad8ac74a-5e90-4d85-b29d-78a9ddb89aa3\",\"description\":\"\"},{\"code\":\"CT_Pachinko\",\"stake\":50,\"payout\":0,\"placedOn\":\"2024-04-04T06:55:38.186Z\",\"transactionId\":\"ad8ac74a-5e90-4d85-b29d-78a9ddb89aa3\",\"description\":\"\"}],\"configOverlays\":[],\"playMode\":\"RealMoney\",\"channel\":\"desktop\",\"os\":\"Windows\",\"device\":\"Desktop\",\"skinId\":\"_default_\",\"brandId\":\"1\",\"maxPayout\":55555,\"currencyRateVersion\":\"r2tgrjtbm2raaaac\",\"totalMultiplier\":4}],\"result\":{\"outcome\":{\"type\":\"SlotAndWheel\",\"topSlot\":{\"wheelSector\":\"CrazyBonus\"},\"wheelResult\":{\"type\":\"BonusRound\",\"wheelSector\":\"CoinFlip\",\"bonus\":{\"type\":\"CoinFlip\",\"coin\":{\"heads\":{\"color\":\"Red\",\"multiplier\":4},\"tails\":{\"color\":\"Blue\",\"multiplier\":10}},\"flips\":[{\"rescue\":false,\"result\":{\"type\":\"Heads\",\"color\":\"Red\",\"multiplier\":4}}],\"bonusMultiplier\":4}}}},\"wager\":107,\"payout\":5,\"thirdOrderId\":\"1b0e01cb-b670-4223-8b91-2b4355e60a11_CT_1\"}\n";
        //String crazyTime = "{\"gameType\":\"crazytime\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":0,\"startedAt\":\"2025-09-05T04:21:41.793Z\",\"result\":{\"outcome\":{\"wheelResult\":{\"type\":\"WinningNumber\",\"wheelSector\":\"5\"},\"type\":\"SlotAndWheel\",\"topSlot\":{\"multiplier\":4,\"wheelSector\":\"CashHunt\"}}},\"wager\":0.30457500000000004,\"settledAt\":\"2025-09-05T04:22:09.005Z\",\"dealer\":{\"uid\":\"tts0rba_________\",\"name\":\"ROB_406\"},\"currency\":\"EUR\",\"id\":\"1862481ecf5b8c88fd335632\",\"table\":{\"name\":\"Crazy Time\",\"id\":\"CrazyTime0000001\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"1862481ecf5b8c88fd335632-tembvj7itmpaadom\",\"currencyRateVersion\":\"tels2ktg2ajaaaab\",\"os\":\"macOS\",\"channel\":\"desktop\",\"screenName\":\"辱工肝菜莀地\",\"sessionId\":\"tembvj7itmpaadomtembvmrr5usacol319de80a8\",\"bets\":[{\"stake\":0.5,\"placedOn\":\"2025-09-05T04:21:56.985Z\",\"code\":\"CT_2\",\"payout\":0,\"transactionId\":\"bac8a801-dfe6-46fb-a4a6-01e0074372e6\"},{\"stake\":0.5,\"placedOn\":\"2025-09-05T04:21:56.985Z\",\"code\":\"CT_CashHunt\",\"payout\":0,\"transactionId\":\"bac8a801-dfe6-46fb-a4a6-01e0074372e6\"},{\"stake\":0.5,\"placedOn\":\"2025-09-05T04:21:56.985Z\",\"code\":\"CT_CoinFlip\",\"payout\":0,\"transactionId\":\"bac8a801-dfe6-46fb-a4a6-01e0074372e6\"}],\"playMode\":\"RealMoney\",\"result\":{\"link\":\"/api/render/v2/html/ChBzdWJzd2luMjAwMDAwMDAxEhgxODYyNDgxZWNmNWI4Yzg4ZmQzMzU2MzIaEHRlbWJ2ajdpdG1wYWFkb20iDlV0ZXN0XzcxNjkzOTEzKgsI8crpxQYQwJaxAjIJZXZvbHV0aW9u\"},\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"4f76ee7a01914c6c84263282ea4682a6\",\"currency\":\"MYR\",\"configOverlays\":[],\"device\":\"Desktop\",\"maxPayout\":2500000,\"playerId\":\"Utest_71693913\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"bac8a801-dfe6-46fb-a4a6-01e0074372e6_CT_CashHunt\"}";
        //String crazyTime3 = "{\"gameType\":\"crazytime\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":0,\"startedAt\":\"2025-09-05T06:02:45.433Z\",\"result\":{\"outcome\":{\"wheelResult\":{\"slotMultiplier\":5,\"type\":\"WinningNumber\",\"wheelSector\":\"1\"},\"type\":\"SlotAndWheel\",\"topSlot\":{\"multiplier\":5,\"wheelSector\":\"1\"}}},\"wager\":0.30457500000000004,\"settledAt\":\"2025-09-05T06:03:12.626Z\",\"dealer\":{\"uid\":\"tts0rba_________\",\"name\":\"ROB_406\"},\"currency\":\"EUR\",\"id\":\"18624da29c7743dbb13e08f7\",\"table\":{\"name\":\"Crazy Time\",\"id\":\"CrazyTime0000001\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"18624da29c7743dbb13e08f7-tc6nq3dlkiyaamds\",\"currencyRateVersion\":\"tels2ktg2ajaaaab\",\"os\":\"macOS\",\"channel\":\"desktop\",\"screenName\":\"6666dfhjdf\",\"sessionId\":\"tc6nq3dlkiyaamdstemhqwbc5usacrcu5d252e53\",\"bets\":[{\"stake\":0.5,\"placedOn\":\"2025-09-05T06:03:00.608Z\",\"code\":\"CT_10\",\"payout\":0,\"transactionId\":\"4a3e92a1-2e68-49d5-9bd6-340accc9396c\"},{\"stake\":0.5,\"placedOn\":\"2025-09-05T06:03:00.608Z\",\"code\":\"CT_CoinFlip\",\"payout\":0,\"transactionId\":\"4a3e92a1-2e68-49d5-9bd6-340accc9396c\"},{\"stake\":0.5,\"placedOn\":\"2025-09-05T06:03:00.608Z\",\"code\":\"CT_CrazyTime\",\"payout\":0,\"transactionId\":\"4a3e92a1-2e68-49d5-9bd6-340accc9396c\"}],\"playMode\":\"RealMoney\",\"result\":{\"link\":\"/api/render/v2/html/ChBzdWJzd2luMjAwMDAwMDAxEhgxODYyNGRhMjljNzc0M2RiYjEzZTA4ZjcaEHRjNm5xM2Rsa2l5YWFtZHMiDlV0ZXN0XzYwMDU4MDEwKgwIoPrpxQYQgIHAqgIyCWV2b2x1dGlvbg\"},\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"382138f212534f68a865d1abd2b1e8d5\",\"currency\":\"MYR\",\"configOverlays\":[],\"device\":\"Desktop\",\"maxPayout\":2500000,\"playerId\":\"Utest_60058010\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"4a3e92a1-2e68-49d5-9bd6-340accc9396c_CT_CrazyTime\"}";
        System.out.println("crazyTime");
        //System.out.println(gameResultInfo(JSONObject.parseObject(crazyTime3, Map.class)));
        String lightningdice = "{\"id\":\"17c35aa4ab328ffb73807b67\",\"gameProvider\":\"evolution\",\"startedAt\":\"2024-04-05T10:12:26.624Z\",\"settledAt\":\"2024-04-05T10:12:54.476Z\",\"status\":\"Resolved\",\"gameType\":\"lightningdice\",\"table\":{\"id\":\"LightningDice001\",\"name\":\"Lightning Dice\"},\"dealer\":{\"uid\":\"tts0rjf_________\",\"name\":\"ROB_699\"},\"currency\":\"EUR\",\"participants\":[{\"casinoId\":\"0rnu7w1c8xhbuhtn\",\"playerId\":\"y_merkulova1\",\"screenName\":\"Yana\",\"playerGameId\":\"17c35aa4ab328ffb73807b67-rhz4wthrmmgaaajz\",\"sessionId\":\"rhz4wthrmmgaaajzr2wx4jl7rfdaaajy19018710\",\"casinoSessionId\":\"\",\"currency\":\"USD\",\"bets\":[{\"code\":\"DICE_High\",\"stake\":5,\"payout\":0,\"placedOn\":\"2024-04-05T10:12:48.289Z\",\"transactionId\":\"712114039178094512\",\"description\":\"\"},{\"code\":\"DICE_Total10\",\"stake\":5,\"payout\":0,\"placedOn\":\"2024-04-05T10:12:48.289Z\",\"transactionId\":\"712114039178094512\",\"description\":\"\"},{\"code\":\"DICE_Total11\",\"stake\":5,\"payout\":0,\"placedOn\":\"2024-04-05T10:12:48.289Z\",\"transactionId\":\"712114039178094512\",\"description\":\"\"},{\"code\":\"DICE_Total12\",\"stake\":5,\"payout\":0,\"placedOn\":\"2024-04-05T10:12:48.289Z\",\"transactionId\":\"712114039178094512\",\"description\":\"\"},{\"code\":\"DICE_Total13\",\"stake\":5,\"payout\":0,\"placedOn\":\"2024-04-05T10:12:48.289Z\",\"transactionId\":\"712114039178094512\",\"description\":\"\"},{\"code\":\"DICE_Total14\",\"stake\":5,\"payout\":0,\"placedOn\":\"2024-04-05T10:12:48.289Z\",\"transactionId\":\"712114039178094512\",\"description\":\"\"},{\"code\":\"DICE_Total15\",\"stake\":5,\"payout\":0,\"placedOn\":\"2024-04-05T10:12:48.289Z\",\"transactionId\":\"712114039178094512\",\"description\":\"\"},{\"code\":\"DICE_Total16\",\"stake\":5,\"payout\":0,\"placedOn\":\"2024-04-05T10:12:48.289Z\",\"transactionId\":\"712114039178094512\",\"description\":\"\"},{\"code\":\"DICE_Total17\",\"stake\":5,\"payout\":0,\"placedOn\":\"2024-04-05T10:12:48.289Z\",\"transactionId\":\"712114039178094512\",\"description\":\"\"},{\"code\":\"DICE_Total18\",\"stake\":5,\"payout\":0,\"placedOn\":\"2024-04-05T10:12:48.289Z\",\"transactionId\":\"712114039178094512\",\"description\":\"\"},{\"code\":\"DICE_Total3\",\"stake\":5,\"payout\":0,\"placedOn\":\"2024-04-05T10:12:48.289Z\",\"transactionId\":\"712114039178094512\",\"description\":\"\"},{\"code\":\"DICE_Total4\",\"stake\":5,\"payout\":0,\"placedOn\":\"2024-04-05T10:12:48.289Z\",\"transactionId\":\"712114039178094512\",\"description\":\"\"},{\"code\":\"DICE_Total5\",\"stake\":5,\"payout\":0,\"placedOn\":\"2024-04-05T10:12:48.289Z\",\"transactionId\":\"712114039178094512\",\"description\":\"\"},{\"code\":\"DICE_Total6\",\"stake\":5,\"payout\":0,\"placedOn\":\"2024-04-05T10:12:48.289Z\",\"transactionId\":\"712114039178094512\",\"description\":\"\"},{\"code\":\"DICE_Total7\",\"stake\":5,\"payout\":0,\"placedOn\":\"2024-04-05T10:12:48.289Z\",\"transactionId\":\"712114039178094512\",\"description\":\"\"},{\"code\":\"DICE_Total8\",\"stake\":5,\"payout\":0,\"placedOn\":\"2024-04-05T10:12:48.289Z\",\"transactionId\":\"712114039178094512\",\"description\":\"\"},{\"code\":\"DICE_Total9\",\"stake\":5,\"payout\":30,\"placedOn\":\"2024-04-05T10:12:48.289Z\",\"transactionId\":\"712114039178094512\",\"description\":\"\"},{\"code\":\"DICE_Triple\",\"stake\":5,\"payout\":125,\"placedOn\":\"2024-04-05T10:12:48.289Z\",\"transactionId\":\"712114039178094512\",\"description\":\"\"}],\"configOverlays\":[],\"playMode\":\"RealMoney\",\"channel\":\"desktop\",\"os\":\"macOS\",\"device\":\"Desktop\",\"currencyRateVersion\":\"r2vy6atny4rqaaab\"}],\"result\":{\"luckyNumbers\":{\"DICE_Total10\":[25],\"DICE_Total17\":[500]},\"winningNumbers\":{},\"second\":3,\"third\":3,\"first\":3},\"wager\":83.05379999999997,\"payout\":143.03711}";
        System.out.println("lightningdice");
        System.out.println(gameResultInfo(JSONObject.parseObject(lightningdice, Map.class)));
        String lightningscalablebj = "{\"id\":\"17c35b10182b12462c059d42\",\"gameProvider\":\"evolution\",\"startedAt\":\"2024-04-05T10:20:08.014Z\",\"settledAt\":\"2024-04-05T10:21:05.490Z\",\"status\":\"Resolved\",\"gameType\":\"lightningscalablebj\",\"table\":{\"id\":\"rxczli45lyyacnlx\",\"name\":\"joaomorais_lightning_sc_bj_dnt\"},\"dealer\":{\"uid\":\"tts0rbd_________\",\"name\":\"ROB_409\"},\"currency\":\"EUR\",\"participants\":[{\"casinoId\":\"0rnu7w1c8xhbuhtn\",\"playerId\":\"perftest000042\",\"screenName\":\"[AP] Kyle Torres\",\"playerGameId\":\"17c35b10182b12462c059d42-mrvou7o6yjqaaa4e\",\"sessionId\":\"mrvou7o6yjqaaa4er2wt4cz6womacadh49944db4\",\"casinoSessionId\":\"a2e2ba9707daf3c7eeff99458d9e13a4ab9539ac\",\"currency\":\"EUR\",\"bets\":[{\"code\":\"LBJ_LightningFee\",\"stake\":10,\"payout\":0,\"placedOn\":\"2024-04-05T10:20:27.041Z\",\"transactionId\":\"712262610451126047\",\"description\":\"\"},{\"code\":\"LBJ_PlayLightning\",\"stake\":10,\"payout\":0,\"placedOn\":\"2024-04-05T10:20:27.041Z\",\"transactionId\":\"712262610451126047\",\"description\":\"\"}],\"configOverlays\":[],\"playMode\":\"RealMoney\",\"channel\":\"desktop\",\"os\":\"Windows\",\"device\":\"Desktop\",\"skinId\":\"_default_\",\"brandId\":\"1\",\"currencyRateVersion\":\"r2vy6atny4rqaaab\",\"hands\":{\"hand1\":{\"decisions\":[{\"type\":\"Hit\",\"recordedAt\":\"2024-04-05T10:20:54.044Z\"}],\"score\":23,\"outcome\":\"Lose\",\"cards\":[\"8H\",\"5S\",\"QC\"],\"position\":\"Main\"}},\"appliedMultiplier\":{\"prevGameId\":\"17c35afb64f52bc4c0550faa\",\"payTableId\":3,\"currentFee\":10,\"value\":2,\"acquiredAt\":\"2024-04-05T10:20:08.014Z\",\"previousFee\":10}},{\"casinoId\":\"0rnu7w1c8xhbuhtn\",\"playerId\":\"perftest000145\",\"screenName\":\"[AP] Edward Murphy\",\"playerGameId\":\"17c35b10182b12462c059d42-mrvov632yjqaabf4\",\"sessionId\":\"mrvov632yjqaabf4r2wt4c4uwomacael94c04b5a\",\"casinoSessionId\":\"314aa4a5d3891557c4597b4a5bc3f540f4b4f0e9\",\"currency\":\"EUR\",\"bets\":[{\"code\":\"LBJ_LightningFee\",\"stake\":10,\"payout\":0,\"placedOn\":\"2024-04-05T10:20:27.042Z\",\"transactionId\":\"712262610451128047\",\"description\":\"\"},{\"code\":\"LBJ_PlayLightning\",\"stake\":10,\"payout\":0,\"placedOn\":\"2024-04-05T10:20:27.042Z\",\"transactionId\":\"712262610451128047\",\"description\":\"\"}],\"configOverlays\":[],\"playMode\":\"RealMoney\",\"channel\":\"desktop\",\"os\":\"Windows\",\"device\":\"Desktop\",\"skinId\":\"_default_\",\"brandId\":\"1\",\"currencyRateVersion\":\"r2vy6atny4rqaaab\",\"hands\":{\"hand1\":{\"decisions\":[{\"type\":\"Hit\",\"recordedAt\":\"2024-04-05T10:20:44.806Z\"}],\"score\":23,\"outcome\":\"Lose\",\"cards\":[\"8H\",\"5S\",\"QC\"],\"position\":\"Main\"}},\"appliedMultiplier\":{\"prevGameId\":\"17c35afb64f52bc4c0550faa\",\"payTableId\":3,\"currentFee\":10,\"value\":2,\"acquiredAt\":\"2024-04-05T10:20:08.014Z\",\"previousFee\":10}},{\"casinoId\":\"0rnu7w1c8xhbuhtn\",\"playerId\":\"perftest000148\",\"screenName\":\"[AP] Matthew Sanders\",\"playerGameId\":\"17c35b10182b12462c059d42-mrvov7v7yjqaabgd\",\"sessionId\":\"mrvov7v7yjqaabgdr2wt4c4owomacad7cb7c2721\",\"casinoSessionId\":\"8f4f019661fd59e86e32322b6b31fbd976a9b523\",\"currency\":\"EUR\",\"bets\":[{\"code\":\"LBJ_LightningFee\",\"stake\":10,\"payout\":0,\"placedOn\":\"2024-04-05T10:20:27.042Z\",\"transactionId\":\"712262610451130047\",\"description\":\"\"},{\"code\":\"SBJ_Main\",\"stake\":10,\"payout\":0,\"placedOn\":\"2024-04-05T10:20:27.042Z\",\"transactionId\":\"712262610451130047\",\"description\":\"\"}],\"configOverlays\":[],\"playMode\":\"RealMoney\",\"channel\":\"desktop\",\"os\":\"Windows\",\"device\":\"Desktop\",\"skinId\":\"_default_\",\"brandId\":\"1\",\"currencyRateVersion\":\"r2vy6atny4rqaaab\",\"hands\":{\"hand1\":{\"decisions\":[{\"type\":\"Stand\",\"recordedAt\":\"2024-04-05T10:20:52.426Z\"}],\"score\":13,\"outcome\":\"Lose\",\"cards\":[\"8H\",\"5S\"],\"position\":\"Main\"}}},{\"casinoId\":\"0rnu7w1c8xhbuhtn\",\"playerId\":\"perftest000080\",\"screenName\":\"[AP] Joshua Stewart\",\"playerGameId\":\"17c35b10182b12462c059d42-mrvovmh4yjqaaa7w\",\"sessionId\":\"mrvovmh4yjqaaa7wr2wt4cz6uyyab4kyadff9d8e\",\"casinoSessionId\":\"a204468c9ba313e4517680dc681f321d01f549ea\",\"currency\":\"EUR\",\"bets\":[{\"code\":\"LBJ_LightningFee\",\"stake\":10,\"payout\":0,\"placedOn\":\"2024-04-05T10:20:27.042Z\",\"transactionId\":\"712262610451132047\",\"description\":\"\"},{\"code\":\"SBJ_Main\",\"stake\":10,\"payout\":0,\"placedOn\":\"2024-04-05T10:20:27.042Z\",\"transactionId\":\"712262610451132047\",\"description\":\"\"}],\"configOverlays\":[],\"playMode\":\"RealMoney\",\"channel\":\"desktop\",\"os\":\"Windows\",\"device\":\"Desktop\",\"skinId\":\"_default_\",\"brandId\":\"1\",\"currencyRateVersion\":\"r2vy6atny4rqaaab\",\"hands\":{\"hand1\":{\"decisions\":[{\"type\":\"Stand\",\"recordedAt\":\"2024-04-05T10:21:00.545Z\"}],\"score\":13,\"outcome\":\"Lose\",\"cards\":[\"8H\",\"5S\"],\"position\":\"Main\"}}},{\"casinoId\":\"0rnu7w1c8xhbuhtn\",\"playerId\":\"perftest000092\",\"screenName\":\"[AP] Sandra Brooks\",\"playerGameId\":\"17c35b10182b12462c059d42-mrvovptgyjqaaba4\",\"sessionId\":\"mrvovptgyjqaaba4r2wt4cz7womacadp0706ea8f\",\"casinoSessionId\":\"281388d5ac475a34f4b5030c45889d586f828d05\",\"currency\":\"EUR\",\"bets\":[{\"code\":\"LBJ_LightningFee\",\"stake\":10,\"payout\":0,\"placedOn\":\"2024-04-05T10:20:27.041Z\",\"transactionId\":\"712262610451127047\",\"description\":\"\"},{\"code\":\"SBJ_Main\",\"stake\":10,\"payout\":0,\"placedOn\":\"2024-04-05T10:20:27.041Z\",\"transactionId\":\"712262610451127047\",\"description\":\"\"}],\"configOverlays\":[],\"playMode\":\"RealMoney\",\"channel\":\"desktop\",\"os\":\"Windows\",\"device\":\"Desktop\",\"skinId\":\"_default_\",\"brandId\":\"1\",\"currencyRateVersion\":\"r2vy6atny4rqaaab\",\"hands\":{\"hand1\":{\"decisions\":[{\"type\":\"Hit\",\"recordedAt\":\"2024-04-05T10:20:58.875Z\"}],\"score\":23,\"outcome\":\"Lose\",\"cards\":[\"8H\",\"5S\",\"QC\"],\"position\":\"Main\"}}},{\"casinoId\":\"0rnu7w1c8xhbuhtn\",\"playerId\":\"perftest000107\",\"screenName\":\"[AP] Ryan Scott\",\"playerGameId\":\"17c35b10182b12462c059d42-mrvovt27yjqaabcj\",\"sessionId\":\"mrvovt27yjqaabcjr2wt4c4dwomacadw989a1b7d\",\"casinoSessionId\":\"c0570504c9df1b823ae4bce669d53d9553b1096f\",\"currency\":\"EUR\",\"bets\":[{\"code\":\"LBJ_LightningFee\",\"stake\":10,\"payout\":0,\"placedOn\":\"2024-04-05T10:20:27.041Z\",\"transactionId\":\"712262610451129047\",\"description\":\"\"},{\"code\":\"LBJ_PlayLightning\",\"stake\":10,\"payout\":0,\"placedOn\":\"2024-04-05T10:20:27.041Z\",\"transactionId\":\"712262610451129047\",\"description\":\"\"}],\"configOverlays\":[],\"playMode\":\"RealMoney\",\"channel\":\"desktop\",\"os\":\"Windows\",\"device\":\"Desktop\",\"skinId\":\"_default_\",\"brandId\":\"1\",\"currencyRateVersion\":\"r2vy6atny4rqaaab\",\"hands\":{\"hand1\":{\"decisions\":[{\"type\":\"AutoStand\",\"recordedAt\":\"2024-04-05T10:21:01.988Z\"}],\"score\":13,\"outcome\":\"Lose\",\"cards\":[\"8H\",\"5S\"],\"position\":\"Main\"}},\"appliedMultiplier\":{\"prevGameId\":\"17c35afb64f52bc4c0550faa\",\"payTableId\":3,\"currentFee\":10,\"value\":2,\"acquiredAt\":\"2024-04-05T10:20:08.014Z\",\"previousFee\":10}},{\"casinoId\":\"0rnu7w1c8xhbuhtn\",\"playerId\":\"perftest000110\",\"screenName\":\"[AP] Zachary Cooper\",\"playerGameId\":\"17c35b10182b12462c059d42-mrvovvcsyjqaabcv\",\"sessionId\":\"mrvovvcsyjqaabcvr2wt4c4kuyyab4lb708fd2eb\",\"casinoSessionId\":\"a8fcdc9e1656fbbf58311263bf705f0742d6693e\",\"currency\":\"EUR\",\"bets\":[{\"code\":\"LBJ_LightningFee\",\"stake\":10,\"payout\":0,\"placedOn\":\"2024-04-05T10:20:27.042Z\",\"transactionId\":\"712262610451131047\",\"description\":\"\"},{\"code\":\"LBJ_PlayLightning\",\"stake\":10,\"payout\":0,\"placedOn\":\"2024-04-05T10:20:27.042Z\",\"transactionId\":\"712262610451131047\",\"description\":\"\"}],\"configOverlays\":[],\"playMode\":\"RealMoney\",\"channel\":\"desktop\",\"os\":\"Windows\",\"device\":\"Desktop\",\"skinId\":\"_default_\",\"brandId\":\"1\",\"currencyRateVersion\":\"r2vy6atny4rqaaab\",\"hands\":{\"hand1\":{\"decisions\":[{\"type\":\"AutoStand\",\"recordedAt\":\"2024-04-05T10:21:01.988Z\"}],\"score\":13,\"outcome\":\"Lose\",\"cards\":[\"8H\",\"5S\"],\"position\":\"Main\"}},\"appliedMultiplier\":{\"prevGameId\":\"17c35afb64f52bc4c0550faa\",\"payTableId\":3,\"currentFee\":10,\"value\":2,\"acquiredAt\":\"2024-04-05T10:20:08.014Z\",\"previousFee\":10}}],\"result\":{\"dealtToPlayer\":[\"8H\",\"5S\",\"QC\"],\"dealerHand\":{\"score\":20,\"cards\":[\"4C\",\"8D\",\"8D\"]},\"wonSideBets\":[],\"lightningPayTable\":{\"id\":5,\"value\":{\"19\":6,\"21\":12,\"17\":2,\"20\":8,\"18\":5,\"BJ\":20}}},\"wager\":140,\"payout\":0}";
        System.out.println("lightningscalablebj");
        System.out.println(gameResultInfo(JSONObject.parseObject(lightningscalablebj, Map.class)));

        String lastDeal = "{\"gameType\":\"rng-dealnodeal\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":14.99362,\"startedAt\":\"2025-09-01T10:40:35.590Z\",\"result\":{\"eliminatedBoxes\":[[\"15\",\"16\",\"5\"],[\"10\",\"3\",\"7\",\"8\"],[\"1\",\"11\",\"13\",\"4\"],[\"12\",\"2\",\"6\"],[\"9\"]],\"lastBox\":\"14\",\"mainPhaseStartedAt\":\"2025-09-01T10:40:35.834Z\",\"parentGameId\":\"18612279aa044e706f9987f3\"},\"wager\":5.397703,\"settledAt\":\"2025-09-01T10:41:43.756Z\",\"dealer\":{\"uid\":\"no-dealer\",\"name\":\"No Dealer\"},\"currency\":\"EUR\",\"id\":\"18612279aa044e706f9987f3\",\"table\":{\"name\":\"First Person Deal or No Deal\",\"id\":\"RngDealNoDeal001\"},\"status\":\"Resolved\",\"participants\":[{\"boxes\":{\"11\":60,\"12\":75,\"13\":100,\"14\":125,\"15\":250,\"16\":375,\"1\":0.5,\"2\":1,\"3\":2.5,\"4\":3.5,\"5\":5,\"6\":10,\"7\":15,\"8\":25,\"9\":40,\"10\":50},\"offers\":[{\"offeredAt\":\"2025-09-01T10:40:53.455Z\",\"offer\":39.04,\"decision\":\"NoDeal\",\"decidedAt\":\"2025-09-01T10:41:05.470Z\"},{\"offeredAt\":\"2025-09-01T10:41:05.520Z\",\"offer\":46.11,\"decision\":\"NoDeal\",\"decidedAt\":\"2025-09-01T10:41:19.054Z\"},{\"offeredAt\":\"2025-09-01T10:41:19.105Z\",\"offer\":50.2,\"decision\":\"NoDeal\",\"decidedAt\":\"2025-09-01T10:41:31.858Z\"},{\"offeredAt\":\"2025-09-01T10:41:31.911Z\",\"offer\":82.5,\"decision\":\"NoDeal\",\"decidedAt\":\"2025-09-01T10:41:43.676Z\"}],\"playerGameId\":\"18612279aa044e706f9987f3\",\"currencyRateVersion\":\"tebjhotaas4qaaae\",\"os\":\"macOS\",\"qualificationSpin\":{\"bet\":5,\"slots\":[2,2,1,0],\"ringsCount\":2,\"bigBox\":375,\"totalBet\":45,\"bigBoxNumber\":\"16\"},\"channel\":\"desktop\",\"screenName\":\"1答复\",\"sessionId\":\"td226fkviiyqacuxtecnshsyr7eafmue75ce909a\",\"qualifiedAt\":\"2025-09-01T10:40:36.812Z\",\"bets\":[{\"stake\":45,\"placedOn\":\"2025-09-01T10:40:36.812Z\",\"code\":\"DOND_SpinOneRings\",\"payout\":125,\"transactionId\":\"2353c01d-ae77-493d-bdfb-fdf6a7eef7bc\"}],\"topUpSpins\":[],\"playMode\":\"RealMoney\",\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"a54b1b2fabf44d3bacab1b4480305c7e\",\"currency\":\"CNY\",\"configOverlays\":[],\"device\":\"Desktop\",\"playerId\":\"Utest_45189822\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"2353c01d-ae77-493d-bdfb-fdf6a7eef7bc_DOND_SpinOneRings\"}\n";
        System.out.println("lastDeal");
        System.out.println(gameResultInfo(JSONObject.parseObject(lastDeal, Map.class)));
        String nooff = "{\"gameType\":\"rng-dealnodeal\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":0,\"startedAt\":\"2025-09-01T04:47:12.163Z\",\"result\":{\"eliminatedBoxes\":[],\"mainPhaseStartedAt\":\"2025-09-01T04:47:12.439Z\",\"parentGameId\":\"18610f30db484fab73b1cf53\"},\"wager\":0.599745,\"settledAt\":\"2025-09-01T04:47:13.194Z\",\"dealer\":{\"uid\":\"no-dealer\",\"name\":\"No Dealer\"},\"currency\":\"EUR\",\"id\":\"18610f30db484fab73b1cf53\",\"table\":{\"name\":\"First Person Deal or No Deal\",\"id\":\"RngDealNoDeal001\"},\"status\":\"Resolved\",\"participants\":[{\"offers\":[],\"playerGameId\":\"18610f30db484fab73b1cf53\",\"currencyRateVersion\":\"tebjhotaas4qaaae\",\"os\":\"macOS\",\"qualificationSpin\":{\"bet\":5,\"slots\":[1,0,1,1],\"ringsCount\":0,\"bigBox\":500,\"totalBet\":5,\"bigBoxNumber\":\"16\"},\"channel\":\"desktop\",\"screenName\":\"1答复\",\"sessionId\":\"td226fkviiyqacuxtebzrsmaztcafcwa3f23b709\",\"qualifiedAt\":\"2025-09-01T04:47:13.194Z\",\"bets\":[{\"stake\":5,\"placedOn\":\"2025-09-01T04:47:13.194Z\",\"code\":\"DOND_SpinThreeRings\",\"payout\":0,\"transactionId\":\"961ca811-1308-444a-bb99-57d68540613d\"}],\"topUpSpins\":[],\"playMode\":\"RealMoney\",\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"21ff3afc07744195a74ed60064231f46\",\"currency\":\"CNY\",\"configOverlays\":[],\"device\":\"Desktop\",\"playerId\":\"Utest_45189822\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"961ca811-1308-444a-bb99-57d68540613d_DOND_SpinThreeRings\"}\n";
        System.out.println("nooff");
        System.out.println(gameResultInfo(JSONObject.parseObject(nooff, Map.class)));
        String deal = "{\"gameType\":\"rng-dealnodeal\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":6.564807,\"startedAt\":\"2025-09-01T10:37:14.089Z\",\"result\":{\"eliminatedBoxes\":[[\"10\",\"15\",\"2\"]],\"mainPhaseStartedAt\":\"2025-09-01T10:37:14.408Z\",\"parentGameId\":\"1861224abf9f0d2f6b961d93\"},\"wager\":5.397703,\"settledAt\":\"2025-09-01T10:37:41.484Z\",\"dealer\":{\"uid\":\"no-dealer\",\"name\":\"No Dealer\"},\"currency\":\"EUR\",\"id\":\"1861224abf9f0d2f6b961d93\",\"table\":{\"name\":\"First Person Deal or No Deal\",\"id\":\"RngDealNoDeal001\"},\"status\":\"Resolved\",\"participants\":[{\"boxes\":{\"11\":60,\"12\":75,\"13\":100,\"14\":125,\"15\":500,\"16\":250,\"1\":0.5,\"2\":1,\"3\":2.5,\"4\":3.5,\"5\":5,\"6\":10,\"7\":15,\"8\":25,\"9\":40,\"10\":50},\"offers\":[{\"offeredAt\":\"2025-09-01T10:37:28.086Z\",\"offer\":54.73,\"decision\":\"Deal\",\"decidedAt\":\"2025-09-01T10:37:41.484Z\"}],\"playerGameId\":\"1861224abf9f0d2f6b961d93\",\"currencyRateVersion\":\"tebjhotaas4qaaae\",\"os\":\"macOS\",\"qualificationSpin\":{\"bet\":5,\"slots\":[2,2,1,1],\"ringsCount\":2,\"bigBox\":500,\"totalBet\":45,\"bigBoxNumber\":\"15\"},\"channel\":\"desktop\",\"screenName\":\"1答复\",\"sessionId\":\"td226fkviiyqacuxtecnshsyr7eafmue75ce909a\",\"qualifiedAt\":\"2025-09-01T10:37:15.065Z\",\"bets\":[{\"stake\":45,\"placedOn\":\"2025-09-01T10:37:15.065Z\",\"code\":\"DOND_SpinOneRings\",\"payout\":54.73,\"transactionId\":\"6ba9e770-59c9-4169-93ce-482a6c3ee746\"}],\"topUpSpins\":[],\"playMode\":\"RealMoney\",\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"a54b1b2fabf44d3bacab1b4480305c7e\",\"currency\":\"CNY\",\"configOverlays\":[],\"device\":\"Desktop\",\"playerId\":\"Utest_45189822\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"6ba9e770-59c9-4169-93ce-482a6c3ee746_DOND_SpinOneRings\"}";
        System.out.println("deal");
        System.out.println(gameResultInfo(JSONObject.parseObject(deal, Map.class)));
        String sicbo = "{\"gameType\":\"sicbo\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":2.410039,\"startedAt\":\"2025-09-03T02:57:41.801Z\",\"result\":{\"winningNumbers\":{},\"luckyNumbers\":{\"SicBo_Combo4And5\":[15],\"SicBo_Combo3And5\":[10],\"SicBo_Total8\":[25],\"SicBo_Combo2And6\":[10],\"SicBo_Triple6\":[500],\"SicBo_Double5\":[20],\"SicBo_Total16\":[88]},\"third\":2,\"first\":1,\"second\":1},\"wager\":9.64016,\"settledAt\":\"2025-09-03T02:58:07.040Z\",\"dealer\":{\"uid\":\"tts0rg5_________\",\"name\":\"ROB_581\"},\"currency\":\"EUR\",\"id\":\"1861a66035cebfff14849316\",\"table\":{\"name\":\"Super Sic Bo\",\"id\":\"SuperSicBo000001\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"1861a66035cebfff14849316-td226fkviiyqacux\",\"currencyRateVersion\":\"tegoa4taas4qaaag\",\"os\":\"macOS\",\"channel\":\"desktop\",\"screenName\":\"1答复\",\"sessionId\":\"td226fkviiyqacuxtegydbyg6lgaaray249a4131\",\"bets\":[{\"stake\":10,\"placedOn\":\"2025-09-03T02:58:00.603Z\",\"code\":\"SicBo_3\",\"payout\":0,\"transactionId\":\"bd3edead-0a7f-4aef-93df-1172fec748bd\"},{\"stake\":10,\"placedOn\":\"2025-09-03T02:58:00.603Z\",\"code\":\"SicBo_Combo2And3\",\"payout\":0,\"transactionId\":\"bd3edead-0a7f-4aef-93df-1172fec748bd\"},{\"stake\":10,\"placedOn\":\"2025-09-03T02:58:00.603Z\",\"code\":\"SicBo_Combo2And5\",\"payout\":0,\"transactionId\":\"bd3edead-0a7f-4aef-93df-1172fec748bd\"},{\"stake\":10,\"placedOn\":\"2025-09-03T02:58:00.603Z\",\"code\":\"SicBo_Double2\",\"payout\":0,\"transactionId\":\"bd3edead-0a7f-4aef-93df-1172fec748bd\"},{\"stake\":10,\"placedOn\":\"2025-09-03T02:58:00.603Z\",\"code\":\"SicBo_Odd\",\"payout\":0,\"transactionId\":\"bd3edead-0a7f-4aef-93df-1172fec748bd\"},{\"stake\":10,\"placedOn\":\"2025-09-03T02:58:00.603Z\",\"code\":\"SicBo_Small\",\"payout\":20,\"transactionId\":\"bd3edead-0a7f-4aef-93df-1172fec748bd\"},{\"stake\":10,\"placedOn\":\"2025-09-03T02:58:00.603Z\",\"code\":\"SicBo_Triple2\",\"payout\":0,\"transactionId\":\"bd3edead-0a7f-4aef-93df-1172fec748bd\"},{\"stake\":10,\"placedOn\":\"2025-09-03T02:58:00.603Z\",\"code\":\"SicBo_Triple5\",\"payout\":0,\"transactionId\":\"bd3edead-0a7f-4aef-93df-1172fec748bd\"}],\"playMode\":\"RealMoney\",\"result\":{\"link\":\"/api/render/v2/html/ChBzdWJzd2luMjAwMDAwMDAxEhgxODYxYTY2MDM1Y2ViZmZmMTQ4NDkzMTYaEHRkMjI2Zmt2aWl5cWFjdXgiDlV0ZXN0XzQ1MTg5ODIyKgsIv93exQYQgLSJEzIJZXZvbHV0aW9u\"},\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"b5b98ec75e504b48b11f8206d601a4fa\",\"currency\":\"CNY\",\"configOverlays\":[],\"device\":\"Desktop\",\"playerId\":\"Utest_45189822\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"bd3edead-0a7f-4aef-93df-1172fec748bd_SicBo_Triple5\"}";
        System.out.println("sicbo");
        System.out.println(gameResultInfo(JSONObject.parseObject(sicbo, Map.class)));
        String parseThb = "{\"gameType\":\"thb\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":0,\"startedAt\":\"2025-09-01T10:43:29.358Z\",\"result\":{\"cards\":{\"flop\":[\"QH\",\"9C\",\"TH\"],\"dealer\":[\"AC\",\"TC\"],\"river\":[\"QC\"],\"turn\":\"9D\",\"player\":[\"4D\",\"2H\"]},\"jackpotOutcome\":{\"cards\":[\"QH\",\"TH\",\"9C\",\"4D\",\"2H\"],\"type\":\"None\"},\"bonus\":{\"holeCardsBonus\":{\"cards\":[\"4D\",\"2H\",\"AC\",\"TC\"],\"type\":\"None\",\"handType\":\"HoleHandBonusOutcome\"}},\"dealer\":{\"qualified\":true,\"cards\":[\"QC\",\"QH\",\"TC\",\"TH\",\"AC\"],\"rank\":2732,\"type\":\"TwoPairs\"},\"outcome\":\"Dealer\",\"player\":{\"cards\":[\"QC\",\"QH\",\"9D\",\"9C\",\"TH\"],\"rank\":2746,\"type\":\"TwoPairs\"}},\"wager\":11.994895,\"settledAt\":\"2025-09-01T10:44:17.992Z\",\"dealer\":{\"uid\":\"tts0rfn_________\",\"name\":\"ROB_563\"},\"currency\":\"EUR\",\"id\":\"186122a21f6628633f008a52\",\"table\":{\"name\":\"Texas Hold'em Bonus Poker\",\"id\":\"THBTable00000001\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"186122a21f6628633f008a52-tcy72shckiyaajdx\",\"currencyRateVersion\":\"tebjhotaas4qaaae\",\"os\":\"macOS\",\"channel\":\"desktop\",\"screenName\":\"mufan\",\"sessionId\":\"tcy72shckiyaajdxtecn3v4vztcafqrzf58db745\",\"bets\":[{\"stake\":20,\"placedOn\":\"2025-09-01T10:43:43.856Z\",\"code\":\"THB_AnteBet\",\"payout\":0,\"transactionId\":\"e36b69bb-3db0-4449-acfa-e676314a255d\"},{\"stake\":40,\"placedOn\":\"2025-09-01T10:43:57.482Z\",\"code\":\"THB_FlopBet\",\"payout\":0,\"transactionId\":\"996898a0-18d6-493d-803a-4fde483c970d\"},{\"stake\":20,\"placedOn\":\"2025-09-01T10:44:08.675Z\",\"code\":\"THB_TurnBet\",\"payout\":0,\"transactionId\":\"c5413bc1-496e-4085-9502-892dace5248f\"},{\"stake\":20,\"placedOn\":\"2025-09-01T10:44:16.001Z\",\"code\":\"THB_RiverBet\",\"payout\":0,\"transactionId\":\"70f1edaa-4d42-4723-9b22-db7662e304bb\"}],\"playMode\":\"RealMoney\",\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"e4cee25af0eb4330848e43ccd778e7bc\",\"currency\":\"CNY\",\"configOverlays\":[],\"decisions\":[{\"recordedAt\":\"2025-09-01T10:43:58.178Z\",\"type\":\"Flop\"},{\"recordedAt\":\"2025-09-01T10:44:09.346Z\",\"type\":\"Turn\"},{\"recordedAt\":\"2025-09-01T10:44:16.660Z\",\"type\":\"River\"}],\"device\":\"Desktop\",\"playerId\":\"Utest_43903257\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"70f1edaa-4d42-4723-9b22-db7662e304bb_THB_RiverBet\"}";
        System.out.println("parseThb");
        System.out.println(gameResultInfo(JSONObject.parseObject(parseThb, Map.class)));
        String baccarat = "{\"gameType\":\"baccarat\",\"gameSubType\":\"redenvelopev2\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":0,\"startedAt\":\"2025-09-03T04:47:12.984Z\",\"result\":{\"sideBetPerfectPair\":\"Lose\",\"bankerInsuranceOutcome\":\"Lose\",\"sideBetEitherPair\":\"Lose\",\"sideBetPlayerPair\":\"Lose\",\"sideBetPlayerBonus\":\"Win\",\"playerInsuranceOutcome\":\"Lose\",\"sideBetBankerPair\":\"Lose\",\"banker\":{\"score\":1,\"cards\":[\"7H\",\"4D\"]},\"redEnvelopePayouts\":{},\"sideBetBankerBonus\":\"Lose\",\"outcome\":\"Player\",\"player\":{\"score\":9,\"cards\":[\"4D\",\"5S\"]}},\"wager\":30.12549,\"settledAt\":\"2025-09-03T04:47:42.096Z\",\"dealer\":{\"uid\":\"tts0rg6_________\",\"name\":\"ROB_582\"},\"currency\":\"EUR\",\"id\":\"1861ac5a2ece14246e98aefd\",\"table\":{\"name\":\"DNT Baccarat Squeeze (850)\",\"id\":\"zixzea8nrf1675oh\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"1861ac5a2ece14246e98aefd-td226fkviiyqacux\",\"currencyRateVersion\":\"tegoa4taas4qaaag\",\"sideBetPerfectPair\":\"Lose\",\"os\":\"macOS\",\"sideBetPlayerPair\":\"Lose\",\"channel\":\"desktop\",\"screenName\":\"1答复\",\"sessionId\":\"td226fkviiyqacuxteg6kiucbe3qar3x207f0730\",\"bets\":[{\"stake\":50,\"placedOn\":\"2025-09-03T04:47:37.088Z\",\"code\":\"BAC_Banker\",\"payout\":0,\"transactionId\":\"9bd422af-8179-4cbd-888a-d7d1aa04d31b\"},{\"stake\":50,\"placedOn\":\"2025-09-03T04:47:37.088Z\",\"code\":\"BAC_BankerBonus\",\"payout\":0,\"transactionId\":\"9bd422af-8179-4cbd-888a-d7d1aa04d31b\"},{\"stake\":50,\"placedOn\":\"2025-09-03T04:47:37.088Z\",\"code\":\"BAC_PerfectPair\",\"payout\":0,\"transactionId\":\"9bd422af-8179-4cbd-888a-d7d1aa04d31b\"},{\"stake\":50,\"placedOn\":\"2025-09-03T04:47:37.088Z\",\"code\":\"BAC_PlayerPair\",\"payout\":0,\"transactionId\":\"9bd422af-8179-4cbd-888a-d7d1aa04d31b\"},{\"stake\":50,\"placedOn\":\"2025-09-03T04:47:37.088Z\",\"code\":\"BAC_Tie\",\"payout\":0,\"transactionId\":\"9bd422af-8179-4cbd-888a-d7d1aa04d31b\"}],\"playMode\":\"RealMoney\",\"result\":{\"link\":\"/api/render/v2/html/ChBzdWJzd2luMjAwMDAwMDAxEhgxODYxYWM1YTJlY2UxNDI0NmU5OGFlZmQaEHRkMjI2Zmt2aWl5cWFjdXgiDlV0ZXN0XzQ1MTg5ODIyKgsI7pDfxQYQgLDjLTIJZXZvbHV0aW9u\"},\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"91e107dd901b4f81b73d31850d139a56\",\"currency\":\"CNY\",\"configOverlays\":[],\"subType\":\"redenvelopev2\",\"device\":\"Desktop\",\"sideBetBankerBonus\":\"Lose\",\"playerId\":\"Utest_45189822\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"9bd422af-8179-4cbd-888a-d7d1aa04d31b_BAC_Tie\"}";
        System.out.println("baccarat");
        System.out.println(gameResultInfo(JSONObject.parseObject(baccarat, Map.class)));

        String andarbahar = "{\"gameType\":\"andarbahar\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":2.273795,\"startedAt\":\"2025-09-02T10:24:03.431Z\",\"result\":{\"cardsDealt\":23,\"andar\":[\"9S\",\"6S\",\"6D\",\"9D\",\"AS\",\"2S\",\"7C\",\"8S\",\"2D\",\"4C\",\"4D\",\"QH\"],\"resolvingCard\":\"QH\",\"winningSideBet\":{\"betCode\":\"AB_Cards_21_25\"},\"bahar\":[\"TC\",\"3S\",\"AH\",\"TS\",\"6C\",\"JC\",\"3C\",\"5D\",\"TD\",\"JH\",\"8H\"],\"joker\":\"QD\",\"multipliedSideBets\":[{\"betCode\":\"AB_Cards_1_5\",\"multiplier\":7}],\"outcome\":\"Andar\",\"deckNumber\":1},\"wager\":3.5902019999999997,\"settledAt\":\"2025-09-02T10:24:47.880Z\",\"dealer\":{\"uid\":\"tts0ri0_________\",\"name\":\"ROB_648\"},\"currency\":\"EUR\",\"id\":\"1861702612d218a89f1a4932\",\"table\":{\"name\":\"Super Andar Bahar\",\"id\":\"AndarBahar000001\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"1861702612d218a89f1a4932-td226fkviiyqacux\",\"currencyRateVersion\":\"ted3uftaas4qaaaf\",\"os\":\"macOS\",\"channel\":\"desktop\",\"screenName\":\"1答复\",\"sessionId\":\"td226fkviiyqacuxtee7eljkyasqar6rcf12acfc\",\"bets\":[{\"stake\":10,\"placedOn\":\"2025-09-02T10:24:20.193Z\",\"code\":\"AB_Andar\",\"payout\":19,\"transactionId\":\"9b208bcf-0c0a-4643-b7c7-cb578c0c65c1\"},{\"stake\":10,\"placedOn\":\"2025-09-02T10:24:20.193Z\",\"code\":\"AB_Bahar\",\"payout\":0,\"transactionId\":\"9b208bcf-0c0a-4643-b7c7-cb578c0c65c1\"},{\"stake\":10,\"placedOn\":\"2025-09-02T10:24:20.193Z\",\"code\":\"AB_Cards_16_20\",\"payout\":0,\"transactionId\":\"9b208bcf-0c0a-4643-b7c7-cb578c0c65c1\"}],\"playMode\":\"RealMoney\",\"result\":{\"link\":\"/api/render/v2/html/ChBzdWJzd2luMjAwMDAwMDAxEhgxODYxNzAyNjEyZDIxOGE4OWYxYTQ5MzIaEHRkMjI2Zmt2aWl5cWFjdXgiDlV0ZXN0XzQ1MTg5ODIyKgwI74vbxQYQgPjOowMyCWV2b2x1dGlvbg\"},\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"048c816d1a2f4cc288652b2db895470f\",\"currency\":\"CNY\",\"configOverlays\":[],\"device\":\"Desktop\",\"playerId\":\"Utest_45189822\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"9b208bcf-0c0a-4643-b7c7-cb578c0c65c1_AB_Cards_16_20\"}";
        System.out.println("andarbahar");
        System.out.println(gameResultInfo(JSONObject.parseObject(andarbahar, Map.class)));

        String cashorcrash = "{\"gameType\":\"cashorcrash\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":0,\"startedAt\":\"2025-09-03T04:30:45.819Z\",\"result\":{\"payoutLevel\":0,\"reachedMultiplier\":0,\"drawnBalls\":[{\"payoutLevel\":0,\"reachedMultiplier\":0,\"ballType\":\"Red\",\"ballId\":\"ball-0\"}]},\"wager\":120.501959,\"settledAt\":\"2025-09-03T04:31:01.786Z\",\"dealer\":{\"uid\":\"tts0rbr_________\",\"name\":\"ROB_423\"},\"currency\":\"EUR\",\"id\":\"1861ab745739d63327aab974\",\"table\":{\"name\":\"Cash Or Crash\",\"id\":\"CashOrCrash00001\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"1861ab745739d63327aab974-td226fkviiyqacux\",\"currencyRateVersion\":\"tegoa4taas4qaaag\",\"os\":\"macOS\",\"channel\":\"desktop\",\"payoutLimitMultiplier\":10,\"screenName\":\"1答复\",\"sessionId\":\"td226fkviiyqacuxteg5kexmbe3qarmqba62b691\",\"bets\":[{\"stake\":1000,\"placedOn\":\"2025-09-03T04:30:55.959Z\",\"code\":\"COC_Main\",\"payout\":0,\"transactionId\":\"5a167fd4-2745-45b5-8f16-af693a3fabca\"}],\"playMode\":\"RealMoney\",\"result\":{\"link\":\"/api/render/v2/html/ChBzdWJzd2luMjAwMDAwMDAxEhgxODYxYWI3NDU3MzlkNjMzMjdhYWI5NzQaEHRkMjI2Zmt2aWl5cWFjdXgiDlV0ZXN0XzQ1MTg5ODIyKgwIhYnfxQYQgNHl9gIyCWV2b2x1dGlvbg\"},\"gameSteps\":[{\"autoContinue\":false,\"type\":\"BaseGameStep\"}],\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"e9b11956ca90484ca36e4f7d4d009d33\",\"currency\":\"CNY\",\"configOverlays\":[],\"device\":\"Desktop\",\"playerId\":\"Utest_45189822\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"5a167fd4-2745-45b5-8f16-af693a3fabca_COC_Main\"}";
        System.out.println("cashorcrash");
        System.out.println(gameResultInfo(JSONObject.parseObject(cashorcrash, Map.class)));


        System.out.println("Blackjack");
        System.out.println(gameResultInfo(JSONObject.parseObject(blackjack, Map.class)));
        System.out.println("craps");
        System.out.println(gameResultInfo(JSONObject.parseObject(craps, Map.class)));
        String crazytime = "{\"gameType\":\"crazytime\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":0.239898,\"startedAt\":\"2025-09-01T03:23:22.437Z\",\"result\":{\"outcome\":{\"wheelResult\":{\"type\":\"WinningNumber\",\"wheelSector\":\"1\"},\"type\":\"SlotAndWheel\",\"topSlot\":{\"wheelSector\":\"CoinFlip\"}}},\"wager\":1.4393880000000001,\"settledAt\":\"2025-09-01T03:23:49.650Z\",\"dealer\":{\"uid\":\"tts0r3c_________\",\"name\":\"ROB_120\"},\"currency\":\"EUR\",\"id\":\"18610a9dc840937bc15568a1\",\"table\":{\"name\":\"Crazy Time\",\"id\":\"CrazyTime0000001\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"18610a9dc840937bc15568a1-td226fkviiyqacux\",\"currencyRateVersion\":\"tebjhotaas4qaaae\",\"os\":\"macOS\",\"channel\":\"desktop\",\"screenName\":\"1答复\",\"sessionId\":\"td226fkviiyqacuxtebuyhasztcafadkf37a2526\",\"bets\":[{\"stake\":1,\"placedOn\":\"2025-09-01T03:23:37.639Z\",\"code\":\"CT_1\",\"payout\":2,\"transactionId\":\"4c26ea7e-989d-4fbf-a694-da870e67be40\"},{\"stake\":2,\"placedOn\":\"2025-09-01T03:23:37.639Z\",\"code\":\"CT_10\",\"payout\":0,\"transactionId\":\"4c26ea7e-989d-4fbf-a694-da870e67be40\"},{\"stake\":1,\"placedOn\":\"2025-09-01T03:23:37.639Z\",\"code\":\"CT_2\",\"payout\":0,\"transactionId\":\"4c26ea7e-989d-4fbf-a694-da870e67be40\"},{\"stake\":2,\"placedOn\":\"2025-09-01T03:23:37.639Z\",\"code\":\"CT_5\",\"payout\":0,\"transactionId\":\"4c26ea7e-989d-4fbf-a694-da870e67be40\"},{\"stake\":2,\"placedOn\":\"2025-09-01T03:23:37.639Z\",\"code\":\"CT_CashHunt\",\"payout\":0,\"transactionId\":\"4c26ea7e-989d-4fbf-a694-da870e67be40\"},{\"stake\":2,\"placedOn\":\"2025-09-01T03:23:37.639Z\",\"code\":\"CT_CoinFlip\",\"payout\":0,\"transactionId\":\"4c26ea7e-989d-4fbf-a694-da870e67be40\"},{\"stake\":1,\"placedOn\":\"2025-09-01T03:23:37.639Z\",\"code\":\"CT_CrazyTime\",\"payout\":0,\"transactionId\":\"4c26ea7e-989d-4fbf-a694-da870e67be40\"},{\"stake\":1,\"placedOn\":\"2025-09-01T03:23:37.639Z\",\"code\":\"CT_Pachinko\",\"payout\":0,\"transactionId\":\"4c26ea7e-989d-4fbf-a694-da870e67be40\"}],\"playMode\":\"RealMoney\",\"result\":{\"link\":\"/api/render/v2/html/ChBzdWJzd2luMjAwMDAwMDAxEhgxODYxMGE5ZGM4NDA5MzdiYzE1NTY4YTEaEHRkMjI2Zmt2aWl5cWFjdXgiDlV0ZXN0XzQ1MTg5ODIyKgwIxaPUxQYQgO34tQIyCWV2b2x1dGlvbg\"},\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"6b009c398b964c7ab096aa6138017012\",\"currency\":\"CNY\",\"configOverlays\":[],\"totalMultiplier\":1,\"device\":\"Desktop\",\"maxPayout\":5000000,\"playerId\":\"Utest_45189822\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"4c26ea7e-989d-4fbf-a694-da870e67be40_CT_Pachinko\"}";
        System.out.println("crazytime");
        System.out.println(gameResultInfo(JSONObject.parseObject(crazytime, Map.class)));

        String csp = "{\"gameType\":\"csp\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":0,\"startedAt\":\"2025-09-02T09:28:19.513Z\",\"result\":{\"cards\":{\"flop\":[],\"dealer\":[\"TD\",\"7S\",\"5S\",\"QC\",\"2S\"],\"river\":[],\"player\":[\"KD\",\"8H\",\"4C\",\"6H\",\"AC\"]},\"jackpotOutcome\":{\"cards\":[\"AC\",\"KD\",\"8H\",\"6H\",\"4C\"],\"type\":\"None\"},\"bonus\":{\"fivePlusOneBonus\":{\"cards\":[\"AC\",\"KD\",\"TD\",\"8H\",\"6H\",\"4C\"],\"type\":\"HighCard\",\"handType\":\"FullHandBonusOutcome\"}},\"dealer\":{\"qualified\":false,\"cards\":[\"QC\",\"TD\",\"7S\",\"5S\",\"2S\"],\"rank\":7133,\"type\":\"HighCard\"},\"outcome\":\"Player\",\"player\":{\"cards\":[\"AC\",\"KD\",\"8H\",\"6H\",\"4C\"],\"rank\":6321,\"type\":\"HighCard\"}},\"wager\":17.95101,\"settledAt\":\"2025-09-02T09:29:03.285Z\",\"dealer\":{\"uid\":\"tts0rgq_________\",\"name\":\"ROB_602\"},\"currency\":\"EUR\",\"id\":\"18616d1ca90e5bc534d2d693\",\"table\":{\"name\":\"Caribbean Stud Poker\",\"id\":\"CSPTable00000001\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"18616d1ca90e5bc534d2d693-td226fkviiyqacux\",\"currencyRateVersion\":\"ted3uftaas4qaaaf\",\"sideBet5p1\":{\"betCode\":\"CSP_5plus1BonusBet\",\"result\":\"Lose\"},\"os\":\"macOS\",\"channel\":\"desktop\",\"screenName\":\"1答复\",\"sessionId\":\"td226fkviiyqacuxtee37ly3yasqapjp7d9ebb89\",\"bets\":[{\"stake\":100,\"placedOn\":\"2025-09-02T09:28:37.005Z\",\"code\":\"CSP_5plus1BonusBet\",\"payout\":0,\"transactionId\":\"e2ae087e-6ca9-4c93-ac12-7d0f3ce4d990\"},{\"stake\":50,\"placedOn\":\"2025-09-02T09:28:37.005Z\",\"code\":\"CSP_AnteBet\",\"payout\":0,\"transactionId\":\"e2ae087e-6ca9-4c93-ac12-7d0f3ce4d990\"}],\"playMode\":\"RealMoney\",\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"d703ae9d85834149b9d682d5a907b3f9\",\"currency\":\"CNY\",\"configOverlays\":[],\"decisions\":[{\"recordedAt\":\"2025-09-02T09:28:58.962Z\",\"type\":\"AutoFold\"}],\"device\":\"Desktop\",\"playerId\":\"Utest_45189822\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"e2ae087e-6ca9-4c93-ac12-7d0f3ce4d990_CSP_AnteBet\"}";
        System.out.println("csp");
        System.out.println(gameResultInfo(JSONObject.parseObject(csp, Map.class)));

        String dhp = "{\"id\":\"17c31368e87ddc1eaa7e3daf\",\"gameProvider\":\"evolution\",\"startedAt\":\"2024-04-04T12:27:04.629Z\",\"settledAt\":\"2024-04-04T12:27:36.191Z\",\"status\":\"Resolved\",\"gameType\":\"dhp\",\"table\":{\"id\":\"rxbatnlmu3jaaob3\",\"name\":\"joaomorais_holdem_dnt\"},\"dealer\":{\"uid\":\"tts0rel_________\",\"name\":\"ROB_525\"},\"currency\":\"EUR\",\"participants\":[{\"casinoId\":\"0rnu7w1c8xhbuhtn\",\"playerId\":\"perftest004164\",\"screenName\":\"[AP] Patricia Lewis\",\"playerGameId\":\"17c31368e87ddc1eaa7e3daf-rh2gmqqw5vlqabmg\",\"sessionId\":\"rh2gmqqw5vlqabmgr2unof7tqy2ab2wtb16c8eec\",\"casinoSessionId\":\"42f7826112d9f06e79d8892fcd20a797ffbe4f5b\",\"currency\":\"EUR\",\"bets\":[{\"code\":\"DHP_FirstHandAABonusBet\",\"stake\":1,\"payout\":0,\"placedOn\":\"2024-04-04T12:27:18.721Z\",\"transactionId\":\"712229692049312144\",\"description\":\"\"},{\"code\":\"DHP_FirstHandAnteBet\",\"stake\":1,\"payout\":0,\"placedOn\":\"2024-04-04T12:27:18.721Z\",\"transactionId\":\"712229692049312144\",\"description\":\"\"},{\"code\":\"DHP_SecondHandAABonusBet\",\"stake\":1,\"payout\":0,\"placedOn\":\"2024-04-04T12:27:18.721Z\",\"transactionId\":\"712229692049312144\",\"description\":\"\"},{\"code\":\"DHP_SecondHandAnteBet\",\"stake\":1,\"payout\":0,\"placedOn\":\"2024-04-04T12:27:18.721Z\",\"transactionId\":\"712229692049312144\",\"description\":\"\"}],\"configOverlays\":[],\"playMode\":\"RealMoney\",\"channel\":\"desktop\",\"os\":\"Windows\",\"device\":\"Desktop\",\"skinId\":\"_default_\",\"brandId\":\"1\",\"currencyRateVersion\":\"r2tgrjtbm2raaaac\",\"decisions\":[{\"recordedAt\":\"2024-04-04T12:27:32.276Z\",\"type\":\"FirstHandFold\"},{\"recordedAt\":\"2024-04-04T12:27:32.314Z\",\"type\":\"SecondHandFold\"}],\"sideBetFirstHandBonus\":{\"betCode\":\"DHP_FirstHandAABonusBet\",\"result\":\"Lose\"},\"sideBetSecondHandBonus\":{\"betCode\":\"DHP_SecondHandAABonusBet\",\"result\":\"Lose\"}},{\"casinoId\":\"0rnu7w1c8xhbuhtn\",\"playerId\":\"perftest004166\",\"screenName\":\"[AP] Gary Cox\",\"playerGameId\":\"17c31368e87ddc1eaa7e3daf-rh2gmr43mmgaabv5\",\"sessionId\":\"rh2gmr43mmgaabv5r2unogbmegtab4y7e4fc2c8f\",\"casinoSessionId\":\"9eea971882581eeb45ef3ed6060ac6852b3a6a64\",\"currency\":\"EUR\",\"bets\":[{\"code\":\"DHP_FirstHandAABonusBet\",\"stake\":1,\"payout\":0,\"placedOn\":\"2024-04-04T12:27:18.751Z\",\"transactionId\":\"712229692049314144\",\"description\":\"\"},{\"code\":\"DHP_FirstHandAnteBet\",\"stake\":1,\"payout\":0,\"placedOn\":\"2024-04-04T12:27:18.751Z\",\"transactionId\":\"712229692049314144\",\"description\":\"\"},{\"code\":\"DHP_SecondHandAABonusBet\",\"stake\":1,\"payout\":0,\"placedOn\":\"2024-04-04T12:27:18.751Z\",\"transactionId\":\"712229692049314144\",\"description\":\"\"},{\"code\":\"DHP_SecondHandAnteBet\",\"stake\":1,\"payout\":0,\"placedOn\":\"2024-04-04T12:27:18.751Z\",\"transactionId\":\"712229692049314144\",\"description\":\"\"}],\"configOverlays\":[],\"playMode\":\"RealMoney\",\"channel\":\"desktop\",\"os\":\"Windows\",\"device\":\"Desktop\",\"skinId\":\"_default_\",\"brandId\":\"1\",\"currencyRateVersion\":\"r2tgrjtbm2raaaac\",\"decisions\":[{\"recordedAt\":\"2024-04-04T12:27:27.848Z\",\"type\":\"FirstHandFold\"},{\"recordedAt\":\"2024-04-04T12:27:27.896Z\",\"type\":\"SecondHandFold\"}],\"sideBetFirstHandBonus\":{\"betCode\":\"DHP_FirstHandAABonusBet\",\"result\":\"Lose\"},\"sideBetSecondHandBonus\":{\"betCode\":\"DHP_SecondHandAABonusBet\",\"result\":\"Lose\"}},{\"casinoId\":\"0rnu7w1c8xhbuhtn\",\"playerId\":\"perftest004165\",\"screenName\":\"[AP] Karen Garcia\",\"playerGameId\":\"17c31368e87ddc1eaa7e3daf-rh2gmrjummgaabv4\",\"sessionId\":\"rh2gmrjummgaabv4r2unogbjqy2ab2wuab8b5620\",\"casinoSessionId\":\"bd675e68c92fa050647202db298008bb61bf2c3f\",\"currency\":\"EUR\",\"bets\":[{\"code\":\"DHP_FirstHandAABonusBet\",\"stake\":1,\"payout\":0,\"placedOn\":\"2024-04-04T12:27:18.751Z\",\"transactionId\":\"712229692049313144\",\"description\":\"\"},{\"code\":\"DHP_FirstHandAnteBet\",\"stake\":1,\"payout\":0,\"placedOn\":\"2024-04-04T12:27:18.751Z\",\"transactionId\":\"712229692049313144\",\"description\":\"\"},{\"code\":\"DHP_SecondHandAABonusBet\",\"stake\":1,\"payout\":0,\"placedOn\":\"2024-04-04T12:27:18.751Z\",\"transactionId\":\"712229692049313144\",\"description\":\"\"},{\"code\":\"DHP_SecondHandAnteBet\",\"stake\":1,\"payout\":0,\"placedOn\":\"2024-04-04T12:27:18.751Z\",\"transactionId\":\"712229692049313144\",\"description\":\"\"}],\"configOverlays\":[],\"playMode\":\"RealMoney\",\"channel\":\"desktop\",\"os\":\"Windows\",\"device\":\"Desktop\",\"skinId\":\"_default_\",\"brandId\":\"1\",\"currencyRateVersion\":\"r2tgrjtbm2raaaac\",\"decisions\":[{\"recordedAt\":\"2024-04-04T12:27:34.816Z\",\"type\":\"FirstHandFold\"},{\"recordedAt\":\"2024-04-04T12:27:34.843Z\",\"type\":\"SecondHandFold\"}],\"sideBetFirstHandBonus\":{\"betCode\":\"DHP_FirstHandAABonusBet\",\"result\":\"Lose\"},\"sideBetSecondHandBonus\":{\"betCode\":\"DHP_SecondHandAABonusBet\",\"result\":\"Lose\"}}],\"result\":{\"dealer\":{\"cards\":[\"JS\",\"9D\",\"8H\",\"7C\",\"4S\"],\"rank\":7274,\"type\":\"HighCard\",\"qualified\":false},\"cards\":{\"dealer\":[\"7C\",\"2D\"],\"player\":[\"AH\",\"AS\",\"KS\",\"5S\"],\"river\":[\"9D\",\"4S\"],\"flop\":[\"3C\",\"JS\",\"8H\"]},\"firstHand\":{\"player\":{\"cards\":[\"AH\",\"KS\",\"JS\",\"9D\",\"8H\"],\"rank\":6238,\"type\":\"HighCard\"},\"bonus\":{\"holdemBonus\":{\"handType\":\"FullHandBonusOutcome\",\"cards\":[\"AH\",\"KS\",\"JS\",\"8H\",\"3C\"],\"type\":\"HighCard\"}},\"outcome\":\"Player\"},\"secondHand\":{\"player\":{\"cards\":[\"AS\",\"JS\",\"9D\",\"8H\",\"5S\"],\"rank\":6500,\"type\":\"HighCard\"},\"bonus\":{\"holdemBonus\":{\"handType\":\"FullHandBonusOutcome\",\"cards\":[\"AS\",\"JS\",\"8H\",\"5S\",\"3C\"],\"type\":\"HighCard\"}},\"outcome\":\"Player\"}},\"wager\":12,\"payout\":0}";
        System.out.println("dhp");
        System.out.println(gameResultInfo(JSONObject.parseObject(dhp, Map.class)));

        String eth = "{\"gameType\":\"eth\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":0,\"startedAt\":\"2025-09-02T10:41:43.773Z\",\"result\":{\"cards\":{\"flop\":[\"JC\",\"2H\",\"9C\"],\"dealer\":[\"9D\",\"QD\"],\"river\":[\"QS\",\"7S\"],\"player\":[\"TD\",\"4D\"]},\"bonus\":{\"tripsBonus\":{\"cards\":[\"QS\",\"JC\",\"TD\",\"9C\",\"7S\"],\"type\":\"HighCard\",\"handType\":\"FullHandBonusOutcome\"}},\"dealer\":{\"qualified\":true,\"cards\":[\"QS\",\"QD\",\"9D\",\"9C\",\"JC\"],\"rank\":2745,\"type\":\"TwoPairs\"},\"outcome\":\"Dealer\",\"player\":{\"cards\":[\"QS\",\"JC\",\"TD\",\"9C\",\"7S\"],\"rank\":7008,\"type\":\"HighCard\"}},\"wager\":4188.569094,\"settledAt\":\"2025-09-02T10:42:27.881Z\",\"dealer\":{\"uid\":\"tts0rm8_________\",\"name\":\"ROB_800\"},\"currency\":\"EUR\",\"id\":\"1861711e1b596f7a5ad7ebb2\",\"table\":{\"name\":\"Extreme Texas Holdem\",\"id\":\"ETHTable00000001\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"1861711e1b596f7a5ad7ebb2-td226fkviiyqacux\",\"currencyRateVersion\":\"ted3uftaas4qaaaf\",\"os\":\"macOS\",\"channel\":\"desktop\",\"screenName\":\"1答复\",\"sessionId\":\"td226fkviiyqacuxtefadlq7yasqasvlc187baf1\",\"bets\":[{\"stake\":10000,\"placedOn\":\"2025-09-02T10:41:58.356Z\",\"code\":\"ETH_AnteBet\",\"payout\":0,\"transactionId\":\"deb0ced3-0ea2-4ab9-bb1c-7febf9c0b0c8\"},{\"stake\":10000,\"placedOn\":\"2025-09-02T10:41:58.356Z\",\"code\":\"ETH_AntePlusBet\",\"payout\":0,\"transactionId\":\"deb0ced3-0ea2-4ab9-bb1c-7febf9c0b0c8\"},{\"stake\":5000,\"placedOn\":\"2025-09-02T10:41:58.356Z\",\"code\":\"ETH_BestFiveBet\",\"payout\":0,\"transactionId\":\"deb0ced3-0ea2-4ab9-bb1c-7febf9c0b0c8\"},{\"stake\":10000,\"placedOn\":\"2025-09-02T10:42:26.002Z\",\"code\":\"ETH_RaiseX1Bet\",\"payout\":0,\"transactionId\":\"b20f20c5-7c62-4277-af02-7184d280da3a\"}],\"playMode\":\"RealMoney\",\"sideBetBestFive\":{\"betCode\":\"ETH_BestFiveBet\",\"result\":\"Lose\"},\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"3d6c9ec6c21a4e4d8d6d7a83b32bccf5\",\"currency\":\"CNY\",\"configOverlays\":[],\"decisions\":[{\"recordedAt\":\"2025-09-02T10:42:26.676Z\",\"type\":\"RaiseX1\"}],\"device\":\"Desktop\",\"playerId\":\"Utest_45189822\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"b20f20c5-7c62-4277-af02-7184d280da3a_ETH_RaiseX1Bet\"}";
        System.out.println("eth");
        System.out.println(gameResultInfo(JSONObject.parseObject(eth, Map.class)));

        String fantan = "{\"gameType\":\"fantan\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":2.303713,\"startedAt\":\"2025-09-02T09:03:14.165Z\",\"result\":{\"buttonsCount\":\"2\"},\"wager\":2.393468,\"settledAt\":\"2025-09-02T09:03:40.934Z\",\"dealer\":{\"uid\":\"tts0re4_________\",\"name\":\"ROB_508\"},\"currency\":\"EUR\",\"id\":\"18616bbe2b466397620ebbb8\",\"table\":{\"name\":\"Fan Tan\",\"id\":\"FanTan0000000001\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"18616bbe2b466397620ebbb8-td226fkviiyqacux\",\"currencyRateVersion\":\"ted3uftaas4qaaaf\",\"os\":\"macOS\",\"channel\":\"desktop\",\"screenName\":\"1答复\",\"sessionId\":\"td226fkviiyqacuxtee2u57vp4gaak5qae8bd42c\",\"bets\":[{\"stake\":5,\"placedOn\":\"2025-09-02T09:03:32.421Z\",\"code\":\"FT_1\",\"payout\":0,\"transactionId\":\"17f342a5-114a-4152-8c21-15313f43b233\"},{\"stake\":5,\"placedOn\":\"2025-09-02T09:03:32.421Z\",\"code\":\"FT_2\",\"payout\":19.25,\"transactionId\":\"17f342a5-114a-4152-8c21-15313f43b233\"},{\"stake\":5,\"placedOn\":\"2025-09-02T09:03:32.421Z\",\"code\":\"FT_Big\",\"payout\":0,\"transactionId\":\"17f342a5-114a-4152-8c21-15313f43b233\"},{\"stake\":5,\"placedOn\":\"2025-09-02T09:03:32.421Z\",\"code\":\"FT_Odd\",\"payout\":0,\"transactionId\":\"17f342a5-114a-4152-8c21-15313f43b233\"}],\"playMode\":\"RealMoney\",\"result\":{\"link\":\"/api/render/v2/html/ChBzdWJzd2luMjAwMDAwMDAxEhgxODYxNmJiZTJiNDY2Mzk3NjIwZWJiYjgaEHRkMjI2Zmt2aWl5cWFjdXgiDlV0ZXN0XzQ1MTg5ODIyKgwI7OXaxQYQgOuuvQMyCWV2b2x1dGlvbg\"},\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"4ef059c2e2724ceeafd7d39d8476186b\",\"currency\":\"CNY\",\"configOverlays\":[],\"device\":\"Desktop\",\"playerId\":\"Utest_45189822\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"17f342a5-114a-4152-8c21-15313f43b233_FT_Odd\"}";
        System.out.println("fantan");
        System.out.println(gameResultInfo(JSONObject.parseObject(fantan, Map.class)));

        String freebet = "{\"id\":\"17c25124c65fe04218953956\",\"gameProvider\":\"evolution\",\"startedAt\":\"2024-04-02T01:07:06.743Z\",\"settledAt\":\"2024-04-02T01:09:21.040Z\",\"status\":\"Resolved\",\"gameType\":\"freebet\",\"table\":{\"id\":\"rxcznn6slyyacnql\",\"name\":\"joao_free-bet_sc_bj_dnt\"},\"dealer\":{\"uid\":\"tts0ree_________\",\"name\":\"ROB_518\"},\"currency\":\"EUR\",\"participants\":[{\"casinoId\":\"0rnu7w1c8xhbuhtn\",\"playerId\":\"perftest000645\",\"screenName\":\"[AP] Edward Murphy\",\"playerGameId\":\"17c25124c65fe04218953956-mrvo2s7eyjqaacuy\",\"sessionId\":\"mrvo2s7eyjqaacuyr2nikmeot2qqhzhed423c202\",\"casinoSessionId\":\"e3f8954505b04f6998cc881a1f219eee5b70b7ea\",\"currency\":\"EUR\",\"bets\":[{\"code\":\"FBBJ_21_plus_3\",\"stake\":10,\"payout\":0,\"placedOn\":\"2024-04-02T01:07:31.763Z\",\"transactionId\":\"711108708752320720\",\"description\":\"\"},{\"code\":\"FBBJ_AnyPair\",\"stake\":10,\"payout\":0,\"placedOn\":\"2024-04-02T01:07:31.763Z\",\"transactionId\":\"711108708752320720\",\"description\":\"\"},{\"code\":\"FBBJ_BustIt\",\"stake\":10,\"payout\":20,\"placedOn\":\"2024-04-02T01:07:31.763Z\",\"transactionId\":\"711108708752320720\",\"description\":\"\"},{\"code\":\"FBBJ_Hot3\",\"stake\":10,\"payout\":0,\"placedOn\":\"2024-04-02T01:07:31.763Z\",\"transactionId\":\"711108708752320720\",\"description\":\"\"},{\"code\":\"FBBJ_Main\",\"stake\":10,\"payout\":20,\"placedOn\":\"2024-04-02T01:07:31.763Z\",\"transactionId\":\"711108708752320720\",\"description\":\"\"}],\"configOverlays\":[],\"playMode\":\"RealMoney\",\"channel\":\"desktop\",\"os\":\"Windows\",\"device\":\"Desktop\",\"skinId\":\"_default_\",\"brandId\":\"1\",\"currencyRateVersion\":\"r2lpletcs5haaaas\",\"hands\":{\"hand1\":{\"decisions\":[{\"type\":\"AutoStand\",\"recordedAt\":\"2024-04-02T01:08:14.248Z\"}],\"score\":17,\"outcome\":\"Win\",\"cards\":[\"7H\",\"TC\"],\"position\":\"Main\"}}},{\"casinoId\":\"0rnu7w1c8xhbuhtn\",\"playerId\":\"perftest000641\",\"screenName\":\"[AP] Tyler Parker\",\"playerGameId\":\"17c25124c65fe04218953956-mrvo2siayjqaacus\",\"sessionId\":\"mrvo2siayjqaacusr2nikmeot2qqhzhd01540c3a\",\"casinoSessionId\":\"26eece263ddb1d6b3c759b289fd19387dab4a7b7\",\"currency\":\"EUR\",\"bets\":[{\"code\":\"FBBJ_21_plus_3\",\"stake\":10,\"payout\":0,\"placedOn\":\"2024-04-02T01:07:31.763Z\",\"transactionId\":\"711108708752319720\",\"description\":\"\"},{\"code\":\"FBBJ_AnyPair\",\"stake\":10,\"payout\":0,\"placedOn\":\"2024-04-02T01:07:31.763Z\",\"transactionId\":\"711108708752319720\",\"description\":\"\"},{\"code\":\"FBBJ_BustIt\",\"stake\":10,\"payout\":20,\"placedOn\":\"2024-04-02T01:07:31.763Z\",\"transactionId\":\"711108708752319720\",\"description\":\"\"},{\"code\":\"FBBJ_Hot3\",\"stake\":10,\"payout\":0,\"placedOn\":\"2024-04-02T01:07:31.763Z\",\"transactionId\":\"711108708752319720\",\"description\":\"\"},{\"code\":\"FBBJ_Main\",\"stake\":10,\"payout\":20,\"placedOn\":\"2024-04-02T01:07:31.763Z\",\"transactionId\":\"711108708752319720\",\"description\":\"\"}],\"configOverlays\":[],\"playMode\":\"RealMoney\",\"channel\":\"desktop\",\"os\":\"Windows\",\"device\":\"Desktop\",\"skinId\":\"_default_\",\"brandId\":\"1\",\"currencyRateVersion\":\"r2lpletcs5haaaas\",\"hands\":{\"hand1\":{\"decisions\":[{\"type\":\"Hit\",\"recordedAt\":\"2024-04-02T01:07:57.311Z\"},{\"type\":\"AutoStand\",\"recordedAt\":\"2024-04-02T01:08:50.357Z\"}],\"score\":18,\"outcome\":\"Win\",\"cards\":[\"7H\",\"TC\",\"AH\"],\"position\":\"Main\"}}},{\"casinoId\":\"0rnu7w1c8xhbuhtn\",\"playerId\":\"perftest000751\",\"screenName\":\"[AP] Sharon Clark\",\"playerGameId\":\"17c25124c65fe04218953956-mrvo3ppyyjqaac63\",\"sessionId\":\"mrvo3ppyyjqaac63r2niqnxmt2qqhzkz67e516c9\",\"casinoSessionId\":\"87d63ea0cc3d2edcd38281aff1e9638c7bba8008\",\"currency\":\"EUR\",\"bets\":[{\"code\":\"FBBJ_Main\",\"stake\":10,\"payout\":20,\"placedOn\":\"2024-04-02T01:07:31.763Z\",\"transactionId\":\"711108708752321720\",\"description\":\"\"}],\"configOverlays\":[],\"playMode\":\"RealMoney\",\"channel\":\"desktop\",\"os\":\"Windows\",\"device\":\"Desktop\",\"skinId\":\"_default_\",\"brandId\":\"1\",\"currencyRateVersion\":\"r2lpletcs5haaaas\",\"hands\":{\"hand1\":{\"decisions\":[{\"type\":\"Stand\",\"recordedAt\":\"2024-04-02T01:08:04.011Z\"}],\"score\":17,\"outcome\":\"Win\",\"cards\":[\"7H\",\"TC\"],\"position\":\"Main\"}}}],\"result\":{\"dealtToPlayer\":[\"7H\",\"TC\",\"AH\"],\"dealerHand\":{\"score\":25,\"cards\":[\"9C\",\"6D\",\"TC\"]},\"wonSideBets\":[{\"code\":\"FBBJ_BustIt\",\"combination\":\"SBJ_BUST_IT_3CARDS\"}]},\"wager\":110,\"payout\":100}";
        System.out.println("freebet");
        System.out.println(gameResultInfo(JSONObject.parseObject(freebet, Map.class)));

        String holdem = "{\"gameType\":\"holdem\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":7180.404162,\"startedAt\":\"2025-09-02T11:00:23.199Z\",\"result\":{\"cards\":{\"flop\":[\"6C\",\"5D\",\"8D\"],\"dealer\":[\"4H\",\"JH\"],\"river\":[\"QC\",\"TH\"],\"player\":[\"6S\",\"TD\"]},\"jackpotOutcome\":{\"cards\":[\"TD\",\"TH\",\"6S\",\"6C\",\"QC\",\"8D\",\"5D\"],\"type\":\"None\"},\"bonus\":{\"holdemBonus\":{\"cards\":[\"6S\",\"6C\",\"TD\",\"8D\",\"5D\"],\"type\":\"OnePair\",\"handType\":\"FullHandBonusOutcome\"}},\"dealer\":{\"qualified\":false,\"cards\":[\"QC\",\"JH\",\"TH\",\"8D\",\"6C\"],\"rank\":7015,\"type\":\"HighCard\"},\"outcome\":\"Player\",\"player\":{\"cards\":[\"TD\",\"TH\",\"6S\",\"6C\",\"QC\"],\"rank\":2965,\"type\":\"TwoPairs\"}},\"wager\":6582.037147999999,\"settledAt\":\"2025-09-02T11:00:56.099Z\",\"dealer\":{\"uid\":\"tts0rjt_________\",\"name\":\"ROB_713\"},\"currency\":\"EUR\",\"id\":\"18617222be56a52ae9e68a52\",\"table\":{\"name\":\"Casino Hold'em\",\"id\":\"HoldemTable00001\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"18617222be56a52ae9e68a52-td226fkviiyqacux\",\"currencyRateVersion\":\"ted3uftaas4qaaaf\",\"os\":\"macOS\",\"channel\":\"desktop\",\"screenName\":\"1答复\",\"sessionId\":\"td226fkviiyqacuxtefbir4sp4gaaqca86edf15f\",\"bets\":[{\"stake\":10000,\"placedOn\":\"2025-09-02T11:00:37.692Z\",\"code\":\"HLDM_AABonusBet\",\"payout\":0,\"transactionId\":\"367c8906-4543-4949-957c-37d3c2567d95\"},{\"stake\":15000,\"placedOn\":\"2025-09-02T11:00:37.692Z\",\"code\":\"HLDM_AnteBet\",\"payout\":30000,\"transactionId\":\"367c8906-4543-4949-957c-37d3c2567d95\"},{\"stake\":30000,\"placedOn\":\"2025-09-02T11:00:53.096Z\",\"code\":\"HLDM_CallBet\",\"payout\":30000,\"transactionId\":\"e4d0cd16-8a0e-4192-a773-05acd7b8ea55\"}],\"sideBetAABonus\":{\"betCode\":\"HLDM_AABonusBet\",\"result\":\"Lose\"},\"playMode\":\"RealMoney\",\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"2f99897277f943f7a09f026033a5892d\",\"currency\":\"CNY\",\"configOverlays\":[],\"decisions\":[{\"recordedAt\":\"2025-09-02T11:00:53.737Z\",\"type\":\"Call\"}],\"device\":\"Desktop\",\"playerId\":\"Utest_45189822\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"e4d0cd16-8a0e-4192-a773-05acd7b8ea55_HLDM_CallBet\"}";
        System.out.println("holdem");
        System.out.println(gameResultInfo(JSONObject.parseObject(holdem, Map.class)));
        String monopoly = "{\"gameType\":\"monopoly\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":0.361506,\"startedAt\":\"2025-09-03T09:56:23.344Z\",\"result\":{\"outcome\":{\"wheelResult\":\"2\",\"reSpins\":[],\"type\":\"WinningNumber\",\"payoutRatio\":2}},\"wager\":0.241004,\"settledAt\":\"2025-09-03T09:56:49.560Z\",\"dealer\":{\"uid\":\"tts0r2u_________\",\"name\":\"ROB_102\"},\"currency\":\"EUR\",\"id\":\"1861bd39468361cd2f6750e6\",\"table\":{\"name\":\"Monopoly Live\",\"id\":\"Monopoly00000001\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"1861bd39468361cd2f6750e6-td226fkviiyqacux\",\"currencyRateVersion\":\"tegoa4taas4qaaag\",\"os\":\"macOS\",\"channel\":\"desktop\",\"screenName\":\"1答复\",\"sessionId\":\"td226fkviiyqacuxtehqcezp6lgabcxeaa682931\",\"bets\":[{\"stake\":1,\"placedOn\":\"2025-09-03T09:56:39.549Z\",\"code\":\"MON_1\",\"payout\":0,\"transactionId\":\"119f8053-3edb-4d49-835f-0d30bc4a746d\"},{\"stake\":1,\"placedOn\":\"2025-09-03T09:56:39.549Z\",\"code\":\"MON_2\",\"payout\":3,\"transactionId\":\"119f8053-3edb-4d49-835f-0d30bc4a746d\"}],\"playMode\":\"RealMoney\",\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"69d953eb3f8d43b29a3d1ac0aeb53efb\",\"currency\":\"CNY\",\"configOverlays\":[],\"device\":\"Desktop\",\"maxPayout\":5000000,\"playerId\":\"Utest_45189822\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"119f8053-3edb-4d49-835f-0d30bc4a746d_MON_2\"}";
        System.out.println("monopoly");
        System.out.println(gameResultInfo(JSONObject.parseObject(monopoly, Map.class)));

        String moneywheel = "{\"gameType\":\"moneywheel\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":1.023133,\"startedAt\":\"2025-08-26T03:46:18.288Z\",\"result\":{\"outcomes\":[\"01\"]},\"wager\":0.511567,\"settledAt\":\"2025-08-26T03:46:54.450Z\",\"dealer\":{\"uid\":\"tts0r2n_________\",\"name\":\"ROB_95\"},\"currency\":\"EUR\",\"id\":\"185f3462b782b416d24e15d1\",\"table\":{\"name\":\"Dream Catcher\",\"id\":\"MOWDream00000001\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"185f3462b782b416d24e15d1-tdkpjqqlodgaah24\",\"currencyRateVersion\":\"tdr23eth6thqaaab\",\"os\":\"macOS\",\"channel\":\"desktop\",\"screenName\":\"123jarvis\",\"sessionId\":\"tdkpjqqlodgaah24tdshw2jwxsmqazyx906b919d\",\"bets\":[{\"stake\":2.5,\"placedOn\":\"2025-08-26T03:46:34.433Z\",\"code\":\"MW_One\",\"payout\":5,\"transactionId\":\"cb827c22-4228-4850-a9e8-cea6e193aca7\"}],\"playMode\":\"RealMoney\",\"result\":{\"link\":\"/api/render/v2/html/ChBzdWJzd2luMjAwMDAwMDAxEhgxODVmMzQ2MmI3ODJiNDE2ZDI0ZTE1ZDEaEHRka3BqcXFsb2RnYWFoMjQiDlV0ZXN0XzY3MjE1Mjk4KgwIrty0xQYQgOnJ1gEyCWV2b2x1dGlvbg\"},\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"c36f9d63c50f49c89df224e99707cb6e\",\"currency\":\"MYR\",\"configOverlays\":[],\"device\":\"Desktop\",\"maxPayout\":2500000,\"playerId\":\"Utest_67215298\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"cb827c22-4228-4850-a9e8-cea6e193aca7_MW_One\"}";
        System.out.println("moneywheel");
        System.out.println(gameResultInfo(JSONObject.parseObject(moneywheel, Map.class)));

        String tcp = "{\"gameType\":\"tcp\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":0,\"startedAt\":\"2025-09-03T11:00:02.867Z\",\"result\":{\"cards\":{\"flop\":[],\"dealer\":[\"QH\",\"2S\",\"7D\"],\"river\":[],\"player\":[\"TH\",\"9H\",\"8H\"]},\"bonus\":{\"sixCardsBonus\":{\"cards\":[\"QH\",\"TH\",\"9H\",\"8H\",\"7D\"],\"type\":\"None\",\"handType\":\"FullHandBonusOutcome\"},\"pairPlusBonus\":{\"cards\":[\"TH\",\"9H\",\"8H\"],\"type\":\"StraightFlush\",\"handType\":\"FullHandBonusOutcome\"}},\"dealer\":{\"qualified\":true,\"cards\":[\"QH\",\"7D\",\"2S\"],\"rank\":2913,\"type\":\"HighCard\"},\"outcome\":\"Player\",\"player\":{\"cards\":[\"TH\",\"9H\",\"8H\"],\"rank\":22784,\"type\":\"StraightFlush\"}},\"wager\":4.217569,\"settledAt\":\"2025-09-03T11:00:36.733Z\",\"dealer\":{\"uid\":\"tts0rbf_________\",\"name\":\"ROB_411\"},\"currency\":\"EUR\",\"id\":\"1861c0b293c3d2db1e2cff78\",\"table\":{\"name\":\"Three Card Poker DNT\",\"id\":\"n5emwq5c5dwepwam\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"1861c0b293c3d2db1e2cff78-tdbbicegyn7aamos\",\"currencyRateVersion\":\"tegoa4taas4qaaag\",\"os\":\"macOS\",\"channel\":\"desktop\",\"screenName\":\"wade\",\"sessionId\":\"tdbbicegyn7aamostehtvgag5usaacqb5fd34ac8\",\"bets\":[{\"stake\":35,\"placedOn\":\"2025-09-03T11:00:17.289Z\",\"code\":\"TCP_AnteBet\",\"payout\":0,\"transactionId\":\"bb0038a5-d277-4896-954f-4c6cdfd2e507\"}],\"playMode\":\"RealMoney\",\"result\":{\"link\":\"/api/render/v2/html/ChBzdWJzd2luMjAwMDAwMDAxEhgxODYxYzBiMjkzYzNkMmRiMWUyY2ZmNzgaEHRkYmJpY2VneW43YWFtb3MiDlV0ZXN0XzI1MDU0NDI1KgwI1L_gxQYQwOLC3QIyCWV2b2x1dGlvbg\"},\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"5782882e672f47759fa2373966461c17\",\"currency\":\"CNY\",\"configOverlays\":[],\"decisions\":[{\"recordedAt\":\"2025-09-03T11:00:34.881Z\",\"type\":\"AutoFold\"}],\"device\":\"Desktop\",\"playerId\":\"Utest_25054425\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"bb0038a5-d277-4896-954f-4c6cdfd2e507_TCP_AnteBet\"}";
        System.out.println("tcp");
        System.out.println(gameResultInfo(JSONObject.parseObject(tcp, Map.class)));

        String rngTopcard = "{\"gameType\":\"rng-topcard\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":242.208937,\"startedAt\":\"2025-09-03T09:11:26.386Z\",\"result\":{\"bSpot\":{\"score\":7,\"card\":\"7C\"},\"aSpot\":{\"score\":6,\"card\":\"6H\"},\"outcome\":\"B\"},\"wager\":363.313404,\"settledAt\":\"2025-09-03T09:11:28.508Z\",\"dealer\":{\"uid\":\"no-dealer\",\"name\":\"No Dealer\"},\"currency\":\"EUR\",\"id\":\"1861bab6bcf21c936ae108b8\",\"table\":{\"name\":\"First Person Top Card\",\"id\":\"rng-topcard00001\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"1861bab6bcf21c936ae108b8\",\"currencyRateVersion\":\"tegoa4taas4qaaag\",\"os\":\"macOS\",\"channel\":\"desktop\",\"screenName\":\"1答复\",\"sessionId\":\"td226fkviiyqacuxtehnnsms6lgabado2afd1c84\",\"bets\":[{\"stake\":1005,\"placedOn\":\"2025-09-03T09:11:27.262Z\",\"code\":\"TC_A\",\"payout\":0,\"transactionId\":\"acd5134e-85dc-424e-8bee-48597e925947\"},{\"stake\":1005,\"placedOn\":\"2025-09-03T09:11:27.262Z\",\"code\":\"TC_B\",\"payout\":2010,\"transactionId\":\"acd5134e-85dc-424e-8bee-48597e925947\"},{\"stake\":1005,\"placedOn\":\"2025-09-03T09:11:27.262Z\",\"code\":\"TC_X\",\"payout\":0,\"transactionId\":\"acd5134e-85dc-424e-8bee-48597e925947\"}],\"playMode\":\"RealMoney\",\"result\":{\"link\":\"/api/render/v2/html/ChBzdWJzd2luMjAwMDAwMDAxEhgxODYxYmFiNmJjZjIxYzkzNmFlMTA4YjgaEHRkMjI2Zmt2aWl5cWFjdXgiDlV0ZXN0XzQ1MTg5ODIyKgwIwIzgxQYQgO6d8gEyCWV2b2x1dGlvbg\"},\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"de17724240154c4098cd6113252555dc\",\"currency\":\"CNY\",\"configOverlays\":[],\"device\":\"Desktop\",\"playerId\":\"Utest_45189822\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"acd5134e-85dc-424e-8bee-48597e925947_TC_X\"}";
        System.out.println("rngTopcard");
        System.out.println(gameResultInfo(JSONObject.parseObject(rngTopcard, Map.class)));

        String rng_craps = "{\"gameType\":\"rng-craps\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":0,\"startedAt\":\"2025-09-01T02:22:40.984Z\",\"result\":{\"rolls\":[{\"result\":{\"first\":2,\"second\":4},\"startedAt\":\"2025-09-01T02:22:40.984Z\",\"puck\":\"ComeOut\",\"rollId\":\"1861074b0068972112c3778d\"}]},\"wager\":1.19949,\"settledAt\":\"2025-09-01T02:22:51.021Z\",\"dealer\":{\"uid\":\"no-dealer\",\"name\":\"No Dealer\"},\"currency\":\"EUR\",\"id\":\"1861074fb9401763b483bdf6\",\"table\":{\"name\":\"First Person Craps\",\"id\":\"RngCraps00000001\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"1861074fb9401763b483bdf6\",\"currencyRateVersion\":\"tebjhotaas4qaaae\",\"os\":\"macOS\",\"channel\":\"desktop\",\"screenName\":\"1答复\",\"sessionId\":\"td226fkviiyqacuxtebrkg47r7eae23295234900\",\"bets\":[{\"stake\":5,\"placedOn\":\"2025-09-01T02:22:48.724Z\",\"code\":\"Craps_Crap2\",\"payout\":0,\"transactionId\":\"137acb7e-5db4-4fac-93b8-689eca1feaba\"},{\"stake\":5,\"placedOn\":\"2025-09-01T02:22:48.724Z\",\"code\":\"Craps_Field\",\"payout\":0,\"transactionId\":\"137acb7e-5db4-4fac-93b8-689eca1feaba\"}],\"playMode\":\"RealMoney\",\"result\":{\"link\":\"/api/render/v2/html/ChBzdWJzd2luMjAwMDAwMDAxEhgxODYxMDc0ZmI5NDAxNzYzYjQ4M2JkZjYaEHRkMjI2Zmt2aWl5cWFjdXgiDlV0ZXN0XzQ1MTg5ODIyKgsI-4bUxQYQwN6BCjIJZXZvbHV0aW9u\"},\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"2784fc34400345cba764b24688c5bada\",\"currency\":\"CNY\",\"configOverlays\":[],\"device\":\"Desktop\",\"playerId\":\"Utest_45189822\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"137acb7e-5db4-4fac-93b8-689eca1feaba_Craps_Field\"}";
        System.out.println("rng_craps");
        System.out.println(gameResultInfo(JSONObject.parseObject(rng_craps, Map.class)));

        String stockmarket = "{\"gameType\":\"stockmarket\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":1.103798,\"startedAt\":\"2025-09-03T08:54:29.266Z\",\"result\":{\"previousGameRoundId\":\"1861b9d32e686b4d9bb38215\",\"rngRoundingNumber\":735575,\"percentage\":-85,\"intermediatePoints\":[0,2,-2,7,6,3,1,3,9,7,8,0,-8,-14,-18,-14,-18,-23,-28,-30,-31,-48,-47,-54,-55,-66,-69,-73,-79,-89]},\"wager\":0.60251,\"settledAt\":\"2025-09-03T08:54:49.269Z\",\"dealer\":{\"uid\":\"tts0rgu_________\",\"name\":\"ROB_606\"},\"currency\":\"EUR\",\"id\":\"1861b9d834053d8fba949675\",\"table\":{\"name\":\"Stock Market\",\"id\":\"StockMarket00001\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"1861b9d834053d8fba949675-td226fkviiyqacux\",\"currencyRateVersion\":\"tegoa4taas4qaaag\",\"os\":\"macOS\",\"channel\":\"desktop\",\"index\":1,\"screenName\":\"1答复\",\"sessionId\":\"td226fkviiyqacuxtehmroshbe3qa3iu9a5a087c\",\"bets\":[{\"stake\":5,\"placedOn\":\"2025-09-03T08:54:39.768Z\",\"code\":\"SM_Down\",\"payout\":9.16,\"transactionId\":\"756455651766245709\"}],\"playMode\":\"RealMoney\",\"result\":{\"link\":\"/api/render/v2/html/ChBzdWJzd2luMjAwMDAwMDAxEhgxODYxYjlkODM0MDUzZDhmYmE5NDk2NzUaEHRkMjI2Zmt2aWl5cWFjdXgiDlV0ZXN0XzQ1MTg5ODIyKgwI2YTgxQYQwLqigAEyCWV2b2x1dGlvbg\"},\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"17229d5c315e4894949f65b7ed01c5a1\",\"currency\":\"CNY\",\"configOverlays\":[],\"commission\":0.09,\"device\":\"Desktop\",\"playerId\":\"Utest_45189822\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"756455651766245709_SM_Down\"}";
        System.out.println("stockmarket");
        System.out.println(gameResultInfo(JSONObject.parseObject(stockmarket, Map.class)));

        String topCard = "{\"gameType\":\"topcard\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":4.820078,\"startedAt\":\"2025-09-03T06:57:49.126Z\",\"result\":{\"bSpot\":{\"score\":7,\"card\":\"7H\"},\"aSpot\":{\"score\":8,\"card\":\"8D\"},\"outcome\":\"A\"},\"wager\":8.435137,\"settledAt\":\"2025-09-03T06:58:11.162Z\",\"dealer\":{\"uid\":\"tts0r38_________\",\"name\":\"ROB_116\"},\"currency\":\"EUR\",\"id\":\"1861b37aad4fe2090c8b72a8\",\"table\":{\"name\":\"Football studio\",\"id\":\"TopCard000000001\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"1861b37aad4fe2090c8b72a8-td226fkviiyqacux\",\"currencyRateVersion\":\"tegoa4taas4qaaag\",\"os\":\"macOS\",\"channel\":\"desktop\",\"screenName\":\"1答复\",\"sessionId\":\"td226fkviiyqacuxtehf2qa66lgaazaqc2699fa9\",\"bets\":[{\"stake\":20,\"placedOn\":\"2025-09-03T06:58:05.834Z\",\"code\":\"TC_A\",\"payout\":40,\"transactionId\":\"a642e37e-9463-49e7-bb00-70e39e4093e3\"},{\"stake\":20,\"placedOn\":\"2025-09-03T06:58:05.834Z\",\"code\":\"TC_B\",\"payout\":0,\"transactionId\":\"a642e37e-9463-49e7-bb00-70e39e4093e3\"},{\"stake\":30,\"placedOn\":\"2025-09-03T06:58:05.834Z\",\"code\":\"TC_X\",\"payout\":0,\"transactionId\":\"a642e37e-9463-49e7-bb00-70e39e4093e3\"}],\"playMode\":\"RealMoney\",\"result\":{\"link\":\"/api/render/v2/html/ChBzdWJzd2luMjAwMDAwMDAxEhgxODYxYjM3YWFkNGZlMjA5MGM4YjcyYTgaEHRkMjI2Zmt2aWl5cWFjdXgiDlV0ZXN0XzQ1MTg5ODIyKgsIg87fxQYQgNmfTTIJZXZvbHV0aW9u\"},\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"07eafe7e80934ea094296db9f95a01c5\",\"currency\":\"CNY\",\"configOverlays\":[],\"device\":\"Desktop\",\"playerId\":\"Utest_45189822\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"a642e37e-9463-49e7-bb00-70e39e4093e3_TC_X\"}";
        System.out.println("topCard");
        System.out.println(gameResultInfo(JSONObject.parseObject(topCard, Map.class)));

        String uth = "{\"gameType\":\"uth\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":3590.20208,\"startedAt\":\"2025-09-02T11:02:17.065Z\",\"result\":{\"cards\":{\"flop\":[\"KD\",\"7H\",\"AS\"],\"dealer\":[\"2H\",\"5H\"],\"river\":[\"7C\",\"QH\"],\"player\":[\"8D\",\"6C\"]},\"bonus\":{\"tripsBonus\":{\"cards\":[\"7C\",\"7H\",\"AS\",\"KD\",\"QH\"],\"type\":\"OnePair\",\"handType\":\"FullHandBonusOutcome\"}},\"dealer\":{\"qualified\":true,\"cards\":[\"7C\",\"7H\",\"AS\",\"KD\",\"QH\"],\"rank\":4866,\"type\":\"OnePair\"},\"outcome\":\"Push\",\"player\":{\"cards\":[\"7C\",\"7H\",\"AS\",\"KD\",\"QH\"],\"rank\":4866,\"type\":\"OnePair\"}},\"wager\":4188.569093,\"settledAt\":\"2025-09-02T11:02:48.096Z\",\"dealer\":{\"uid\":\"tts0r3y_________\",\"name\":\"ROB_142\"},\"currency\":\"EUR\",\"id\":\"1861723d4147ebc6c047d3b2\",\"table\":{\"name\":\"Ultimate Texas Hold'em\",\"id\":\"UTHTable00000001\"},\"status\":\"Resolved\",\"participants\":[{\"sideBetTrips\":{\"betCode\":\"UTH_TripsBonusBet\",\"result\":\"Lose\"},\"playerGameId\":\"1861723d4147ebc6c047d3b2-td226fkviiyqacux\",\"currencyRateVersion\":\"ted3uftaas4qaaaf\",\"os\":\"macOS\",\"channel\":\"desktop\",\"screenName\":\"1答复\",\"sessionId\":\"td226fkviiyqacuxtefbnf3lyasqatty395d1a77\",\"bets\":[{\"stake\":5000,\"placedOn\":\"2025-09-02T11:02:31.629Z\",\"code\":\"UTH_AnteBet\",\"payout\":5000,\"transactionId\":\"30cdec21-68de-4dad-a62a-9456468c7e07\"},{\"stake\":5000,\"placedOn\":\"2025-09-02T11:02:31.629Z\",\"code\":\"UTH_BlindBet\",\"payout\":5000,\"transactionId\":\"30cdec21-68de-4dad-a62a-9456468c7e07\"},{\"stake\":5000,\"placedOn\":\"2025-09-02T11:02:31.629Z\",\"code\":\"UTH_TripsBonusBet\",\"payout\":0,\"transactionId\":\"30cdec21-68de-4dad-a62a-9456468c7e07\"},{\"stake\":20000,\"placedOn\":\"2025-09-02T11:02:42.969Z\",\"code\":\"UTH_RaiseX4Bet\",\"payout\":20000,\"transactionId\":\"ff232208-a472-4299-9c40-62164513216f\"}],\"playMode\":\"RealMoney\",\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"b43a6eda049143c09271426db5222b2b\",\"currency\":\"CNY\",\"configOverlays\":[],\"decisions\":[{\"recordedAt\":\"2025-09-02T11:02:43.600Z\",\"type\":\"RaiseX4\"}],\"device\":\"Desktop\",\"playerId\":\"Utest_45189822\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"ff232208-a472-4299-9c40-62164513216f_UTH_RaiseX4Bet\"}";
        System.out.println("uth");
        System.out.println(gameResultInfo(JSONObject.parseObject(uth, Map.class)));

        String str = "{\"gameType\":\"baccarat\",\"gameSubType\":\"redenvelopev2\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":0,\"startedAt\":\"2025-09-05T07:13:08.030Z\",\"result\":{\"sideBetPerfectPair\":\"Win\",\"bankerInsuranceOutcome\":\"Lose\",\"redEnvelopePayoutsV2\":{\"BAC_PlayerPair\":15},\"sideBetPlayerPair\":\"Win\",\"sideBetPlayerBonus\":\"Lose\",\"playerInsuranceOutcome\":\"Lose\",\"sideBetBankerPair\":\"Win\",\"banker\":{\"score\":8,\"cards\":[\"4D\",\"4D\"]},\"sideBetEitherPair\":\"Win\",\"redEnvelopePayouts\":{},\"sideBetBankerBonus\":\"Win\",\"outcome\":\"Banker\",\"player\":{\"score\":0,\"cards\":[\"5S\",\"5D\"]}},\"wager\":5.076262,\"settledAt\":\"2025-09-05T07:13:34.563Z\",\"dealer\":{\"uid\":\"tts0r12_________\",\"name\":\"ROB_38\"},\"currency\":\"EUR\",\"id\":\"18625179c2e9eaecd2616c16\",\"table\":{\"name\":\"DNT Baccarat Squeeze (850)\",\"id\":\"zixzea8nrf1675oh\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"18625179c2e9eaecd2616c16-tc6nq3dlkiyaamds\",\"currencyRateVersion\":\"tels2ktg2ajaaaab\",\"os\":\"macOS\",\"channel\":\"desktop\",\"screenName\":\"6666dfhjdf\",\"sessionId\":\"tc6nq3dlkiyaamdstemlrtcbkipacnqf2f8f5c93\",\"bets\":[{\"stake\":25,\"placedOn\":\"2025-09-05T07:13:29.483Z\",\"code\":\"BAC_Player\",\"payout\":0,\"transactionId\":\"4ac6b13f-9e91-4e32-a41b-fd7e1a5dd177\"}],\"playMode\":\"RealMoney\",\"result\":{\"link\":\"/api/render/v2/html/ChBzdWJzd2luMjAwMDAwMDAxEhgxODYyNTE3OWMyZTllYWVjZDI2MTZjMTYaEHRjNm5xM2Rsa2l5YWFtZHMiDlV0ZXN0XzYwMDU4MDEwKgwInpvqxQYQwOW6jAIyCWV2b2x1dGlvbg\"},\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"7716c3f96e2343ce9f4975183dbc2934\",\"currency\":\"MYR\",\"configOverlays\":[],\"subType\":\"redenvelopev2\",\"device\":\"Desktop\",\"playerId\":\"Utest_60058010\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"4ac6b13f-9e91-4e32-a41b-fd7e1a5dd177_BAC_Player\"}";
        System.out.println("str");
        System.out.println(gameResultInfo(JSONObject.parseObject(str, Map.class)));

        //String str2 = "{\"gameType\":\"baccarat\",\"gameSubType\":\"peek\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":0,\"startedAt\":\"2025-09-05T07:46:48.201Z\",\"result\":{\"sideBetEitherPair\":\"Lose\",\"sideBetPlayerPair\":\"Lose\",\"peekCards\":[\"P1\",\"P2\",\"B1\"],\"sideBetBankerPair\":\"Lose\",\"banker\":{\"score\":5,\"cards\":[\"KC\",\"5C\"]},\"redEnvelopePayouts\":{},\"outcome\":\"Banker\",\"player\":{\"score\":3,\"cards\":[\"4D\",\"KD\",\"9D\"]}},\"wager\":1.218302,\"settledAt\":\"2025-09-05T07:48:04.357Z\",\"dealer\":{\"uid\":\"tts0reo_________\",\"name\":\"ROB_528\"},\"currency\":\"EUR\",\"id\":\"186253501e63e1f607b88337\",\"table\":{\"name\":\"Peek Baccarat\",\"id\":\"peekbaccarat0001\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"186253501e63e1f607b88337-tc6nq3dlkiyaamds\",\"currencyRateVersion\":\"tels2ktg2ajaaaab\",\"os\":\"macOS\",\"channel\":\"desktop\",\"screenName\":\"6666dfhjdf\",\"sessionId\":\"tc6nq3dlkiyaamdstemnouu75usacvnp3c504af5\",\"bets\":[{\"stake\":5,\"placedOn\":\"2025-09-05T07:47:03.045Z\",\"code\":\"BAC_Player\",\"payout\":0,\"transactionId\":\"eca09e0e-6a6d-433b-a3be-37a72fdb9be8\"},{\"stake\":1,\"placedOn\":\"2025-09-05T07:47:03.045Z\",\"code\":\"BAC_Player_Fee\",\"payout\":0,\"transactionId\":\"eca09e0e-6a6d-433b-a3be-37a72fdb9be8\"}],\"playMode\":\"RealMoney\",\"result\":{\"link\":\"/api/render/v2/html/ChBzdWJzd2luMjAwMDAwMDAxEhgxODYyNTM1MDFlNjNlMWY2MDdiODgzMzcaEHRjNm5xM2Rsa2l5YWFtZHMiDlV0ZXN0XzYwMDU4MDEwKgwItKvqxQYQwMadqgEyCWV2b2x1dGlvbg\"},\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"18704bb267174ffeb3416d3148816c1f\",\"currency\":\"MYR\",\"configOverlays\":[],\"subType\":\"peek\",\"device\":\"Desktop\",\"playerId\":\"Utest_60058010\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"eca09e0e-6a6d-433b-a3be-37a72fdb9be8_BAC_Player\"}";
        String str2 = "{\"gameType\":\"crazytime\",\"gameProvider\":\"evolution\",\"gameSubProvider\":\"evolution\",\"payout\":0,\"startedAt\":\"2025-09-06T08:13:18.475Z\",\"result\":{\"outcome\":{\"wheelResult\":{\"type\":\"WinningNumber\",\"wheelSector\":\"1\"},\"type\":\"SlotAndWheel\",\"topSlot\":{\"wheelSector\":\"10\"}}},\"wager\":0.255963,\"settledAt\":\"2025-09-06T08:13:46.967Z\",\"dealer\":{\"uid\":\"tts0r8r_________\",\"name\":\"ROB_315\"},\"currency\":\"EUR\",\"id\":\"1862a356f375f04c35423a45\",\"table\":{\"name\":\"Crazy Time\",\"id\":\"CrazyTime0000001\"},\"status\":\"Resolved\",\"participants\":[{\"playerGameId\":\"1862a356f375f04c35423a45-teo2vhvin3qaafs7\",\"currencyRateVersion\":\"teofhbta2ajaaaac\",\"os\":\"macOS\",\"channel\":\"desktop\",\"screenName\":\"evo视讯\",\"sessionId\":\"teo2vhvin3qaafs7tepbjeixkipadtmc39dbeda5\",\"bets\":[{\"stake\":0.1,\"placedOn\":\"2025-09-06T08:13:33.667Z\",\"code\":\"CT_10\",\"payout\":0,\"transactionId\":\"3836898e-b216-4242-afb6-93b27d9d9797\"},{\"stake\":0.1,\"placedOn\":\"2025-09-06T08:13:33.667Z\",\"code\":\"CT_2\",\"payout\":0,\"transactionId\":\"3836898e-b216-4242-afb6-93b27d9d9797\"},{\"stake\":0.1,\"placedOn\":\"2025-09-06T08:13:33.667Z\",\"code\":\"CT_CoinFlip\",\"payout\":0,\"transactionId\":\"3836898e-b216-4242-afb6-93b27d9d9797\"}],\"playMode\":\"RealMoney\",\"result\":{\"link\":\"/api/render/v2/html/ChBzdWJzd2luMjAwMDAwMDAxEhgxODYyYTM1NmYzNzVmMDRjMzU0MjNhNDUaEHRlbzJ2aHZpbjNxYWFmczciDlV0ZXN0XzI1ODc2ODc2KgwIutrvxQYQwP-MzQMyCWV2b2x1dGlvbg\"},\"casinoId\":\"subswin200000001\",\"casinoSessionId\":\"44e7c4f1ee17453596dbe07ff12d809f\",\"currency\":\"USD\",\"configOverlays\":[],\"device\":\"Desktop\",\"maxPayout\":500000,\"playerId\":\"Utest_25876876\",\"status\":\"Resolved\"}],\"thirdOrderId\":\"3836898e-b216-4242-afb6-93b27d9d9797_CT_2\"}";
        System.out.println("str2");
        System.out.println(gameResultInfo(JSONObject.parseObject(str2, Map.class)));
    }

    /**
     * 龙虎
     * Dragon<6> 3H;Tiger<10> 7H
     * Dragon<6> 3H; Tiger<10> 7H
     */
    public static String parseDragonTiger(Map<String, Object> result) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> dragon = (Map<String, Object>) result.get("dragon");
        if (dragon != null) {
            sb.append("Dragon<").append(dragon.get("score")).append("> ");
            appendCard(sb, dragon);
            sb.append(";");
        }
        Map<String, Object> tiger = (Map<String, Object>) result.get("tiger");
        if (tiger != null) {
            sb.append("Tiger<").append(tiger.get("score")).append("> ");
            appendCard(sb, tiger);

        }
        return sb.toString();
    }

    /**
     * seat2<29>   C2,D6
     * dealer<20> C2,D6,S2
     * lightningscalablebj todo 该解析没有对应的座位
     *
     * @param result
     * @return
     */

    public static String parseBlackjack(Map<String, Object> result) {
        StringBuilder sb = new StringBuilder();

        // ---------------- 玩家座位解析 ----------------
        //组装一行：Seat5: (18,Win) 8H TD -> Win
        Map<String, Object> seats = (Map<String, Object>) result.get("seats");
        if (ObjectUtil.isEmpty(seats)) {
            return "";
        }
        for (Map.Entry<String, Object> entry : seats.entrySet()) {
            String seatName = entry.getKey();              // 例如 "Seat5"
            Map<String, Object> seatData;
            try {
                seatData = (Map<String, Object>) entry.getValue();
            } catch (ClassCastException e) {
                continue; // 非预期结构，跳过
            }

            // 分数
            Object score = seatData.get("score"); // 可能是 Integer 或 Long
            String scoreStr = score == null ? "-" : String.valueOf(score);

            // 手牌
            //Object cardsObj = seatData.get("cards");
            sb.append(seatName)
                    .append(":<")
                    .append(scoreStr);

            // 解析 win
            String outcome = String.valueOf(seatData.getOrDefault("outcome", ""));
            if (outcome != null && !outcome.isEmpty()) {
                //sb.append(" ").append(outcome);
            }
            // 手牌
            sb.append(">");
            appendCards(sb, seatData);
            // 结果（Win/Lose/Push/Blackjack等）


            // 组装一行：Seat5: (18,Win) 8H TD -> Win

            sb.append("\n");
        }
        // dear
        // ---------------- 庄家解析 ----------------
        Map<String, Object> dealer = (Map<String, Object>) result.get("dealer");
        if (dealer != null) {
            Object score = dealer.get("score");
            String scoreStr = score == null ? "-" : String.valueOf(score);

            Object cardsObj = dealer.get("cards");
            sb.append("dealer:");
            sb.append("<").append(scoreStr);
            // 爆牌判断
            if (score instanceof Number && ((Number) score).intValue() > 21) {
                // sb.append(", Bust");
            }
            sb.append(">");
            appendCards(sb, dealer);
        }

        return sb.toString();

    }

    private static String parseBalloonRace(Map<String, Object> result) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> gameRoundResult = (Map<String, Object>) result.get("gameRoundResult");
        if (ObjectUtil.isNotEmpty(gameRoundResult)) {
            String balloonResult = gameRoundResult.get("balloonResult").toString();
            if (ObjectUtil.isNotEmpty(balloonResult)) {
                return sb.append(balloonResult).toString();
            }
        }
        return "";

    }

    /**
     * 解析 American Roulette 的 outcomes 并生成字符串描述。
     * American Roulette / First Person American Roulette Family
     * data.result.outcomes[].number/color/type
     *
     * @param result 游戏结果 Map，包含 "outcomes" 字段
     * @return 格式化后的 outcomes 字符串，例如："17 Red Odd"
     */
    private static String parseRouletteOutcomes(Map<String, Object> result) {
        StringBuilder sb = new StringBuilder();
        List<Map<String, Object>> outcomes = (List<Map<String, Object>>) result.get("outcomes");

        if (ObjectUtil.isNotEmpty(outcomes)) {
            for (Map<String, Object> outcome : outcomes) {
                String number = (String) outcome.get("number");
                String type = (String) outcome.get("type");
                String color = (String) outcome.get("color");

                if (StringUtils.isNotEmpty(number)) {
                    sb.append(number).append(" ");
                }
                if (StringUtils.isNotEmpty(color)) {
                    sb.append(color).append(" ");
                }
                if (StringUtils.isNotEmpty(type)) {
                    sb.append(type).append(" ");
                }
            }
        }

        return sb.toString().trim();
    }

    /**
     * 生成 Bac Bo 游戏结果的字符串描述。
     * 包含庄家和闲家的骰子点数信息。
     *
     * @param result 游戏结果 Map，包含 "bankerDice" 和 "playerDice" 对象
     * @return 格式化后的字符串，例如：
     * "bankerDice: (7) 5 2 playerDice: (6) 5 1"
     */
    private static String bacboResult(Map<String, Object> result) {
        StringBuilder stringBuilder = new StringBuilder();

        Map<String, Object> bankerDict = (Map<String, Object>) result.get("bankerDice");
        if (ObjectUtil.isNotEmpty(bankerDict)) {
            stringBuilder.append("bankerDice:").append(" ");
            stringBuilder.append("<").append(bankerDict.get("score")).append(">").append(" ");
            Integer first = (Integer) bankerDict.get("first");
            Integer second = (Integer) bankerDict.get("second");
            stringBuilder.append(first).append(" ").append(second).append(" ").append("\n");
        }
        Map<String, Object> playerDict = (Map<String, Object>) result.get("playerDice");
        if (ObjectUtil.isNotEmpty(playerDict)) {
            stringBuilder.append("playerDice:").append(" ");
            stringBuilder.append("<").append(playerDict.get("score")).append(">").append(" ");
            Integer first = (Integer) playerDict.get("first");
            Integer second = (Integer) playerDict.get("second");
            stringBuilder.append(first).append(" ").append(second).append(" ");
        }
        return stringBuilder.toString();
    }

    /**
     * 生成 Baccarat 游戏结果的字符串描述。
     * 包含庄家和闲家的牌面和点数信息。
     *
     * @param result 游戏结果 Map，包含 "banker" 和 "player" 对象
     *               例如：
     *               {
     *               "banker": { "score": 8, "cards": ["4D","4H"] },
     *               "player": { "score": 8, "cards": ["3S","5C"] },
     *               "outcome": "Tie"
     *               }
     * @return 格式化后的字符串，例如：
     * "banker: (8) [4D 4H] player: (8) [3S 5C] outcome: Tie"
     */
    @SuppressWarnings("unchecked")
    private static String baccaratResult(Map<String, Object> result) {
        StringBuilder stringBuilder = new StringBuilder();


        // 庄家信息
        Map<String, Object> banker = (Map<String, Object>) result.get("banker");
        if (ObjectUtil.isNotEmpty(banker)) {
            stringBuilder.append("banker: ");
            stringBuilder.append("<").append(banker.get("score")).append(">");
            appendCards(stringBuilder, banker);
            stringBuilder.append("\n");
        }

        // 闲家信息
        Map<String, Object> player = (Map<String, Object>) result.get("player");
        if (ObjectUtil.isNotEmpty(player)) {
            stringBuilder.append("player: ");
            stringBuilder.append("<").append(player.get("score")).append(">");
            appendCards(stringBuilder, player);
        }

        // 游戏结果 outcome
       /* Object outcome = result.get("outcome");
        if (outcome != null) {
            stringBuilder.append("\n").append("outcome: ").append(outcome);
        }*/

        return stringBuilder.toString().trim();
    }

    /**
     * 解析并拼接手牌信息
     * <p>
     * * 花色解析规则
     * * C： 梅花
     * * D： 方块
     * * H： 红桃
     * * S： 黑桃
     */
    private static void appendCard(StringBuilder sb, Map<String, Object> roleMap) {
        if (ObjectUtil.isNotEmpty(roleMap)) {
            //sb.append(role).append(": ");
            String card = (String) roleMap.get("card");
            if (ObjectUtil.isNotEmpty(card)) {
                String rank = card.substring(0, card.length() - 1); // 点数
                String suit = card.substring(card.length() - 1);      // 花色
                EvoGameCarSuitEnum byCode = EvoGameCarSuitEnum.byCode(suit);
                sb.append(rank).append(byCode.getDescription());

            }
            //sb.append("\n");
        }
    }

    /**
     * 解析并拼接手牌信息
     * <p>
     * * 花色解析规则
     * * C： 梅花
     * * D： 方块
     * * H： 红桃
     * * S： 黑桃
     */
    private static void appendCards(StringBuilder sb, Map<String, Object> roleMap) {
        if (ObjectUtil.isNotEmpty(roleMap)) {
            //sb.append(role).append(": ");
            List<String> cards = (List<String>) roleMap.get("cards");
            if (ObjectUtil.isNotEmpty(cards)) {
                for (String cardStr : cards) {
                    String rank = cardStr.substring(0, cardStr.length() - 1); // 点数
                    if (StringUtils.endsWithIgnoreCase("T", rank)) {
                        rank = "10";
                    }
                    String suit = cardStr.substring(cardStr.length() - 1);      // 花色
                    EvoGameCarSuitEnum byCode = EvoGameCarSuitEnum.byCode(suit);
                    sb.append(rank).append(byCode.getDescription()).append(",");
                }
                sb.deleteCharAt(sb.length() - 1);
            }
            //sb.append("\n");
        }
    }

    /**
     * 解析并拼接手牌信息,给21点游戏使用
     * <p>
     * * 花色解析规则
     * * C： 梅花
     * * D： 方块
     * * H： 红桃
     * * S： 黑桃
     */
    private static String appendCardString(List<String> cards) {
        if (ObjectUtil.isEmpty(cards)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int total = 0;
        int aceCount = 0; // 记录 A 的数量
        if (ObjectUtil.isNotEmpty(cards)) {
            for (String cardStr : cards) {
                String rank = ""; // 点数
                String suit = ""; // 花色

                // 遍历每个字符，数字拼 rank，字母拼 suit
                for (char ch : cardStr.toCharArray()) {
                    if (Character.isDigit(ch) || "TJQKA".contains(String.valueOf(ch).toUpperCase())) {
                        rank += ch;
                        if (StringUtils.endsWithIgnoreCase("T", rank)) {
                            rank = "10";
                        }
                    } else {
                        suit += ch;
                    }
                }
                // 计算单张牌的点数
                int cardValue;
                switch (rank.toUpperCase()) {
                    case "T":
                    case "J":
                    case "Q":
                    case "K":
                        cardValue = 10;
                        break;
                    case "A":
                        cardValue = 11; // 默认算 11
                        aceCount++;
                        break;
                    default:
                        cardValue = Integer.parseInt(rank);
                        break;
                }
                total += cardValue;

                EvoGameCarSuitEnum byCode = EvoGameCarSuitEnum.byCode(suit);
                sb.append(rank).append(byCode.getDescription()).append(" ");
            }
        }
        // 调整 A 的值（如果总点数 > 21，就把 A 从 11 当作 1，每次减 10）
        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
        }

        // 在最前面加总点数尖括号
        return  sb.toString().trim();

    }

    /**
     * 解析并拼接牌组信息
     * <p>
     * * 花色解析规则
     * * C： 梅花
     * * D： 方块
     * * H： 红桃
     * * S： 黑桃
     */
    private static String appendCards(List<String> cards) {
        if (ObjectUtil.isEmpty(cards)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (ObjectUtil.isNotEmpty(cards)) {
            for (String cardStr : cards) {
                String rank = ""; // 点数
                String suit = ""; // 花色

                // 遍历每个字符，数字拼 rank，字母拼 suit
                for (char ch : cardStr.toCharArray()) {
                    if (Character.isDigit(ch) || "TJQKA".contains(String.valueOf(ch).toUpperCase())) {
                        rank += ch;
                        if (StringUtils.endsWithIgnoreCase("T", rank)) {
                            rank = "10";
                        }
                    } else {
                        suit += ch;
                    }
                }
                EvoGameCarSuitEnum byCode = EvoGameCarSuitEnum.byCode(suit);
                //System.out.println(JSON.toJSONString(byCode));
                sb.append(rank).append(byCode.getDescription()).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
        }

        // 在最前面加总点数尖括号
        return sb.toString().trim();

    }

    /**
     * 计算一组牌的 Blackjack 点数
     */
    private static int calculateTotalPoints(List<String> cards) {
        int total = 0;
        int aceCount = 0;

        if (ObjectUtil.isNotEmpty(cards)) {
            for (String cardStr : cards) {
                String rank = "";
                String suit = "";

                // 拆分 rank 和 suit
                for (char ch : cardStr.toCharArray()) {
                    if (Character.isDigit(ch) || "TJQKA".contains(String.valueOf(ch).toUpperCase())) {
                        rank += ch;
                        if (StringUtils.endsWithIgnoreCase("T", rank)) {
                            rank = "10";
                        }
                    } else {
                        suit += ch;
                    }
                }

                // 计算点数
                switch (rank.toUpperCase()) {
                    case "T":
                    case "J":
                    case "Q":
                    case "K":
                        total += 10;
                        break;
                    case "A":
                        aceCount++;
                        total += 11; // 先按 11 算
                        break;
                    default:
                        total += Integer.parseInt(rank);
                        break;
                }
            }

            // 如果总点数 > 21，且有 A，把 A 当作 1（每次减 10）
            while (total > 21 && aceCount > 0) {
                total -= 10;
                aceCount--;
            }
        }

        return total;
    }


    /**
     * 投注结果
     */
    public static String betResult(final Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();
        String betResult = null;
        String status = (String) map.get("status");
        if (ObjectUtil.equal(status, EvoOrderStatusEnum.RESOLVED.getCode())) {
            String payoutTime = (String) map.get("startedAt");
            if (payoutTime != null) {
                String thirdOrderId = (String) map.get("thirdOrderId");
                Map<String, Object> betByThirdOrderId = findBetByThirdOrderId(map, thirdOrderId);
                BigDecimal betAmount = new BigDecimal(betByThirdOrderId.get("stake").toString());
                BigDecimal payout = new BigDecimal(betByThirdOrderId.get("payout").toString());
                BigDecimal winLossAmount = payout.subtract(betAmount);
                if (winLossAmount.compareTo(BigDecimal.ZERO) > 0) {
                    //betResult = "赢";
                    betResult = OrderRecordInfoTitleUtil.getWinTitle();
                } else if (winLossAmount.compareTo(BigDecimal.ZERO) == 0) {
                    //betResult = "和";
                    betResult = OrderRecordInfoTitleUtil.getTieTitle();
                } else {
                    //betResult = "输";
                    betResult = OrderRecordInfoTitleUtil.getLoseTitle();
                }
                OrderRecordInfoTitleUtil.setWinOrLoseResultTitle(stringBuilder);
                stringBuilder/*.append("输赢结果：")*/.append(betResult).append("\n");

                if (map.containsKey("result")) {
                    OrderRecordInfoTitleUtil.setResultTitle(stringBuilder);
                    //stringBuilder.append("游戏结果:").append("\n");
                    // 游戏结果
                    stringBuilder.append(EvoLanguageConversionUtils.gameResultInfo(map));
                    stringBuilder.append("\n");
                }


            }
        }
        //已结算


        return stringBuilder.toString();
    }

    /**
     *
     */
    public static Map<String, Object> getBetMap(Map<String, Object> map) {
        String thirdOrderId = (String) map.get("thirdOrderId");
        // 找到bet

        return findBetByThirdOrderId(map, thirdOrderId);
    }

    /**
     * 根据 thirdOrderId 在 map 结构的游戏记录里找到对应的 bet 信息
     *
     * @param map          整个游戏记录（JSON 解析成 Map）
     * @param thirdOrderId 唯一订单号（由 transactionId + "_" + code 拼接而成）
     * @return 匹配到的 bet（Map 格式），找不到则返回 null
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> findBetByThirdOrderId(Map<String, Object> map, String thirdOrderId) {
        // 参数判空，避免 NPE
        if (map == null || thirdOrderId == null) {
            return null;
        }

        // 获取 participants 列表（一个游戏记录里可能有多个参与者）
        List<Map<String, Object>> participants = (List<Map<String, Object>>) map.get("participants");
        if (participants == null || participants.isEmpty()) {
            return null; // 如果没有参与者，直接返回 null
        }

        // 遍历每个参与者
        for (Map<String, Object> participant : participants) {
            // 获取该参与者的 bets 列表（每个参与者可能下多个注单）
            List<Map<String, Object>> bets = (List<Map<String, Object>>) participant.get("bets");
            if (bets == null || bets.isEmpty()) {
                continue; // 如果没有下注记录，跳过
            }

            // 遍历当前参与者的每一条 bet
            for (int i = 0; i < bets.size(); i++) {
                Map<String, Object> bet = bets.get(i);
                // 获取下注的 code 和 transactionId
                String code = (String) bet.get("code");
                String transactionId = (String) bet.get("transactionId");

                // 确认字段不为空
                if (code != null && transactionId != null) {
                    // 按生成 thirdOrderId 的规则拼接
                    String combined = transactionId + "_" + i;

                    // 判断是否和传入的 thirdOrderId 匹配
                    if (ObjectUtil.equal(combined, thirdOrderId)) {
                        // 找到目标 bet，直接返回
                        return bet;
                    }
                }
            }
          /*  for (Map<String, Object> bet : bets) {

            }*/
        }

        // 遍历完没有找到，返回 null
        return null;
    }


}

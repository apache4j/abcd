package com.cloud.baowang.play.game.dg2;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.play.api.enums.dg2.DG2GameTypeEnum;
import com.cloud.baowang.play.api.vo.card.Card;
import com.cloud.baowang.play.game.dg2.enums.DG2PlayTypeEnum;
import com.cloud.baowang.play.game.dg2.enums.DG2ResultTypeEnum;
import com.cloud.baowang.play.game.dg2.enums.DG2SedieEnum;
import com.cloud.baowang.play.game.dg2.enums.SBPlayTypeEnum;
import com.cloud.baowang.play.game.sa.SAGameCarRankEnum;
import com.cloud.baowang.play.game.sa.SAGameCarSuitEnum;
import com.cloud.baowang.play.game.sh.enums.SHBetResultEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Slf4j
public class DG2OrderInfoUtil {

    public static void main(String[] args) {
//        StringBuilder stringBuilder = new StringBuilder();
//        String source = "{\"even\":500,\"fan\":{\"2\":500,\"3\":500},\"jiao\":{\"12\":500},\"sanmen\":{\"234\":632},\"nian\":{\"32\":500,\"41\":500},\"tong\":{\"134\":500,\"341\":500},\"evenW\":975.0,\"sanmenW\":{\"234\":831.712},\"nianW\":{\"41\":1450.0},\"tongW\":{\"341\":737.5,\"134\":500.0}}";
//        convertBetDetail(stringBuilder,source);
//        System.out.println("DG2OrderInfoUtil.main - "+stringBuilder);


        String resultList = " {\"banker\":[{\"pokerNumber\":\"NINE\",\"pokerPattern\":\"SPADE\"},{\"pokerNumber\":\"FIVE\",\"pokerPattern\":\"CLUB\"}],\"player\":[{\"pokerNumber\":\"EIGHT\",\"pokerPattern\":\"SPADE\"},{\"pokerNumber\":\"TWO\",\"pokerPattern\":\"HEART\"},{\"pokerNumber\":\"JACK\",\"pokerPattern\":\"HEART\"}]}";
        Map<String, List<Card>> resultMap = JSON.parseObject(resultList, new TypeReference<Map<String, List<Card>>>() {});
        StringBuilder resultBuilder = new StringBuilder();
        List<Card> banker = resultMap.getOrDefault("banker",Collections.emptyList());
        List<Card> player = resultMap.getOrDefault("player",Collections.emptyList());
        resultBuilder.append("\n");
        resultBuilder.append("[");
        //resultBuilder.append(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_BET_RESULT, SHBetResultEnum.COMM_PLAYER.getCode())).append(":");
        resultBuilder.append("banker:");
        resultBuilder.append(buildCardPower(player)).append(":");

        for (Card card : player) {
            System.out.println("DG2OrderInfoUtil.main card - "+card);
            resultBuilder.append(convertCardResult(card)).append(",");
        }
        //resultBuilder.append(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_BET_RESULT, SHBetResultEnum.COMM_BANKER.getCode())).append(":");
        resultBuilder.append("player:");
        resultBuilder.append(buildCardPower(banker)).append(":");
        for (Card card : banker) {
            resultBuilder.append(convertCardResult(card)).append(",");
        }
        resultBuilder.append("]");
        resultBuilder.append("\n");
        System.out.println("DG2OrderInfoUtil.main ---- "+resultBuilder.toString());
    }

    public static int buildCardPower(List<Card> cardList){
        int power = 0;
        for (Card card : cardList) {
            power+= Math.min(card.getPokerNumber().getPower(), 10);
        }
        return power%10;
    }

    public static String buildABResultList(String result){
        Map<String, Object> map = JSON.parseObject(result, Map.class);

        String pokerJson = String.valueOf(map.get("poker"));

        Map<String, Object> pokerMap = JSON.parseObject(pokerJson, Map.class);
        if (pokerMap == null){
            return "";
        }
        List<Card> jokerCardList = Lists.newArrayList();
        List<Card> andarCardList = Lists.newArrayList();
        List<Card> baharCardList = Lists.newArrayList();

        String joker = String.valueOf(pokerMap.get("joker"));
        String andar = String.valueOf(pokerMap.get("andar"));
        String bahar = String.valueOf(pokerMap.get("bahar"));


        List<String> andarPart = List.of(andar.split("-"));
        List<String> baharPart  = List.of(bahar.split("-"));

        jokerCardList.add(new Card().formatCard(Integer.parseInt(joker)));
        andarPart.stream().filter(StringUtils::isNotBlank).forEach(cardStr -> andarCardList.add(new Card().formatCard(Integer.parseInt(cardStr))));
        baharPart.stream().filter(StringUtils::isNotBlank).forEach(cardStr -> baharCardList.add(new Card().formatCard(Integer.parseInt(cardStr))));
        Map<String, List<Card>> cardMap = new HashMap<>();
        cardMap.put("joker", jokerCardList);
        cardMap.put("andar", andarCardList);
        cardMap.put("bahar", baharCardList);
        return JSONObject.toJSONString(cardMap);
    }


    public static String getDGResultForBetResult(String resultList, String gameCode,String language) {
        if (StringUtils.isEmpty(resultList)) {
            return "";
        }
        DG2GameTypeEnum gameTypeEnum = DG2GameTypeEnum.enumOfCode(gameCode);
        StringBuilder resultBuilder = new StringBuilder();
        switch (gameTypeEnum) {
            case BAC, BAC_2, BAC_8, BBAC:
                //"\n[闲:♠ K ,♠ K ,♦ J ,庄♠ 8 ,♥ 4 ,♠ Q ,]\n

                Map<String, List<Card>> resultMap = JSON.parseObject(resultList, new TypeReference<Map<String, List<Card>>>() {
                });

                List<Card> banker = resultMap.getOrDefault("banker",Collections.emptyList());
                List<Card> player = resultMap.getOrDefault("player",Collections.emptyList());
                resultBuilder.append("[");
                resultBuilder.append( SHBetResultEnum.COMM_PLAYER.getName()).append(":");
                resultBuilder.append(buildCardPower(player)).append(":");
                for (Card card : player) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append( SHBetResultEnum.COMM_BANKER.getName()).append(":");
                resultBuilder.append(buildCardPower(banker)).append(":");
                for (Card card : banker) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("]");
                break;
            case DTX, BDTX:
                Map<String, List<Card>> resultMapLH = JSON.parseObject(resultList, new TypeReference<Map<String, List<Card>>>() {});

                List<Card> dragon = resultMapLH.getOrDefault("dragon",Collections.emptyList());
                List<Card> tiger = resultMapLH.getOrDefault("tiger",Collections.emptyList());
                resultBuilder.append("[");
                resultBuilder.append( SHBetResultEnum.COMM_DRAGON.getName()).append(":");
                for (Card card : dragon) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append( SHBetResultEnum.COMM_TIGER.getName()).append(":");
                for (Card card : tiger) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }

                resultBuilder.append("]");
                break;

            case ROT, BROT,  FT:
                resultBuilder.append(resultList);
                break;
            case SBR, BSBR:
                if (StringUtils.isNotEmpty(resultList) && resultList.length() > 1) {
                    List<String> digits = resultList.chars()
                            .mapToObj(c -> String.valueOf((char) c))
                            .toList();

                    String joined = String.join(",", digits);
                    resultBuilder.append(joined);
                }
                break;

            case XOCDIA:
                buildSedieResult(resultBuilder,resultList);
                break;

            case ZJH, BZJH:
                Map<String, List<Card>> resultMapZJH = JSON.parseObject(resultList, new TypeReference<Map<String, List<Card>>>() {
                });

                List<Card> black = resultMapZJH.getOrDefault("black",Collections.emptyList());
                List<Card> red = resultMapZJH.getOrDefault("red",Collections.emptyList());
                resultBuilder.append("[");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "黑" : "black").append(":");
                for (Card card : black) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "红" : "red").append(":");
                for (Card card : red) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("]");
                break;

            case SG, BSG:
                Map<String, List<Card>> resultMapCow = JSON.parseObject(resultList, new TypeReference<Map<String, List<Card>>>() {});
                resultBuilder.append("[");
                List<Card> firstcard = resultMapCow.getOrDefault("firstcard",Collections.emptyList());
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "头牌" : "firstcard").append(":");
                for (Card card : firstcard) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");

                List<Card> bankerSG = resultMapCow.getOrDefault("banker",Collections.emptyList());
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "庄" : "banker").append(":");
                for (Card card : bankerSG) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");


                List<Card> player1 = resultMapCow.getOrDefault("player1",Collections.emptyList());
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "闲1" : "player1").append(":");
                for (Card card : player1) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");
                List<Card> player2 = resultMapCow.getOrDefault("player2",Collections.emptyList());
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "闲2" : "player2").append(":");
                for (Card card : player2) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");
                List<Card> player3 = resultMapCow.getOrDefault("player3",Collections.emptyList());
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "闲3" : "player3").append(":");
                for (Card card : player3) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                if (resultBuilder.length() > 1 && resultBuilder.charAt(resultBuilder.length() - 1) == ',') {
                    resultBuilder.deleteCharAt(resultBuilder.length() - 1);
                }
                resultBuilder.append("]");
                break;

            case COW, BCOW:
                //TODO
                Map<String, List<Card>> resultMapSG = JSON.parseObject(resultList, new TypeReference<Map<String, List<Card>>>() {});
                resultBuilder.append("[");
                List<Card> firstcardCow = resultMapSG.getOrDefault("firstcard",Collections.emptyList());
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "头牌" : "firstcard").append(":");
                for (Card card : firstcardCow) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");

                List<Card> bankerCow = resultMapSG.getOrDefault("banker",Collections.emptyList());
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "庄" : "banker").append(":");
                resultBuilder.append("<");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "牛" : "Bull");
                if (buildCardPower(bankerCow) == 0){
                    resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "牛" : "Bull");
                }else {
                    resultBuilder.append(buildCardPower(bankerCow));
                }
                resultBuilder.append(">");
                for (Card card : bankerCow) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");


                List<Card> player1Cow = resultMapSG.getOrDefault("player1",Collections.emptyList());
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "闲1" : "player1").append(":");
                resultBuilder.append("<");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "牛" : "Bull");
                if (buildCardPower(player1Cow) == 0){
                    resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "牛" : "Bull");
                }else {
                    resultBuilder.append(buildCardPower(player1Cow));
                }
                resultBuilder.append(">");
                for (Card card : player1Cow) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");
                List<Card> player2Cow = resultMapSG.getOrDefault("player2",Collections.emptyList());
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "闲2" : "player2").append(":");
                resultBuilder.append("<");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "牛" : "Bull");
                if (buildCardPower(player2Cow) == 0){
                    resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "牛" : "Bull");
                }else {
                    resultBuilder.append(buildCardPower(player2Cow));
                }
                resultBuilder.append(">");
                for (Card card : player2Cow) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");
                List<Card> player3Cow = resultMapSG.getOrDefault("player3",Collections.emptyList());
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "闲3" : "player3").append(":");
                resultBuilder.append("<");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "牛" : "Bull");
                if (buildCardPower(player3Cow) == 0){
                    resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "牛" : "Bull");
                }else {
                    resultBuilder.append(buildCardPower(player3Cow));
                }
                resultBuilder.append(">");
                for (Card card : player3Cow) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                if (resultBuilder.length() > 1 && resultBuilder.charAt(resultBuilder.length() - 1) == ',') {
                    resultBuilder.deleteCharAt(resultBuilder.length() - 1);
                }
                resultBuilder.append("]");
                break;

            case BJ, BBJ:
                Map<String, List<Card>> blackJackMap = JSON.parseObject(resultList, new TypeReference<Map<String, List<Card>>>() {
                });
                Map<String, List<List<Card>>> mergeList = new HashMap<>();
                for (Map.Entry<String, List<Card>> entry : blackJackMap.entrySet()) {
                    String rawKey = entry.getKey();
                    String mainKey = rawKey.split("_")[0];
                    List<Card> cardList = entry.getValue();
                    mergeList.computeIfAbsent(mainKey, k -> new ArrayList<>()).add(cardList);
                }
                resultBuilder.append("[");
                for (Map.Entry<String, List<List<Card>>> entry : mergeList.entrySet()) {
                    String pos = entry.getKey();
                    List<List<Card>> values = entry.getValue();
                    if ("banker".equals(pos)) {
                        resultBuilder.append(pos);
                    }else {
                        resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "入座第一手牌" : "first hand");
                    }
                    resultBuilder.append(":");
                    resultBuilder.append("[");

                    for (List<Card> cards : values) {
                        resultBuilder.append("[");
                        for (Card card : cards) {
                            resultBuilder.append(convertCardResult(card)).append(",");
                        }
                        resultBuilder.append("]");
                        resultBuilder.append(CommonConstant.COMMA);
                    }
                    resultBuilder.append("]");
                    resultBuilder.append("\n");
                }
                resultBuilder.append("]");
                break;
            case ANDARBAHAR, BANDARBAHAR:
                String abResult = buildABResultList(resultList);
                Map<String, List<Card>> abMap = JSON.parseObject(abResult, new TypeReference<Map<String, List<Card>>>() {});
                if (abMap  ==null) {
                    return "";
                }
                List<Card> joker = abMap.get("joker");
                List<Card> andar = abMap.get("andar");
                List<Card> bahar = abMap.get("bahar");
                for (Card card : joker) {
                    resultBuilder.append(convertCardResult(card));
                }
                resultBuilder.append("\n");
                resultBuilder.append("[");
                resultBuilder.append( SHBetResultEnum.AB_ANDAR.getName()).append(":");
                for (Card card : andar) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                if (resultBuilder.length() > 1 && resultBuilder.charAt(resultBuilder.length() - 1) == ',') {
                    resultBuilder.deleteCharAt(resultBuilder.length() - 1);
                }
                resultBuilder.append("\n");
                resultBuilder.append( SHBetResultEnum.AB_BAHAR.getName()).append(":");
                for (Card card : bahar) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                if (resultBuilder.length() > 1 && resultBuilder.charAt(resultBuilder.length() - 1) == ',') {
                    resultBuilder.deleteCharAt(resultBuilder.length() - 1);
                }
                resultBuilder.append("]");
                resultBuilder.append("\n");
                break;

            case UNKNOWN:
                break;
        }
        if (resultBuilder.length() > 1 && resultBuilder.charAt(resultBuilder.length() - 1) == ',') {
            resultBuilder.deleteCharAt(resultBuilder.length() - 1);
        }
        return resultBuilder.toString();
    }



    public static String getDGResultList(String resultList, String gameCode,String language) {

        if (StringUtils.isEmpty(resultList)) {
            return "";
        }
        DG2GameTypeEnum gameTypeEnum = DG2GameTypeEnum.enumOfCode(gameCode);
        StringBuilder resultBuilder = new StringBuilder();
        switch (gameTypeEnum) {
            case BAC, BAC_2, BAC_8, BBAC:
                //"\n[闲:♠ K ,♠ K ,♦ J ,庄♠ 8 ,♥ 4 ,♠ Q ,]\n

                Map<String, List<Card>> resultMap = JSON.parseObject(resultList, new TypeReference<Map<String, List<Card>>>() {
                });
                if (resultMap== null){
                    return "";
                }
                List<Card> banker = resultMap.get("banker");
                List<Card> player = resultMap.get("player");
                resultBuilder.append("[");
                resultBuilder.append(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_BET_RESULT, SHBetResultEnum.COMM_PLAYER.getCode())).append(":");
                resultBuilder.append(buildCardPower(player)).append(":");
                for (Card card : player) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_BET_RESULT, SHBetResultEnum.COMM_BANKER.getCode())).append(":");
                resultBuilder.append(buildCardPower(banker)).append(":");
                for (Card card : banker) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("]");
                break;
            case DTX, BDTX:
                Map<String, List<Card>> resultMapLH = JSON.parseObject(resultList, new TypeReference<Map<String, List<Card>>>() {});
                if (resultMapLH== null){
                    return "";
                }
                List<Card> dragon = resultMapLH.get("dragon");
                List<Card> tiger = resultMapLH.get("tiger");
                resultBuilder.append("[");
                resultBuilder.append(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_BET_RESULT, SHBetResultEnum.COMM_DRAGON.getCode())).append(":");
                for (Card card : dragon) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_BET_RESULT, SHBetResultEnum.COMM_TIGER.getCode())).append(":");
                for (Card card : tiger) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }

                resultBuilder.append("]");
                break;

            case ROT, BROT,  FT:
                resultBuilder.append(resultList);
                break;
            case SBR, BSBR:
                if (StringUtils.isNotEmpty(resultList) && resultList.length() > 1) {
                    List<String> digits = resultList.chars()
                            .mapToObj(c -> String.valueOf((char) c))
                            .toList();

                    String joined = String.join(",", digits);
                    resultBuilder.append(joined);
                }
                break;

            case XOCDIA:
                buildSedieResult(resultBuilder,resultList);
                break;

            case ZJH, BZJH:
                Map<String, List<Card>> resultMapZJH = JSON.parseObject(resultList, new TypeReference<Map<String, List<Card>>>() {
                });
                if (resultMapZJH== null){
                    return "";
                }
                List<Card> black = resultMapZJH.get("black");
                List<Card> red = resultMapZJH.get("red");
                resultBuilder.append("[");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "黑" : "black").append(":");
                for (Card card : black) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "红" : "red").append(":");
                for (Card card : red) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("]");
                break;

            case SG, BSG:
                Map<String, List<Card>> resultMapCow = JSON.parseObject(resultList, new TypeReference<Map<String, List<Card>>>() {});
                if (resultMapCow== null){
                    return "";
                }
                resultBuilder.append("[");
                List<Card> firstcard = resultMapCow.get("firstcard");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "头牌" : "firstcard").append(":");
                for (Card card : firstcard) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");

                List<Card> bankerSG = resultMapCow.getOrDefault("banker", Collections.emptyList());
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "庄" : "banker").append(":");
                for (Card card : bankerSG) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");


                List<Card> player1 = resultMapCow.getOrDefault("player1", Collections.emptyList());
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "闲1" : "player1").append(":");
                for (Card card : player1) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");
                List<Card> player2 = resultMapCow.getOrDefault("player2", Collections.emptyList());
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "闲2" : "player2").append(":");
                for (Card card : player2) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");
                List<Card> player3 = resultMapCow.getOrDefault("player3", Collections.emptyList());
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "闲3" : "player3").append(":");
                for (Card card : player3) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                if (resultBuilder.length() > 1 && resultBuilder.charAt(resultBuilder.length() - 1) == ',') {
                    resultBuilder.deleteCharAt(resultBuilder.length() - 1);
                }
                resultBuilder.append("]");
                break;

            case COW, BCOW:
                //TODO
                Map<String, List<Card>> resultMapSG = JSON.parseObject(resultList, new TypeReference<Map<String, List<Card>>>() {});
                if (resultMapSG== null){
                    return "";
                }
                resultBuilder.append("[");
                List<Card> firstcardCow = resultMapSG.get("firstcard");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "头牌" : "firstcard").append(":");
                for (Card card : firstcardCow) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");

                List<Card> bankerCow = resultMapSG.getOrDefault("banker", Collections.emptyList());
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "庄" : "banker").append(":");
                resultBuilder.append("<");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "牛" : "Bull");
                if (buildCardPower(bankerCow) == 0){
                   resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "牛" : "Bull");
                }else {
                   resultBuilder.append(buildCardPower(bankerCow));
                }
                resultBuilder.append(">");
                for (Card card : bankerCow) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");


                List<Card> player1Cow = resultMapSG.getOrDefault("player1", Collections.emptyList());
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "闲1" : "player1").append(":");
                resultBuilder.append("<");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "牛" : "Bull");
                if (buildCardPower(player1Cow) == 0){
                    resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "牛" : "Bull");
                }else {
                    resultBuilder.append(buildCardPower(player1Cow));
                }
                resultBuilder.append(">");
                for (Card card : player1Cow) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");
                List<Card> player2Cow = resultMapSG.getOrDefault("player2", Collections.emptyList());
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "闲2" : "player2").append(":");
                resultBuilder.append("<");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "牛" : "Bull");
                if (buildCardPower(player2Cow) == 0){
                    resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "牛" : "Bull");
                }else {
                    resultBuilder.append(buildCardPower(player2Cow));
                }
                resultBuilder.append(">");
                for (Card card : player2Cow) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");
                List<Card> player3Cow = resultMapSG.getOrDefault("player3", Collections.emptyList());
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "闲3" : "player3").append(":");
                resultBuilder.append("<");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "牛" : "Bull");
                if (buildCardPower(player3Cow) == 0){
                    resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "牛" : "Bull");
                }else {
                    resultBuilder.append(buildCardPower(player3Cow));
                }
                resultBuilder.append(">");
                for (Card card : player3Cow) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                if (resultBuilder.length() > 1 && resultBuilder.charAt(resultBuilder.length() - 1) == ',') {
                    resultBuilder.deleteCharAt(resultBuilder.length() - 1);
                }
                resultBuilder.append("]");
                break;

            case BJ, BBJ:
                Map<String, List<Card>> blackJackMap = JSON.parseObject(resultList, new TypeReference<Map<String, List<Card>>>() {
                });
                if (blackJackMap== null){
                    return "";
                }
                Map<String, List<List<Card>>> mergeList = new HashMap<>();
                for (Map.Entry<String, List<Card>> entry : blackJackMap.entrySet()) {
                    String rawKey = entry.getKey();
                    String mainKey = rawKey.split("_")[0];
                    List<Card> cardList = entry.getValue();
                    mergeList.computeIfAbsent(mainKey, k -> new ArrayList<>()).add(cardList);
                }
                resultBuilder.append("[");
                for (Map.Entry<String, List<List<Card>>> entry : mergeList.entrySet()) {
                    String pos = entry.getKey();
                    List<List<Card>> values = entry.getValue();
                    if ("banker".equals(pos)) {
                        resultBuilder.append(pos);
                    }else {
                        resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "入座第一手牌" : "first hand");
                    }
                    resultBuilder.append(":");
                    resultBuilder.append("[");

                    for (List<Card> cards : values) {
                        resultBuilder.append("[");
                        for (Card card : cards) {
                            resultBuilder.append(convertCardResult(card)).append(",");
                        }
                        resultBuilder.append("]");
                        resultBuilder.append(CommonConstant.COMMA);
                    }
                    resultBuilder.append("]");
                    resultBuilder.append("\n");
                }
                resultBuilder.append("]");
                break;
            case ANDARBAHAR, BANDARBAHAR:
                String abResult = buildABResultList(resultList);
                Map<String, List<Card>> abMap = JSON.parseObject(abResult, new TypeReference<Map<String, List<Card>>>() {
                });
                List<Card> joker = abMap.getOrDefault("joker", Collections.emptyList());
                List<Card> andar = abMap.getOrDefault("andar", Collections.emptyList());
                List<Card> bahar = abMap.getOrDefault("bahar", Collections.emptyList());
                for (Card card : joker) {
                    resultBuilder.append(convertCardResult(card));
                }
                resultBuilder.append("\n");
                resultBuilder.append("[");
                resultBuilder.append(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_BET_RESULT, SHBetResultEnum.AB_ANDAR.getCode())).append(":");
                for (Card card : andar) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                if (resultBuilder.length() > 1 && resultBuilder.charAt(resultBuilder.length() - 1) == ',') {
                    resultBuilder.deleteCharAt(resultBuilder.length() - 1);
                }
                resultBuilder.append("\n");
                resultBuilder.append(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_BET_RESULT, SHBetResultEnum.AB_BAHAR.getCode())).append(":");
                for (Card card : bahar) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                if (resultBuilder.length() > 1 && resultBuilder.charAt(resultBuilder.length() - 1) == ',') {
                    resultBuilder.deleteCharAt(resultBuilder.length() - 1);
                }
                resultBuilder.append("]");
                resultBuilder.append("\n");
                break;

            case UNKNOWN:
                break;
        }
        if (resultBuilder.length() > 1 && resultBuilder.charAt(resultBuilder.length() - 1) == ',') {
            resultBuilder.deleteCharAt(resultBuilder.length() - 1);
        }
        return resultBuilder.toString();
    }

    private static String convertCardResult(Card card) {
        if (card == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        SAGameCarSuitEnum carSuitEnum = SAGameCarSuitEnum.byCode(card.getPokerPattern().getCode());
        if (carSuitEnum != null) {
            stringBuilder.append(carSuitEnum.getIcon()).append(" ");
        }

        SAGameCarRankEnum bankRank = SAGameCarRankEnum.byCode(card.getPokerNumber().getPower());
        if (bankRank != null) {
            stringBuilder.append(bankRank.getDescription()).append(" ");
        }
        return stringBuilder.toString();
    }

    private static void buildBJLPlayTypeStr(StringBuilder stringBuilder, String type) {
        String gameTypeId = I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_PLAY_TYPE, type);
        stringBuilder.append(gameTypeId).append(CommonConstant.COMMA);
    }

    public static String buildBetTypeStr(String playType, String thirdGameCode,String playInfo) {
        StringBuilder stringBuilder = new StringBuilder();
        switch (thirdGameCode) {
            case "1":
            case "2":
            case "8":
            case "41":  // 区块链百家乐
            case "3":
            case "42":  // 龙虎类
                buildBJLPlayTypeStr(stringBuilder, playType);
                break;

            case "4":
            case "47": //,轮盘 ,番摊,色碟
            case "6":
                convertBetDetail(stringBuilder, playInfo);
                break;
            case "5":
            case "48":  // 骰宝类
                convertSBBetDetail(stringBuilder, playInfo);

                break;
            case "11":  // 炸金花
            case "43":  // 区块链炸金花

            case "7":   // 斗牛
            case "44":  // 区块链牛牛
            case "16":  // 三公
            case "45":  // 区块链三公
            case "20":  // 安达巴哈
            case "46":  // 区块链安达巴哈

                buildNoI18PlayType(stringBuilder,playType);
                break;
            case "14": //色碟
                buildSediePlayType(stringBuilder,playType);
                break;
            case "21"://21点
            case "53":
                buildBlackJackPlayType(stringBuilder,playInfo);
                break;

            default:
                break;


        }
        return stringBuilder.toString();
    }

    private static void buildBlackJackPlayType(StringBuilder stringBuilder, String playInfo) {
        Map<String, Object> map = JSON.parseObject(playInfo, new TypeReference<Map<String, Object>>() {});
        if (map==null){
            return;
        }
        int size = map.size();
        int index = 0;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            if (DG2PlayTypeEnum.getEnum(key) == null){
                continue;
            }
            Object value = entry.getValue();
            index++;
            buildNoI18PlayType(stringBuilder, key);
            if (DG2PlayTypeEnum.SEAT_SEAT.getCode().equals(key)) {
                stringBuilder.append("<").append(value).append(">");
            } else if (DG2PlayTypeEnum.SEAT_BASE.getCode().equals(key)) {
                boolean isSeat = Boolean.parseBoolean(value.toString());
                stringBuilder.append("<");
                if (isSeat) {
                    // 入座
                    buildNoI18PlayType(stringBuilder, DG2PlayTypeEnum.SEAT_BASE.getCode());
                } else {
                    // 旁注
                    buildNoI18PlayType(stringBuilder, DG2PlayTypeEnum.SEAT_BASE_SIDE.getCode());
                }
                stringBuilder.append(">");
            } else {
                stringBuilder.append("<").append(value).append(">");
            }

            if (index < size) {
                stringBuilder.append(CommonConstant.COMMA);
            }
        }
    }


    private static void convertSBBetDetail(StringBuilder stringBuilder, String playType) {
        log.info( " convertSBBetDetail - "+ playType  );

        Map<String, Object> map ;
        try {
            map = JSON.parseObject(playType, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return;
        }
        if (map == null){
            return;
        }
        int size = map.size();
        int index = 0;

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            if (DG2PlayTypeEnum.getEnum(key) == null){
                continue;
            }
            buildSBNoI18PlayType(stringBuilder, key);

            Object value = entry.getValue();
            if (value instanceof Map) {
                stringBuilder.append("<");
                Map<String, Object> innerMap = (Map<String, Object>) value;
                for (Map.Entry<String, Object> inner : innerMap.entrySet()) {
                    stringBuilder.append(inner.getKey()).append(CommonConstant.COMMA);
                }
                if (stringBuilder.length() > 1 && stringBuilder.charAt(stringBuilder.length() - 1) == ',') {
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }
                stringBuilder.append(">");
            }
            stringBuilder.append(CommonConstant.COMMA);
            if (stringBuilder.length() > 1 && stringBuilder.charAt(stringBuilder.length() - 1) == ',') {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
            index++;
            if (index < size) {
                stringBuilder.append("\n");
            }
        }
    }

    private static void buildSBNoI18PlayType(StringBuilder stringBuilder, String playType) {
        String[] playTypes = playType.split(CommonConstant.COMMA);
        int size = playTypes.length;
        int index = 0;
        for (String type : playTypes) {
            if (LanguageEnum.ZH_CN.getLang().equals(CurrReqUtils.getLanguage())) {
                SBPlayTypeEnum.fromKey(type).ifPresent(SBPlayTypeEnum -> stringBuilder.append(SBPlayTypeEnum.getDesc()));
            } else {
                SBPlayTypeEnum.fromKey(type).ifPresent(SBPlayTypeEnum -> stringBuilder.append(SBPlayTypeEnum.getShCode()));
            }
            index++;
            if (index < size) {
                stringBuilder.append(CommonConstant.COMMA);
            }
        }
    }

    private static void convertBetDetail(StringBuilder stringBuilder, String playType) {
        log.info( " playType - "+ playType  );

        Map<String, Object> map ;
        try {
            map = JSON.parseObject(playType, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return;
        }
        if (map == null){
            return;
        }
        int size = map.size();
        int index = 0;

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            if (DG2PlayTypeEnum.getEnum(key) == null){
                continue;
            }
            buildNoI18PlayType(stringBuilder, key);

            Object value = entry.getValue();
            if (value instanceof Map) {
                stringBuilder.append("<");
                Map<String, Object> innerMap = (Map<String, Object>) value;
                for (Map.Entry<String, Object> inner : innerMap.entrySet()) {
                    stringBuilder.append(inner.getKey()).append(CommonConstant.COMMA);
                }
                if (stringBuilder.length() > 1 && stringBuilder.charAt(stringBuilder.length() - 1) == ',') {
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }
                stringBuilder.append(">");
            }
            stringBuilder.append(CommonConstant.COMMA);
            if (stringBuilder.length() > 1 && stringBuilder.charAt(stringBuilder.length() - 1) == ',') {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
            index++;
            if (index < size) {
                stringBuilder.append("\n");
            }
        }
    }

    private static void buildSediePlayType(StringBuilder stringBuilder, String playType) {
        String[] playTypes = playType.split(CommonConstant.COMMA);
        int size = playTypes.length;
        int index = 0;
        for (String type : playTypes) {
            if (LanguageEnum.ZH_CN.getLang().equals(CurrReqUtils.getLanguage())) {
                DG2SedieEnum.fromKey(type).ifPresent(DG2SedieEnum -> stringBuilder.append(DG2SedieEnum.getDesc()));
            } else {
                DG2SedieEnum.fromKey(type).ifPresent(DG2SedieEnum -> stringBuilder.append(DG2SedieEnum.getShCode()));
            }
            index++;
            if (index < size) {
                stringBuilder.append(CommonConstant.COMMA);
            }
        }

    }


    private static void buildSedieResult(StringBuilder stringBuilder, String result) {
        Optional<DG2ResultTypeEnum> sedieResultEnum = DG2ResultTypeEnum.fromCode(result);
        sedieResultEnum.ifPresent(DG2ResultTypeEnum ->
                stringBuilder.append(LanguageEnum.ZH_CN.getLang().equals(CurrReqUtils.getLanguage())?DG2ResultTypeEnum.getZh_CN():DG2ResultTypeEnum.getEn_US()));


    }

    private static void buildNoI18PlayType(StringBuilder stringBuilder, String playType) {
        String[] playTypes = playType.split(CommonConstant.COMMA);
        int size = playTypes.length;
        int index = 0;
        for (String type : playTypes) {
           if (LanguageEnum.ZH_CN.getLang().equals(CurrReqUtils.getLanguage())) {
               DG2PlayTypeEnum.fromKey(type).ifPresent(DG2PlayTypeEnum -> stringBuilder.append(DG2PlayTypeEnum.getDesc()));
           } else {
               DG2PlayTypeEnum.fromKey(type).ifPresent(DG2PlayTypeEnum -> stringBuilder.append(DG2PlayTypeEnum.getShCode()));
           }
            index++;
            if (index < size) {
                stringBuilder.append(CommonConstant.COMMA);
            }
       }

    }

}






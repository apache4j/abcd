package com.cloud.baowang.play.game.db.sh;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.play.api.vo.card.Card;
import com.cloud.baowang.play.game.db.sh.enums.DBSHGameTypeEnum;
import com.cloud.baowang.play.game.db.sh.enums.DBSHPlayTypeEnum;
import com.cloud.baowang.play.game.sa.SAGameCarRankEnum;
import com.cloud.baowang.play.game.sa.SAGameCarSuitEnum;
import com.cloud.baowang.play.game.sh.enums.SHBetResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

@Slf4j
public class DBSHOrderInfoUtil {

    public static int getCardPower(List<Card> cardList){
        int power = 0;
        for (Card card : cardList) {
            power+= Math.min(card.getPokerNumber().getPower(), 10);
        }
        return power%10;
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


    public static String getResultInfo(String resultList, String gameCode,String language) {
        if (StringUtils.isEmpty(resultList)) {
            return "";
        }
        DBSHGameTypeEnum gameTypeEnum = DBSHGameTypeEnum.fromCode(gameCode);
        StringBuilder resultBuilder = new StringBuilder();
        switch (gameTypeEnum) {
            case GAME_2001:
            case GAME_2002:
            case GAME_2003:
            case GAME_2004:
            case GAME_2005:
            case GAME_2014:  //百家乐
            case GAME_2027:
            case GAME_2030:
            case GAME_2034:
            case GAME_2038:
            case GAME_2016://保险百家乐
                //"\n[闲:♠ K ,♠ K ,♦ J ,庄♠ 8 ,♥ 4 ,♠ Q ,]\n
                if (gameTypeEnum.equals(DBSHGameTypeEnum.GAME_2016)){
                    resultList = resultList.split("\\|")[0];
                }
                if (gameTypeEnum.equals(DBSHGameTypeEnum.GAME_2034)){
                    resultList = resultList.split("&")[0];
                }
                Map<String, List<Card>> resultMap = convertToCard(resultList,CommonConstant.SEMICOLON,"banker", "player");
                if (resultMap.isEmpty()){
                    break;
                }
                List<Card> banker = resultMap.get("banker");
                List<Card> player = resultMap.get("player");
                resultBuilder.append("[");
                resultBuilder.append(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_BET_RESULT, SHBetResultEnum.COMM_PLAYER.getCode())).append(":");
                resultBuilder.append(getCardPower(player)).append(":");
                for (Card card : player) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_BET_RESULT, SHBetResultEnum.COMM_BANKER.getCode())).append(":");
                resultBuilder.append(getCardPower(banker)).append(":");
                for (Card card : banker) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("]");
                break;


            case GAME_2006://龙虎
                Map<String, List<Card>> resultMapLH = convertToCard(resultList,CommonConstant.SEMICOLON,"dragon", "tiger");
                if (resultMapLH.isEmpty()){
                    break;
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
                if (resultBuilder.length() > 1 && resultBuilder.charAt(resultBuilder.length() - 1) == ',') {
                    resultBuilder.deleteCharAt(resultBuilder.length() - 1);
                }

                resultBuilder.append("]");
                break;

            case GAME_2009://牛牛
                //TODO
                Map<String, List<Card>> resultMapSG = convertToCard(resultList,CommonConstant.SEMICOLON,"firstcard", "banker", "player1", "player2", "player3");
                if (resultMapSG.isEmpty()){
                    break;
                }
                resultBuilder.append("[");
                List<Card> firstcardCow = resultMapSG.get("firstcard");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "头牌" : "firstcard").append(":");
                for (Card card : firstcardCow) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");

                List<Card> bankerCow = resultMapSG.get("banker");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "庄" : "banker").append(":");
                resultBuilder.append("<");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "牛" : "Bull");
                if (getCardPower(bankerCow) == 0){
                    resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "牛" : "Bull");
                }else {
                    resultBuilder.append(getCardPower(bankerCow));
                }
                resultBuilder.append(">");
                for (Card card : bankerCow) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");


                List<Card> player1Cow = resultMapSG.get("player1");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "闲1" : "player1").append(":");
                resultBuilder.append("<");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "牛" : "Bull");
                if (getCardPower(player1Cow) == 0){
                    resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "牛" : "Bull");
                }else {
                    resultBuilder.append(getCardPower(player1Cow));
                }
                resultBuilder.append(">");
                for (Card card : player1Cow) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");
                List<Card> player2Cow = resultMapSG.get("player2");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "闲2" : "player2").append(":");
                resultBuilder.append("<");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "牛" : "Bull");
                if (getCardPower(player2Cow) == 0){
                    resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "牛" : "Bull");
                }else {
                    resultBuilder.append(getCardPower(player2Cow));
                }
                resultBuilder.append(">");
                for (Card card : player2Cow) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");
                List<Card> player3Cow = resultMapSG.get("player3");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "闲3" : "player3").append(":");
                resultBuilder.append("<");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "牛" : "Bull");
                if (getCardPower(player3Cow) == 0){
                    resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "牛" : "Bull");
                }else {
                    resultBuilder.append(getCardPower(player3Cow));
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

            case GAME_2011:
                Map<String, List<Card>> resultMapCow = convertToCard(resultList,CommonConstant.SEMICOLON,"firstcard", "banker", "player1", "player2", "player3");
                if (resultMapCow.isEmpty()){
                    break;
                }
                resultBuilder.append("[");
                List<Card> firstcard = resultMapCow.get("firstcard");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "头牌" : "firstcard").append(":");
                for (Card card : firstcard) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");

                List<Card> bankerSG = resultMapCow.get("banker");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "庄" : "banker").append(":");
                for (Card card : bankerSG) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");


                List<Card> player1 = resultMapCow.get("player1");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "闲1" : "player1").append(":");
                for (Card card : player1) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");
                List<Card> player2 = resultMapCow.get("player2");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "闲2" : "player2").append(":");
                for (Card card : player2) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");
                List<Card> player3 = resultMapCow.get("player3");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "闲3" : "player3").append(":");
                for (Card card : player3) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                if (resultBuilder.length() > 1 && resultBuilder.charAt(resultBuilder.length() - 1) == ',') {
                    resultBuilder.deleteCharAt(resultBuilder.length() - 1);
                }
                resultBuilder.append("]");
                break;

            case GAME_2007,GAME_2008,GAME_2023,GAME_2022,GAME_2029,GAME_2031,GAME_2032:
                resultBuilder.append(resultList);
                break;

            case GAME_2020://番摊
                buildFantanResultInfo(resultBuilder,resultList);
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "番" : "fan");
                break;

            case GAME_2021:
                Map<String, List<Card>> blackJackMap = convertToCard(resultList,CommonConstant.SEMICOLON,"banker","player");
                if (blackJackMap.isEmpty()){
                    break;
                }
                List<Card> bankerJ = blackJackMap.get("banker");
                List<Card> playerJ = blackJackMap.get("player");
                resultBuilder.append("[");
                resultBuilder.append(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_BET_RESULT, SHBetResultEnum.COMM_BANKER.getCode())).append(":");
                for (Card card : bankerJ) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_BET_RESULT, SHBetResultEnum.COMM_PLAYER.getCode())).append(":");
                for (Card card : playerJ) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }

                resultBuilder.append("]");
                break;

            case GAME_2010, GAME_2026:
                Map<String, List<Card>> resultMapZJh = convertToCard(resultList,CommonConstant.SEMICOLON,"banker", "player");
                if (resultMapZJh.isEmpty()){
                    break;
                }
                List<Card> bankerZJH = resultMapZJh.get("banker");
                List<Card> playerZJh = resultMapZJh.get("player");
                resultBuilder.append("[");
                resultBuilder.append(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_BET_RESULT, SHBetResultEnum.COMM_PLAYER.getCode())).append(":");
                resultBuilder.append(getCardPower(playerZJh)).append(":");
                for (Card card : playerZJh) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_BET_RESULT, SHBetResultEnum.COMM_BANKER.getCode())).append(":");
                resultBuilder.append(getCardPower(bankerZJH)).append(":");
                for (Card card : bankerZJH) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("]");
                break;


            case GAME_2015:
                Map<String, List<Card>> resultMapBull = convertToCard(resultList,CommonConstant.SEMICOLON,"black", "red");
                if (resultMapBull.isEmpty()){
                    break;
                }
                List<Card> black = resultMapBull.get("black");
                List<Card> red = resultMapBull.get("red");
                resultBuilder.append("[");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "红牛" : "redBull").append(":");
                resultBuilder.append(getCardPower(red)).append(":");
                for (Card card : red) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "黑牛" : "blackBull").append(":");
                resultBuilder.append(getCardPower(black)).append(":");
                for (Card card : black) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("]");
                break;

            case GAME_2019:
                Map<String, List<Card>> resultPoker = convertToCard(resultList,CommonConstant.SEMICOLON,"banker", "player","public1","public2","public3");
                if (resultPoker.isEmpty()){
                    break;
                }
                resultBuilder.append("[");
                List<Card> cardB = resultPoker.get("banker");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "庄" : "banker").append(":");
                for (Card card : cardB) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");

                List<Card> cardP = resultPoker.get("player");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "闲" : "player").append(":");
                for (Card card : cardP) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");


                List<Card> public1 = resultPoker.get("public1");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "公区1" : "player1").append(":");
                for (Card card : public1) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");
                List<Card> public2 = resultPoker.get("public2");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "公区2" : "player2").append(":");
                for (Card card : public2) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("\n");
                List<Card> public3 = resultPoker.get("public3");
                resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? "公区3" : "player3").append(":");
                for (Card card : public3) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                if (resultBuilder.length() > 1 && resultBuilder.charAt(resultBuilder.length() - 1) == ',') {
                    resultBuilder.deleteCharAt(resultBuilder.length() - 1);
                }
                resultBuilder.append("]");
                break;

            case GAME_2025:
                Map<String, List<Card>> abMap = convertToCard(resultList,CommonConstant.SEMICOLON,"joker","andar", "bahar");
                if (abMap.isEmpty()){
                    break;
                }
                List<Card> joker = abMap.get("joker");
                List<Card> andar = abMap.get("andar");
                List<Card> bahar = abMap.get("bahar");
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

    private static void buildFantanResultInfo(StringBuilder resultBuilder, String resultList) {
        String[] split = resultList.split(CommonConstant.COLON);
        if (split.length != 3){
            return;
        }
        int sum = Integer.parseInt(split[0]) + Integer.parseInt(split[1]) + Integer.parseInt(split[2]);
        int mod = sum % 4;
        resultBuilder.append(mod == 0 ? 4 : mod);
    }


    public static String getBetTypeStr(String playType ) {
        StringBuilder stringBuilder = new StringBuilder();
        DBSHPlayTypeEnum gameTypeEnum = DBSHPlayTypeEnum.fromCode(playType);
        String language = CurrReqUtils.getLanguage();
        stringBuilder.append(LanguageEnum.ZH_CN.getLang().equals(language) ? gameTypeEnum.getZh_cn() : gameTypeEnum.getEn_us());
        return stringBuilder.toString();
    }



    public static Map<String, List<Card>> convertToCard(String judgeResult, String split, String... roles) {
        Map<String, List<Card>> cardMap = new HashMap<>();


        String[] parts = judgeResult.split(Pattern.quote(split));
        if (parts.length < roles.length) {
            return cardMap;
        }
        for (int i = 0; i < roles.length; i++) {
            String[] cardValues = parts[i].split(":");
            List<Card> cards = parseCardArray(cardValues, roles[i]);
            cardMap.put(roles[i], cards);
        }

        return cardMap;
    }




    private static List<Card> parseCardArray(String[] cardArray, String role) {
        List<Card> cards = new ArrayList<>();
        for (String s : cardArray) {
            try {
                int cardValue = Integer.parseInt(s.trim());
                Card card = new Card().formatDBCard(cardValue);
                cards.add(card);
                System.out.println(role + " card: " + card);
            } catch (NumberFormatException e) {
                System.err.println("Invalid card value for " + role + ": " + s);
            }
        }
        return cards;
    }

    public static void main(String[] args) {
        String sss = "9; 2:16:13:17:10; 31:38:51:45:38; 33:42:3:4:14; 17:17;28:13:20";
        Map<String, List<Card>> stringListMap = convertToCard(sss,CommonConstant.SEMICOLON,"firstcard", "banker", "player1", "player2", "player3");
        System.out.println("DBSHOrderInfoUtil.main - "+stringListMap);
    }





}






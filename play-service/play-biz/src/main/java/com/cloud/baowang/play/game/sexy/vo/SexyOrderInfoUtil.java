package com.cloud.baowang.play.game.sexy.vo;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.play.api.enums.SexyGameTypeEnum;
import com.cloud.baowang.play.api.vo.card.Card;
import com.cloud.baowang.play.game.sa.SAGameCarRankEnum;
import com.cloud.baowang.play.game.sa.SAGameCarSuitEnum;
import com.cloud.baowang.play.game.sexy.enums.SEXYResultTypeEnum;
import com.cloud.baowang.play.game.sexy.enums.SexyExtraSBPlayTypeEnum;
import com.cloud.baowang.play.game.sexy.enums.SexyPlayTypeEnum;
import com.cloud.baowang.play.game.sh.enums.SHBetResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Slf4j
public class SexyOrderInfoUtil {

    public static int buildCardPower(List<Card> cardList){
        int power = 0;
        for (Card card : cardList) {
            power+= Math.min(card.getPokerNumber().getPower(), 10);
        }
        return power%10;
    }
    public static String getSexyResultList(String resultList ,String gameCode) {
        if (StringUtils.isEmpty(resultList)) {
            return "-";
        }
        SexyGameTypeEnum gameTypeEnum = SexyGameTypeEnum.getEnumByCode(gameCode);
        StringBuilder resultBuilder = new StringBuilder();
        switch (gameTypeEnum) {
            case BAC,BAC_2:
                //"\n[闲:♠ K ,♠ K ,♦ J ,庄♠ 8 ,♥ 4 ,♠ Q ,]\n

                Map<String, List<Card>> resultMap = JSON.parseObject(resultList, new TypeReference<Map<String, List<Card>>>(){});

                List<Card> banker = resultMap.get("banker");
                List<Card> player = resultMap.get("player");
                resultBuilder.append("\n");
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
                if(resultBuilder.charAt(resultBuilder.length() - 1) == ','){
                    resultBuilder.deleteCharAt(resultBuilder.length() - 1);
                }
                resultBuilder.append("]");
                resultBuilder.append("\n");
                break;
            case LH:
                Map<String, List<Card>> resultMapLH = JSON.parseObject(resultList, new TypeReference<Map<String, List<Card>>>(){});

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
                if(resultBuilder.charAt(resultBuilder.length() - 1) == ','){
                    resultBuilder.deleteCharAt(resultBuilder.length() - 1);
                }
                resultBuilder.append("]");
                resultBuilder.append("\n");
                break;
            case LP,SBR,E_SBR:
                String rsp = resultList.replaceAll("\\[\"(.*?)\"\\]", "[$1]");
                resultBuilder.append(rsp);
                resultBuilder.append("\n");
                break;

            case YSD:
                List<String> parseArray = JSON.parseArray(resultList, String.class);
                resultBuilder.append("[");
                buildNoI18Result(resultBuilder, parseArray);
                if(resultBuilder.length()>1 && resultBuilder.charAt(resultBuilder.length() - 1) == ','){
                    resultBuilder.deleteCharAt(resultBuilder.length() - 1);
                }
                resultBuilder.append("]");
                resultBuilder.append("\n");
                break;

            case YXX:
                List<String> list = JSON.parseArray(resultList, String.class);
                List<String> words = new ArrayList<>();
                if (!list.isEmpty()) {
                    String combined = list.get(0);
                    words = Arrays.asList(combined.split(","));
                }
                resultBuilder.append("[");
                buildNoI18Result(resultBuilder, words);
                if(resultBuilder.length()>1 && resultBuilder.charAt(resultBuilder.length() - 1) == ','){
                    resultBuilder.deleteCharAt(resultBuilder.length() - 1);
                }
                resultBuilder.append("]");
                resultBuilder.append("\n");
                break;
            case TPD :
                if (!JSON.isValid(resultList)) {
                    //老数据 滚
                    break;
                }
                Map<String, List<Card>> resultMapTPD = JSON.parseObject(resultList, new TypeReference<Map<String, List<Card>>>(){});
                List<Card> bankerTPD = resultMapTPD.get("banker");
                List<Card> playerTPD = resultMapTPD.get("player");
                resultBuilder.append("\n");
                resultBuilder.append("[");
                resultBuilder.append("PLAYER").append(":");
                for (Card card : playerTPD) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                resultBuilder.append("BANKER").append(":");
                for (Card card : bankerTPD) {
                    resultBuilder.append(convertCardResult(card)).append(",");
                }
                if(resultBuilder.charAt(resultBuilder.length() - 1) == ','){
                    resultBuilder.deleteCharAt(resultBuilder.length() - 1);
                }
                resultBuilder.append("]");
                resultBuilder.append("\n");
                break;
            case UNKNOWN:
                break;
        }
        return resultBuilder.toString();
    }

    private static void buildNoI18Result(StringBuilder resultBuilder, List<String> words) {
        for (String str : words) {
            Optional<SEXYResultTypeEnum> sexyResultTypeEnum = SEXYResultTypeEnum.fromCode(str);
            sexyResultTypeEnum.ifPresent(SEXYResultTypeEnum ->
                    resultBuilder.append(LanguageEnum.ZH_CN.getLang().equals(CurrReqUtils.getLanguage())?SEXYResultTypeEnum.getZh_CN():SEXYResultTypeEnum.getEn_US()));
            resultBuilder.append(",");
        }
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

    public static String buildBetTypeStr(String playType,String thirdGameCode) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] playTypes = playType.split(CommonConstant.COMMA);
        for (String type : playTypes) {
            if (thirdGameCode.equals(SexyGameTypeEnum.TPD.getCode())) {
                stringBuilder.append(type);
                continue;
            }
            if (thirdGameCode.equals(SexyGameTypeEnum.BAC.getCode())
                    || thirdGameCode.equals(SexyGameTypeEnum.LH.getCode())
                    || thirdGameCode.equals(SexyGameTypeEnum.LP.getCode())) {
                String gameTypeId = I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_PLAY_TYPE, type);
                stringBuilder.append(gameTypeId).append(CommonConstant.COMMA);
            }else if (thirdGameCode.equals(SexyGameTypeEnum.E_SBR.getCode())
                    || thirdGameCode.equals(SexyGameTypeEnum.YSD.getCode())
                    || thirdGameCode.equals(SexyGameTypeEnum.SBR.getCode())) {
                if (LanguageEnum.ZH_CN.getLang().equals(CurrReqUtils.getLanguage())){
                    SexyExtraSBPlayTypeEnum.fromKey(type).ifPresent(SexyExtraSBPlayTypeEnum -> stringBuilder.append(SexyExtraSBPlayTypeEnum.getDesc()));
                }else {
                    SexyExtraSBPlayTypeEnum.fromKey(type).ifPresent(SexyExtraSBPlayTypeEnum -> stringBuilder.append(SexyExtraSBPlayTypeEnum.getShCode()));
                }
            } else {
                if (LanguageEnum.ZH_CN.getLang().equals(CurrReqUtils.getLanguage())){
                    SexyPlayTypeEnum.fromKey(type).ifPresent(SexyPlayTypeEnum -> stringBuilder.append(SexyPlayTypeEnum.getDesc()));
                }else {
                    SexyPlayTypeEnum.fromKey(type).ifPresent(SexyPlayTypeEnum -> stringBuilder.append(SexyPlayTypeEnum.getShCode()));
                }
            }
        }
        if(stringBuilder.length()>1 && stringBuilder.charAt(stringBuilder.length() - 1) == ','){
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();

    }


    public static void main(String[] args) {
//        OrderRecordPO orderRecordPO = new OrderRecordPO();
//        orderRecordPO.setResultList("{\"banker\":[{\"pokerNumber\":\"KING\",\"pokerPattern\":\"DIAMOND\"},{\"pokerNumber\":\"TEN\",\"pokerPattern\":\"HEART\"},{\"pokerNumber\":\"ACE\",\"pokerPattern\":\"DIAMOND\"}],\"player\":[{\"pokerNumber\":\"SEVEN\",\"pokerPattern\":\"DIAMOND\"},{\"pokerNumber\":\"QUEEN\",\"pokerPattern\":\"DIAMOND\"}]}");
//        orderRecordPO.setThirdGameCode("MX-LIVE-001");
        String json = "[\"Crab,one,two\"]";

        // 1. 先把 JSON 字符串转成 List<String>
        List<String> list = JSON.parseArray(json, String.class);

        // 2. 取出第一个字符串，然后拆分成单词
        List<String> words = new ArrayList<>();
        if (!list.isEmpty()) {
            String combined = list.get(0);  // "Crab,one,two"
            words = Arrays.asList(combined.split(","));
        }

        System.out.println(words);
    }
}




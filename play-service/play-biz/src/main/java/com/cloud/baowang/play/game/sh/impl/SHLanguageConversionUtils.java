package com.cloud.baowang.play.game.sh.impl;

import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.play.api.enums.dg2.DG2GameTypeEnum;
import com.cloud.baowang.play.game.sa.*;
import com.cloud.baowang.play.game.sh.enums.SHBetResultEnum;
import com.cloud.baowang.play.game.sh.enums.SHGameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class SHLanguageConversionUtils {





    /**
     * 闪电龙虎的税费跟投注本金
     */
    public static BigDecimal getShLightningAmount(String parlayInfo,String orderInfo) {

        if (orderInfo != null && orderInfo.equals(SHGameTypeEnum.SDLH.getCode().toString())) {
            JSONObject jsonObject = JSON.parseObject(parlayInfo, JSONObject.class);
            if (jsonObject.containsKey("lightningAmount")) {
                //闪电龙虎税费
                return jsonObject.getBigDecimal("lightningAmount");
            }
        }
        return null;
    }


    /**
     * 闪电龙虎的税费跟投注本金
     */
    public static BigDecimal getShTotalAmount(String parlayInfo,String orderInfo) {

        if (orderInfo != null && orderInfo.equals(SHGameTypeEnum.SDLH.getCode().toString())) {
            JSONObject jsonObject = JSON.parseObject(parlayInfo, JSONObject.class);
            if (jsonObject.containsKey("totalAmount")) {
                //闪电龙虎 投注本金
                return jsonObject.getBigDecimal("totalAmount");
            }
        }
        return null;
    }

    /**
     * 视讯的闪电龙湖结果
     */
    public static String getShSDLHResult(JSONObject betResultVoucherSource) {
        StringBuilder stringBuilder = new StringBuilder();


        //龙
        JSONObject dragonJson = betResultVoucherSource.getJSONObject("dragon");

        if (dragonJson != null) {
            stringBuilder.append(I18nMessageUtil.getSystemParamAndTrans
                    (CommonConstant.SH_BET_RESULT, SHBetResultEnum.COMM_DRAGON.getCode()));

            //花色
            Integer pokerPattern = dragonJson.getInt("pokerPattern");
            SAGameCarSuitEnum carSuitEnum = SAGameCarSuitEnum.byCode(pokerPattern);
            if(pokerPattern != null && carSuitEnum != null){
                stringBuilder.append(carSuitEnum.getIcon());
            }

            //牌
            Integer pokerNumber = dragonJson.getInt("pokerNumber");
            SAGameCarRankEnum carRankEnum = SAGameCarRankEnum.byCode(pokerNumber);
            if(pokerNumber != null && carRankEnum != null){
                stringBuilder.append(carRankEnum.getDescription()).append("、");
            }
        }

        //虎
        JSONObject tigerJson = betResultVoucherSource.getJSONObject("tiger");
        if (tigerJson != null) {
            stringBuilder.append(I18nMessageUtil.getSystemParamAndTrans
                    (CommonConstant.SH_BET_RESULT, SHBetResultEnum.COMM_TIGER.getCode()));

            //花色
            Integer pokerPattern = tigerJson.getInt("pokerPattern");
            SAGameCarSuitEnum carSuitEnum = SAGameCarSuitEnum.byCode(pokerPattern);
            if(pokerPattern != null && carSuitEnum != null){
                stringBuilder.append(carSuitEnum.getIcon());
            }

            //牌
            Integer pokerNumber = tigerJson.getInt("pokerNumber");
            SAGameCarRankEnum carRankEnum = SAGameCarRankEnum.byCode(pokerNumber);
            if(pokerNumber != null && carRankEnum != null){
                stringBuilder.append(carRankEnum.getDescription());
            }
        }
        return stringBuilder.toString();
    }

    //视讯客户端结果牌
    public static String getClientConversionBetResult(String dataSource,String gameType) {
        //如果是闪电龙虎,后端直接把三方规则字符返回,客户端自己解析
        if(StringUtils.isNotBlank(gameType) && gameType.equals(String.valueOf(SHGameTypeEnum.SDLH.getCode()))){
            return dataSource;
        }
        return conversionBetResult(dataSource,gameType);
    }

    /**
     * 该方法用于视讯的结果牌逻辑,
     *
     * @param dataSource 三方数据源
     * @return 多语言翻译
     */
    public static String conversionBetResult(String dataSource,String gameType) {
        if (StringUtils.isBlank(dataSource)) {
            return dataSource;
        }

        //闪电龙虎的解析格式
        if(StringUtils.isNotBlank(gameType) && gameType.equals(String.valueOf(SHGameTypeEnum.SDLH.getCode()))){
            JSONObject betResultVoucherSource = JSON.parseObject(dataSource,JSONObject.class);
            return getShSDLHResult(betResultVoucherSource);
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



    public static String conversionDG2BetResult(String dataSource,String gameType) {
        if (StringUtils.isBlank(dataSource)) {
            return dataSource;
        }

        //闪电龙虎的解析格式
        if(StringUtils.isNotBlank(gameType)  ){
            if (gameType.equals(String.valueOf(DG2GameTypeEnum.DTX.getCode())) || gameType.equals(String.valueOf(DG2GameTypeEnum.BDTX.getCode()))){
                JSONObject betResultVoucherSource = JSON.parseObject(dataSource,JSONObject.class);
                return getShSDLHResult(betResultVoucherSource);
            }
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

    /**
     * SH视讯投注结果详情
     */
    public static String shOrderInfoBetResult(final Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();
        String betResult = null;
        //已结算
        if (map.containsKey("settlementTimestamp")) {
            Long settlementTimestamp = (Long) map.get("settlementTimestamp");
            if (settlementTimestamp != null && settlementTimestamp > 0) {
                if (map.containsKey("winLossAmount")) {
                    BigDecimal winLossAmount = map.get("winLossAmount") == null ? null : new BigDecimal(map.get("winLossAmount").toString());
                    if (winLossAmount != null) {
                        if (winLossAmount.compareTo(BigDecimal.ZERO) > 0) {
//                            betResult = "赢";
                            betResult = I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.SH_ORDER_RESULT_TITLE_WIN.getCode());
                        } else if (winLossAmount.compareTo(BigDecimal.ZERO) == 0) {
//                            betResult = "和";
                            betResult = I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.SH_ORDER_RESULT_TITLE_TIE.getCode());
                        } else {
//                            betResult = "输";
                            betResult = I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.SH_ORDER_RESULT_TITLE_LOSE.getCode());
                        }
                        stringBuilder.append(I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.SH_ORDER_RESULT_TITLE_WIN_OR_LOSE_RESULTS.getCode()))
                                .append(": ").append(betResult).append("\n");
                    }
                }
            }
        }

        Integer gameTypeId = (Integer) map.get("gameTypeId");
        if (Objects.equals(SHGameTypeEnum.SDLH.getCode(), gameTypeId)) {
//            stringBuilder.append("闪电：");
            stringBuilder.append(I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.SH_ORDER_RESULT_TITLE_LIGHTNING.getCode()))
                    .append(": ");

            if (map.containsKey("betResultVoucherSource")) {
                Object betResultVoucherSource =  map.get("betResultVoucherSource");
                JSONObject betResultVoucherSourceJson = com.alibaba.fastjson2.JSON.parseObject(betResultVoucherSource.toString(),JSONObject.class);
//                闪电
                JSONObject lightningJson = betResultVoucherSourceJson.getJSONObject("lightning");
                if (lightningJson != null) {
                    //牌
                    Integer pokerPattern = lightningJson.getInt("pokerPattern");
                    SAGameCarSuitEnum carSuitEnum = SAGameCarSuitEnum.byCode(pokerPattern);
                    if(pokerPattern != null && carSuitEnum != null){
                        stringBuilder.append(carSuitEnum.getDescription());
                    }

                    //倍数
                    Integer odds = lightningJson.getInt("odds");
                    if(odds != null){
                        stringBuilder.append(odds).append("X").append("\n");
                    }
                }

            }
        }
//        stringBuilder.append("局结果：");
        stringBuilder.append(I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.SH_ORDER_RESULT_GAME_RESULTS.getCode()))
                .append(": ");

        //闪电龙湖解析特殊
        if (Objects.equals(SHGameTypeEnum.SDLH.getCode(), gameTypeId)) {
            if (map.containsKey("betResultVoucherSource")) {

                Object betResultVoucherSourceObj =  map.get("betResultVoucherSource");
                JSONObject betResultVoucherSource = com.alibaba.fastjson2.JSON.parseObject(betResultVoucherSourceObj.toString(),JSONObject.class);
                stringBuilder.append(SHLanguageConversionUtils.getShSDLHResult(betResultVoucherSource));
            }
        } else {
            if (map.containsKey("betResult")) {
                stringBuilder.append(map.get("betResult"));
            }
        }

//        if (map.containsKey("gameTypeId")) {
//            Integer gameTypeId = (Integer) map.get("gameTypeId");
//            if (gameTypeId==22 && map.containsKey("betResultVoucher")) {
//                String betResultVoucher = (String) map.get("betResultVoucher");
//                // 结算时间
//                stringBuilder.append("闪电：").append(betResultVoucher.split("、")[2].split(":")[1]).append("\n");
//            }
//        }
        return stringBuilder.toString();
    }


}

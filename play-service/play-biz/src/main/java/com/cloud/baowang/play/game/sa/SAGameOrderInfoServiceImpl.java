package com.cloud.baowang.play.game.sa;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.VenueOrderInfoService;
import com.cloud.baowang.play.game.sh.impl.SAOrderInfoUtil;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.po.OrderRecordPO;
import com.cloud.baowang.play.service.GameInfoService;
import com.cloud.baowang.play.util.OrderRecordInfoTitleUtil;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service(ServiceType.GAME_INFO_THIRD_API_SERVICE + VenuePlatformConstants.SA)
public class SAGameOrderInfoServiceImpl implements VenueOrderInfoService {


    private final GameInfoService gameInfoService;

    @Override
    public String getGameVenueCode() {
        return VenuePlatformConstants.SA;
    }


    /**
     * 游戏详情
     */
    private String gameDetailInfo(final Map<String, Object> map) {


        List<GameInfoPO> gameInfoPOList = gameInfoService.queryGameByVenueCode(VenueEnum.SA.getVenueCode());
        Map<String, GameInfoPO> gameInfoPOMap = gameInfoPOList.stream().collect(Collectors.toMap(GameInfoPO::getAccessParameters, GameInfoPO -> GameInfoPO, (k1, k2) -> k2));

        String gameCode = String.valueOf(map.getOrDefault("hostID", ""));

        GameInfoPO gameInfoPO = gameInfoPOMap.get(gameCode);

        StringBuilder stringBuilder = new StringBuilder();
        if (ObjectUtil.isNotEmpty(gameInfoPO)) {
            OrderRecordInfoTitleUtil.setGameNameTitle(stringBuilder);
            stringBuilder.append(gameInfoPO.getGameName()).append("\n");
        }
        OrderRecordInfoTitleUtil.setGameTableNameTitle(stringBuilder);
        stringBuilder.append(gameCode).append("\n");

        if (map.containsKey("round")) {
            OrderRecordInfoTitleUtil.setTableNumberTitle(stringBuilder);
            stringBuilder.append(map.get("round")).append("\n");
        }
        return stringBuilder.toString();
    }


    /**
     * 投注详情
     */
    private String betDetailInfo(final Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();
        if (map.containsKey("gameType")) {
            String gameType = (String) map.get("gameType");
            String betType = String.format(CommonConstant.SA_BET_TYPE, gameType);
            Integer betTypeContext = (Integer) map.get("betType");
            String gameTypeId = I18nMessageUtil.getSystemParamAndTrans(betType, String.valueOf(betTypeContext));
            stringBuilder.append(I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.SH_ORDER_RESULT_TITLE_TABLE_BET.getCode())).append(": ").append(gameTypeId).append("\n");
        }

//        if (map.containsKey("rate")) {
//            stringBuilder.append("赔率:").append(map.get("rate"));
//        }
        return stringBuilder.toString();
    }

    /**
     * 投注结果
     */
    private String betResult(final Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();
        String betResult = null;
        //已结算
        if (map.containsKey("payoutTime")) {
            String payoutTime = (String) map.get("payoutTime");
            if (payoutTime != null) {
                if (map.containsKey("resultAmount")) {
                    BigDecimal winLossAmount = map.get("resultAmount") == null ? null : new BigDecimal(map.get("resultAmount").toString());
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
                        stringBuilder.append(I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.SH_ORDER_RESULT_TITLE_WIN_OR_LOSE_RESULTS.getCode())).append(": ");
                        stringBuilder.append(betResult).append("\n");
                    }
                }
            }
        }

        if (map.containsKey("gameResult")) {

            //游戏类型
            String gameType = (String) map.get("gameType");


//            JSONObject gameResultJson = new JSONObject(gameResult.toString(), true);
            JSONObject gameResultJson = JSONUtil.parseObj(map.get("gameResult"));

            stringBuilder.append(I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.SH_ORDER_RESULT_GAME_RESULTS.getCode())).append(": ");
            if(SAGameTypeEnum.ULTRAROULETTE.getCode().equals(gameType)){
//                JSONObject ultraRouletteResult = new JSONObject(gameResultJson.getStr("UltraRouletteResult"), true);
                JSONObject ultraRouletteResult = JSONUtil.parseObj(gameResultJson.getStr("UltraRouletteResult"));
                stringBuilder.append(SAOrderInfoUtil.getSaResultString(ultraRouletteResult, gameType));
            } if(SAGameTypeEnum.BAC.getCode().equals(gameType)){
                stringBuilder.append(SAOrderInfoUtil.getSaResultString(gameResultJson, gameType));
            }else if(SAGameTypeEnum.BLACKJACK.getCode().equals(gameType)){
                stringBuilder.append(SAOrderInfoUtil.getSaResultString(gameResultJson, gameType));
            }else {
                for (String key : gameResultJson.keySet()) {
//                    JSONObject subObj = new JSONObject(gameResultJson.getStr(key), true);
                    JSONObject subObj = JSONUtil.parseObj(gameResultJson.getStr(key));
                    if (subObj.containsKey("ResultDetail")) {
//                        JSONObject resultDetail = new JSONObject(subObj.getStr("ResultDetail"), true);
                        JSONObject resultDetail = JSONUtil.parseObj(subObj.getStr("ResultDetail"));
                        stringBuilder.append(SAOrderInfoUtil.getSaResultString(resultDetail, gameType));
                    }
                }
            }


        }
        return stringBuilder.toString();
    }


    @Override
    public List<Map<String, Object>> getOrderInfo(Map<String, Object> parlayMap) {


        List<Map<String, Object>> resultList = Lists.newArrayList();


        Map<String, Object> sportsMap = Maps.newHashMap();
        // 游戏详情
        sportsMap.put("gameDetailInfo", gameDetailInfo(parlayMap));
        // 投注详情
        sportsMap.put("betDetailInfo", betDetailInfo(parlayMap));

        // 注单结果
        sportsMap.put("orderResult", betResult(parlayMap));

        if (parlayMap.containsKey("payoutTime")) {
            String payoutTime = (String) parlayMap.get("payoutTime");
            Long settleTime = TimeZoneUtils.parseDate4TimeZoneToTime(payoutTime, TimeZoneUtils.patten_yyyyMMddHHmmssSSS, TimeZoneUtils.ShangHaiTimeZone);
            // 结算时间
            sportsMap.put("settleTime", settleTime);
        }

        resultList.add(sportsMap);

        return resultList;
    }


    public String getOrderRecordInfo(OrderRecordPO recordPO) {
        if (Objects.isNull(recordPO) || StringUtils.isEmpty(recordPO.getParlayInfo())) {
            return "";
        }
        String playType = recordPO.getPlayType();


        String betType = String.format(CommonConstant.SA_BET_TYPE, playType);
        String betTypeContext = recordPO.getBetContent();

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(recordPO.getGameName()).append("\n");

        OrderRecordInfoTitleUtil.setBetTableTitle(stringBuilder);//桌号
        stringBuilder.append(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SA_GAME_CODE,recordPO.getDeskNo())).append("\n");

        OrderRecordInfoTitleUtil.setBetTitle(stringBuilder);//下注
        stringBuilder.append(toSetRedText(betType,betTypeContext)).append("\n");

        OrderRecordInfoTitleUtil.setResultTitle(stringBuilder);//结果
        stringBuilder.append(SAOrderInfoUtil.getSaResultList(recordPO)).append("\n");

        return stringBuilder.toString();
    }


    public String toSetRedText(String type,String text) {
        text = "<span style=\"color: red;\">" + I18nMessageUtil.getSystemParamAndTrans(type, text) + "</span>";
        return text;
    }

}

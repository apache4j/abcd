package com.cloud.baowang.play.game.sh.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.VenueOrderInfoService;
import com.cloud.baowang.play.game.sa.SAGameCarSuitEnum;
import com.cloud.baowang.play.game.sh.enums.SHGameTypeEnum;
import com.cloud.baowang.play.po.OrderRecordPO;
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

@AllArgsConstructor
@Service(ServiceType.GAME_INFO_THIRD_API_SERVICE + VenuePlatformConstants.SH)
public class SHGameOrderInfoServiceImpl implements VenueOrderInfoService {


    @Override
    public String getGameVenueCode() {
        return VenuePlatformConstants.SH;
    }


    /**
     * 游戏详情
     */
    private String gameDetailInfo(final Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();
        if (map.containsKey("gameTypeName")) {
            OrderRecordInfoTitleUtil.setGameNameTitle(stringBuilder);
            stringBuilder.append(map.get("gameTypeName")).append("\n");
        }

        if (map.containsKey("deskNo")) {
            OrderRecordInfoTitleUtil.setGameTableNameTitle(stringBuilder);
            stringBuilder.append(map.get("deskNo")).append("\n");
        }

        if (map.containsKey("gameNo")) {
            OrderRecordInfoTitleUtil.setTableNumberTitle(stringBuilder);
            stringBuilder.append(map.get("gameNo")).append("\n");
        }
        return stringBuilder.toString();
    }


    /**
     * 投注详情
     */
    private String betDetailInfo(final Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();

        if (map.containsKey("playType")) {
            String gameTypeId = I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_PLAY_TYPE, String.valueOf(map.get("playType")));
            OrderRecordInfoTitleUtil.setBetTitle(stringBuilder);
            stringBuilder.append(gameTypeId).append("\n");
        }

        if (map.containsKey("rate")) {
            stringBuilder.append("赔率:").append(map.get("rate"));
        }
        return stringBuilder.toString();
    }

    /**
     * 投注结果
     */
    private String betResult(final Map<String, Object> map) {
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
                                .append(betResult).append("\n");
                    }
                }
            }
        }

        Integer gameTypeId = (Integer) map.get("gameTypeId");
        if (Objects.equals(SHGameTypeEnum.SDLH.getCode(), gameTypeId)) {
//            stringBuilder.append("闪电：");
            stringBuilder.append(I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.SH_ORDER_RESULT_TITLE_LIGHTNING.getCode()));

            if (map.containsKey("betResultVoucherSource")) {
                Object betResultVoucherSource =  map.get("betResultVoucherSource");
                JSONObject betResultVoucherSourceJson = JSON.parseObject(betResultVoucherSource.toString(),JSONObject.class);
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
        stringBuilder.append(I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.SH_ORDER_RESULT_GAME_RESULTS.getCode()));

        //闪电龙湖解析特殊
        if (Objects.equals(SHGameTypeEnum.SDLH.getCode(), gameTypeId)) {
            if (map.containsKey("betResultVoucherSource")) {

                Object betResultVoucherSourceObj =  map.get("betResultVoucherSource");
                JSONObject betResultVoucherSource = JSON.parseObject(betResultVoucherSourceObj.toString(),JSONObject.class);
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


    @Override
    public List<Map<String, Object>> getOrderInfo(Map<String, Object> parlayMap) {
        List<Map<String, Object>> resultList = Lists.newArrayList();


        Map<String, Object> sportsMap = Maps.newHashMap();
        // 游戏详情
        sportsMap.put("gameDetailInfo", gameDetailInfo(parlayMap));
        // 投注详情
        sportsMap.put("betDetailInfo", betDetailInfo(parlayMap));

        // 注单结果
        sportsMap.put("orderResult", SHLanguageConversionUtils.shOrderInfoBetResult(parlayMap));

        if (parlayMap.containsKey("settlementTimestamp")) {
            Long settlementTimeStamp = (Long) parlayMap.get("settlementTimestamp");
            // 结算时间
            sportsMap.put("settleTime", settlementTimeStamp);
        }

        resultList.add(sportsMap);

//        else {
//            for (Map<String, Object> map : list) {
//                Map<String, Object> sportsMap = Maps.newHashMap();
//                String matchInfo = SBAOrderParseUtil.getSimpleMatchInfo(map);
//                sportsMap.put("matchInfo", matchInfo);
//                // 盘口详情
//                String marketInfo = SBAOrderParseUtil.getMarketInfo(map);
//                sportsMap.put("marketInfo", marketInfo);
//                // 注单详情
////                String orderInfo = SBAOrderParseUtil.getParlayOrderInfo(parlayMap, map);
//                sportsMap.put("orderInfo", getOrderInfoDetail(map));
//                // 结算时间(单关如果没有内层结算时间，则取外层结算时间) 时间戳特殊处理
////                Long settleTime = TimeZoneUtils.parseDate4TimeZoneToTime(sbaGameService
////                        .getSettledTime(Integer.parseInt(Optional.ofNullable(map.get("sport_type"))
////                                .orElse("").toString()), Integer.parseInt(Optional.ofNullable(map
////                                .get("bet_type")).orElse("").toString()), Optional.ofNullable(map.get("ticket_status"))
////                                .orElse("").toString(), Optional.ofNullable(map.get("winlost_datetime"))
////                                .orElse("").toString(), Optional.ofNullable(parlayMap.get("settlement_time"))
////                                .orElse("").toString()), TimeZoneUtils.pattenT_SSS, TimeZoneUtils.NewYorkTimeZone);
//
//
//                if (parlayMap.containsKey("settlement_time")) {
//                    String settlementTime = parlayMap.get("settlement_time").toString();
//                    String time = sbaGameService.formatTime(settlementTime);
//                    if (ObjectUtil.isNotEmpty(time)) {
//                        Long settleTime = TimeZoneUtils.convertToTimestamp(time, timeZone, null);
//                        sportsMap.put("settleTime", String.valueOf(settleTime));
//                    }
//                }
//
//                // 注单结果
//                String orderResult = SBAOrderParseUtil.getOrderResult(map);
//                sportsMap.put("orderResult", orderResult);
//                resultList.add(sportsMap);
//            }
//        }
        return resultList;
    }

    public String getOrderRecordInfo(OrderRecordPO recordPO) {
        // CurrReqUtils.getTimezone();
        if (Objects.isNull(recordPO) || StringUtils.isEmpty(recordPO.getParlayInfo())) {
            return "";
        }

        //闪电龙湖的逻辑是独立的
        if (SHGameTypeEnum.SDLH.getCode().toString().equals(recordPO.getOrderInfo())) {
            JSONObject jsonObject = JSONUtil.parseObj(recordPO.getParlayInfo());
            Map<String,Object> parlayMap = jsonObject;
            return SHLanguageConversionUtils.shOrderInfoBetResult(parlayMap);
        }


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(recordPO.getGameName()).append("\n");

        OrderRecordInfoTitleUtil.setBetTableTitle(stringBuilder);
        stringBuilder.append(recordPO.getDeskNo()).append("\n");

        OrderRecordInfoTitleUtil.setBetTitle(stringBuilder);
        stringBuilder.append(toSetRedText(recordPO.getPlayType())).append("\n");

         if (StrUtil.isNotBlank(recordPO.getResultList())) {
             OrderRecordInfoTitleUtil.setResultTitle(stringBuilder);
             stringBuilder.append(SHLanguageConversionUtils.conversionBetResult(recordPO.getResultList(), recordPO.getOrderInfo())).append("\n");
        }
        return stringBuilder.toString();
    }


    public String toSetRedText(String text) {
        if (ObjectUtil.isEmpty(text)) {
            return null;
        }
        text = "<span style=\"color: red;\">" + I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_PLAY_TYPE, text) + "</span>";
        return text;
    }
}

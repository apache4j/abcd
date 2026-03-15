package com.cloud.baowang.play.game.acelt.utils;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.game.acelt.enums.AceLtBetResultStatusEnums;
import com.cloud.baowang.play.game.acelt.enums.AceLtPlayTypeEnum;
import com.cloud.baowang.play.game.acelt.response.AceLtBetRecord;
import com.cloud.baowang.play.po.OrderRecordPO;
import com.cloud.baowang.play.task.acelt.params.AceLtVenuePullBetParams;
import com.cloud.baowang.play.util.OrderRecordInfoTitleUtil;
import com.google.common.collect.Maps;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class AceLtOrderParseUtil {

    /**
     * 获取名称
     * 游戏名称
     */
    public static String getGameNameInfo(final Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();
        if (map.containsKey("gameName")) {
            OrderRecordInfoTitleUtil.setAceGameNameTitle(stringBuilder);
            stringBuilder.append(map.get("gameName")).append("\n");
        }

        if (map.containsKey("issueNo")) {
            OrderRecordInfoTitleUtil.setAceTableNumberTitle(stringBuilder);
            stringBuilder.append(map.get("issueNo")).append("\n");
        }

        return stringBuilder.toString();
    }


    /**
     * 获取注单单关，串关投注单信息
     *
     * @param map
     * @return
     */
    public static String getOrderInfo(final Map<String, Object> map) {
        return map.get("ticketResult") + "@" + map.get("odd");
    }

    public static Object getPlayNameInfo(Map<String, Object> map) {
        if(map.containsKey("gamePlayCode")){
            String gamePlayCode = (String) map.get("gamePlayCode");
           return AceLtPlayTypeEnum.getNameByCode(gamePlayCode);
        }
        return null;
    }

    public static Object betOrderInfo(Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();
        if (map.containsKey("nums") && map.get("nums") != null) {
            String betStr = (String) map.get("nums");
            OrderRecordInfoTitleUtil.setBetTitle(stringBuilder);
            stringBuilder.append(betStr).append("\n");
        }

        if (map.containsKey("curOdd")) {
            OrderRecordInfoTitleUtil.setOddsTitle(stringBuilder);
            stringBuilder.append(map.get("curOdd")).append("\n");
        }

        if (map.containsKey("multiple")) {
            OrderRecordInfoTitleUtil.setMultipleTitle(stringBuilder);
            stringBuilder.append(map.get("multiple")).append("\n");
        }

        if (map.containsKey("betCount")) {
            OrderRecordInfoTitleUtil.setBetCountTitle(stringBuilder);
            stringBuilder.append(map.get("betCount")).append("\n");
        }

        return stringBuilder.toString();
    }

    public static Object getSettleTime(Map<String, Object> map) {
        if (map.containsKey("settleTime")) {
            return map.get("settleTime");
        }
        return null;
    }

    public static String getStatus(Map<String, Object> map) {
        return AceLtBetResultStatusEnums.nameOfCode((Integer) map.get("state"));
    }

    public static AceLtVenuePullBetParams nextAceLtPullBetParams(AceLtVenuePullBetParams params) {

        params.setStartTime(params.getEndTime() - params.getStep());
        params.setEndTime(params.getEndTime() + params.getStep());
        if (params.getEndTime() > System.currentTimeMillis()) {
            params.setStartTime(System.currentTimeMillis() - params.getStep());
            params.setEndTime(System.currentTimeMillis());
        }
        return params;
    }

    /**
     * 王牌彩票与彩票的注单详情
     */
    public static List<Map<String, Object>> getDetailOrderInfo(Map<String, Object> map){
        List<Map<String, Object>> resultList = Lists.newArrayList();
        Map<String, Object> weMap = Maps.newHashMap();
        // 彩种详情
        weMap.put("aceltInfo", AceLtOrderParseUtil.getGameNameInfo(map));
        // 玩法详情
        weMap.put("playNameInfo", AceLtOrderParseUtil.getPlayNameInfo(map));
        // 注单详情
        weMap.put("betOrderInfo", AceLtOrderParseUtil.betOrderInfo(map));
        // 结算时间
        weMap.put("settleTime", AceLtOrderParseUtil.getSettleTime(map));


        StringBuilder betOrderResult = new StringBuilder();

        if (map.containsKey("state")) {
            Integer state = (Integer) map.get("state");
            if(state == AceLtBetResultStatusEnums.SETTLEMENT.getCode()){
                //是否中奖(0-未中奖，1-已中奖 ，2-和局)
                if (map.containsKey("isWin")) {
                    OrderRecordInfoTitleUtil.setWinOrLoseResultTitle(betOrderResult);
                    Integer lotteryNum = (Integer) map.get("isWin");
                    if (lotteryNum == 0) {
                        OrderRecordInfoTitleUtil.setLoseTitle(betOrderResult);
//                        betOrderResult.append("输");
                    } else if (lotteryNum == 1) {
                        OrderRecordInfoTitleUtil.setWinTitle(betOrderResult);
//                        betOrderResult.append("赢");
                    } else {
                        OrderRecordInfoTitleUtil.setTieTitle(betOrderResult);
//                        betOrderResult.append("和局");
                    }
                    betOrderResult.append("\n");
                }
            }
        }


        //开奖结果
        if (map.containsKey("lotteryNum")) {
            String lotteryNum = (String) map.get("lotteryNum");
            if (ObjectUtil.isNotEmpty(lotteryNum)) {
                OrderRecordInfoTitleUtil.setLotteryResultsTitle(betOrderResult);
                betOrderResult.append(lotteryNum);
            }
        }
        weMap.put("betOrderResult", betOrderResult.toString());
        resultList.add(weMap);
        return resultList;
    }


    public static String getOrderRecordInfo(OrderRecordPO recordPO) {

        StringBuilder stringBuilder = new StringBuilder();

        try {

            Map<String, Object> map = JSONObject.parseObject(recordPO.getParlayInfo(), Map.class);
            if (map.containsKey("gameName")) {
                String gameName = (String) map.get("gameName");
                if (ObjectUtil.isNotEmpty(gameName)) {
                    OrderRecordInfoTitleUtil.setAceGameNameTitle(stringBuilder);
                    stringBuilder.append(gameName).append("\n");
                }
            }

            if (map.containsKey("issueNo")) {
                String issueNo = (String) map.get("issueNo");
                if (ObjectUtil.isNotEmpty(issueNo)) {
                    OrderRecordInfoTitleUtil.setAceTableNumberTitle(stringBuilder);
                    stringBuilder.append(issueNo).append("\n");
                }
            }

            if (map.containsKey("gamePlayCode")) {
                OrderRecordInfoTitleUtil.setPlayTypeTitle(stringBuilder);
                stringBuilder.append(AceLtOrderParseUtil.getPlayNameInfo(map)).append("\n");
            }

            if (map.containsKey("nums")) {
                String nums = (String) map.get("nums");
                if (ObjectUtil.isNotEmpty(nums)) {
                    OrderRecordInfoTitleUtil.setBetTitle(stringBuilder);
                    stringBuilder.append(toSetRedText(nums)).append("\n");
                }

                if (map.containsKey("curOdd")) {
                    String curOdd = String.valueOf(map.get("curOdd"));
                    if (ObjectUtil.isNotEmpty(curOdd)) {
                        stringBuilder.append("@").append(curOdd).append("\n");
                    }
                }
            }




            if (map.containsKey("lotteryNum")) {
                String lotteryNum = (String) map.get("lotteryNum");
                if (ObjectUtil.isNotEmpty(lotteryNum)) {
                    OrderRecordInfoTitleUtil.setLotteryResultsTitle(stringBuilder);
                    stringBuilder.append(lotteryNum).append("\n");
                }
            }
        } catch (Exception e) {
            return null;
        }
        return stringBuilder.toString();
    }

    public static String toSetRedText(String text) {
        if (ObjectUtil.isEmpty(text)) {
            return null;
        }
        text = "<span style=\"color: red;\">" + text + "</span>";
        return text;
    }
}

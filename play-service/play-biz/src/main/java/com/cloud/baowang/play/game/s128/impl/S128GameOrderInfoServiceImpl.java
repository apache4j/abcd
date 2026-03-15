package com.cloud.baowang.play.game.s128.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.VenueOrderInfoService;
import com.cloud.baowang.play.game.s128.enums.BetContentEnum;
import com.cloud.baowang.play.po.OrderRecordPO;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor
@Service(ServiceType.GAME_INFO_THIRD_API_SERVICE + VenuePlatformConstants.S128)
@Slf4j
public class S128GameOrderInfoServiceImpl  implements VenueOrderInfoService {

    @Override
    public String getGameVenueCode() {
        return VenuePlatformConstants.S128;
    }


    protected final static String MATCH_INFO_S128 = "赛事名称: %s\n赛事编号: %s\n日场次:  %s\n对阵信息:  %s\n";

    protected final static String ORDER_INFO_S128 = "投注: %s\n赔率: %s";

    protected final static String ORDER_RESULT = "注单结果: %s\n赛场结果: %s";

    private String getSimpleMatchInfo(Map<String, Object> parlyMap){
        String arenaNameCn = parlyMap.getOrDefault("arenaNameCn", "").toString();
        if (StrUtil.isEmpty(arenaNameCn)){
            arenaNameCn = parlyMap.getOrDefault("arenaCode","").toString();
        }
        String matchNo = parlyMap.getOrDefault("matchNo", "").toString();
        if (StrUtil.isEmpty(arenaNameCn)){
            matchNo = parlyMap.getOrDefault("matchNO","").toString();
        }
        String fightNo = parlyMap.getOrDefault("fightNo","").toString();

        String meronCockCnVSwalaCockCn = "龙 VS 凤";

        return String.format(MATCH_INFO_S128,arenaNameCn, matchNo,fightNo,meronCockCnVSwalaCockCn);
    }

    private String getMarketInfo(Map<String, Object> parlyMap){
        return parlyMap.getOrDefault("meronCockCn","").toString() + parlyMap.getOrDefault("walaCockCn","").toString();
    }


    private String getEsportOrderInfo(Map<String, Object> parlyMap, String totalOdd){
        String odd = parlyMap.getOrDefault("oddsGiven", "").toString();
        String betOn = parlyMap.getOrDefault("betOn", "").toString();
        String betOnName = BetContentEnum.fromName(betOn);
        return String.format(ORDER_INFO_S128,(betOnName),odd);
    }

    private String getOrderResult(Map<String, Object> parlyMap){
        String result = "";
        String resCode = parlyMap.getOrDefault("status","").toString();
        if (resCode.equalsIgnoreCase("WIN")){
            result = "赢";
        }else if (resCode.equalsIgnoreCase("LOSE")){
            result = "输";
        } else if (resCode.equalsIgnoreCase("REFUND")) {
            result = "取消";
        }else if (resCode.equalsIgnoreCase("CANCEL")) {
            result = "取消";
        }
        String fightResult = parlyMap.getOrDefault("fightResult", "").toString();
        String fromName = BetContentEnum.fromName(fightResult);

        return String.format(ORDER_RESULT,result, fromName);
    }

    @Override
    public List<Map<String, Object>> getOrderInfo(Map<String, Object> parlayMap) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (Objects.isNull(parlayMap)) {
            return list;
        }
        Map<String, Object> eSportsMap = Maps.newHashMap();
        // 比赛详情 matchInfo
        String matchInfo = getSimpleMatchInfo(parlayMap);
        eSportsMap.put("matchInfo", matchInfo);
        // 盘口详情 marketInfo
        eSportsMap.put("marketInfo", "龙凤");

        // 注单详情 orderInfo
        String orderInfo = getEsportOrderInfo(parlayMap,null);
        eSportsMap.put("orderInfo", orderInfo);
        // 结算时间 时间戳特殊处理
        String settlementDateTimeStr = parlayMap.getOrDefault("processedDatetime","0").toString();
        Long settleTime = TimeZoneUtils.parseDate4TimeZoneToTime(settlementDateTimeStr, TimeZoneUtils.patten_yyyyMMddHHmmss, TimeZoneUtils.ShangHaiTimeZone);
        eSportsMap.put("settleTime", null == settleTime ? null : String.valueOf(settleTime));
        // 注单结果
        String orderResult = getOrderResult(parlayMap);
        eSportsMap.put("orderResult", orderResult);
        list.add(eSportsMap);

        return list;
    }

    public String getOrderRecordInfo(OrderRecordPO recordPO) {
        if (Objects.isNull(recordPO) || StringUtils.isEmpty(recordPO.getParlayInfo())) {
            return "";
        }
        String str = "";
        JSONObject parlayMap = null;
        if (StringUtils.isNotBlank(recordPO.getParlayInfo())) {
            try {
                parlayMap = JSONObject.parseObject(recordPO.getParlayInfo());
            } catch (Exception e) {
                log.error(" S128 parlayInfo format error");
            }
        }
        if (parlayMap!=null){
            String arenaNameCn = parlayMap.getOrDefault("arenaNameCn","").toString();
            if (StrUtil.isEmpty(arenaNameCn)){
                arenaNameCn = parlayMap.getOrDefault("arenaCode","").toString();
            }
            String createdDatetime = parlayMap.getOrDefault("createdDatetime","").toString();
            String matchNo = parlayMap.getOrDefault("matchNo","").toString();
            if (StrUtil.isEmpty(matchNo)){
                matchNo = parlayMap.getOrDefault("matchNO","").toString();
            }
            String oddsGiven = parlayMap.getOrDefault("oddsGiven","").toString();
            String fightNo = parlayMap.getOrDefault("fightNo","").toString();

            String betOn = parlayMap.getOrDefault("betOn","").toString();

            String betOnName = BetContentEnum.fromName(betOn);

            str += arenaNameCn + "（" + createdDatetime + "）\n";
            str += "赛事编号: " + matchNo + "\n";
            str += "日场次: " + fightNo + "\n";
            str += "对阵信息: 龙 VS 凤\n";
            str += "下注: " + toSetRedText(betOnName) +"@"+ oddsGiven+ "\n";
            str += "结果: " ;
            if (StrUtil.isNotBlank(recordPO.getResultList())) {
                str += BetContentEnum.fromName(recordPO.getResultList()) + "\n";
            }
            if (StrUtil.isEmpty(recordPO.getGameNo())){
                recordPO.setGameNo(parlayMap.getOrDefault("arenaCode","").toString());
            }
        }
        return str;
    }



    public static String toSetRedText(String text) {
        if (ObjectUtil.isEmpty(text)) {
            return "";
        }
        text = "<span style=\"color: red;\">" + text + "</span>";
        return text;
    }
}

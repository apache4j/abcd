package com.cloud.baowang.play.util;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.play.api.vo.dbDj.DbDJOrderRecordDetailRes;
import com.cloud.baowang.play.api.vo.dbPanDaSport.orderRecord.DbPanDaSportOrderDetail;
import com.cloud.baowang.play.api.vo.dbPanDaSport.orderRecord.DbPanDaSportOrderRecordRes;
import com.cloud.baowang.play.po.OrderRecordPO;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class DbDJOrderUtil {

    /**
     * 赛事ID
     */
    public static String getGameNo(OrderRecordPO recordPO) {
        StringBuilder stringBuilder = new StringBuilder();

        Map<String, Object> parlayMap = JSONObject.parseObject(recordPO.getParlayInfo(), Map.class);
        //英雄召唤注单详情
        if (parlayMap.containsKey("series_name")) {
            if (!parlayMap.containsKey("ticket_plan_no")) {
                return (String) parlayMap.get("ticket_plan_no");
            }
        } else {//电子
            JSONArray jsonArray = (JSONArray) parlayMap.get("detail");
            if (jsonArray!=null && !jsonArray.isEmpty()) {
                List<DbDJOrderRecordDetailRes> detailList = jsonArray.toJavaList(DbDJOrderRecordDetailRes.class);
                for (DbDJOrderRecordDetailRes item : detailList) {
                    stringBuilder.append(item.getMatch_id()).append("\n");
                }
            }else {
                stringBuilder.append(parlayMap.get("match_id")).append("\n");
            }
        }
        return stringBuilder.toString();
    }
}

package com.cloud.baowang.play.util;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.play.api.vo.dbPanDaSport.orderRecord.DbPanDaSportOrderDetail;
import com.cloud.baowang.play.api.vo.dbPanDaSport.orderRecord.DbPanDaSportOrderRecordRes;
import com.cloud.baowang.play.po.OrderRecordPO;

import java.util.List;

public class DbPanDaSportOrderUtil {

    /**
     * 赛事ID
     */
    public static String getGameNo(OrderRecordPO recordPO){
        DbPanDaSportOrderRecordRes recordRes = JSONObject.parseObject(recordPO.getParlayInfo(), DbPanDaSportOrderRecordRes.class);
        List<DbPanDaSportOrderDetail> detailList = recordRes.getDetailList();
        StringBuilder stringBuilder = new StringBuilder();
        for (DbPanDaSportOrderDetail item : detailList) {
            stringBuilder.append(item.getMatchId()).append("\n");
        }
        return stringBuilder.toString();
    }
}

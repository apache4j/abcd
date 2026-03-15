package com.cloud.baowang.play.game.base;


import com.cloud.baowang.play.api.vo.order.client.EventOrderClientResVO;
import com.cloud.baowang.play.api.vo.order.client.SportOrderClientResVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.po.OrderRecordPO;

import java.util.List;
import java.util.Map;

public interface VenueOrderInfoService {

    /**
     * 获取当前游戏大类场馆code
     *
     * @return
     */
    String getGameVenueCode();

    /**
     * 各游戏大类场馆下注单明细展示
     *
     * @param parlayMap
     */
    List<Map<String, Object>> getOrderInfo(Map<String, Object> parlayMap);

    default String getOrderRecordInfo(OrderRecordPO recordPO) {
        return null;
    }

    /**
     * 各游戏大类场馆下注单明细展示(客户端)
     */
    // List<Map<String, Object>> getClientOrderInfo(BetGameOrderInfoVO orderInfoVO, Map<String, Object> parlayMap);


    default String getBetType(String json) {
        return null;
    }

    /**
     * 体育
     */
    default SportOrderClientResVO getSportData(OrderRecordPO record, String lang, VenueInfoVO venueInfoVO) {
        return null;
    }


    /**
     * 电竞
     */
    default EventOrderClientResVO getESportData(OrderRecordPO record) {
        return null;
    }



}

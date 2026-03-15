//package com.cloud.baowang.play.game.acelt;
//
//import cn.hutool.core.util.ObjectUtil;
//import com.alibaba.fastjson2.JSONObject;
//import com.cloud.baowang.play.api.constants.VenueCodeConstants;
//import com.cloud.baowang.play.constants.ServiceType;
//import com.cloud.baowang.play.game.acelt.enums.AceLtBetResultStatusEnums;
//import com.cloud.baowang.play.game.acelt.utils.AceLtOrderParseUtil;
//import com.cloud.baowang.play.game.base.VenueOrderInfoService;
//import com.cloud.baowang.play.po.OrderRecordPO;
//import com.google.common.collect.Maps;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.compress.utils.Lists;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * <h2></h2>
// */
//@Slf4j
//@AllArgsConstructor
//@Service(ServiceType.GAME_INFO_THIRD_API_SERVICE + VenueCodeConstants.WP_ACELT)
//public class AceLtWPOrderServiceImpl implements VenueOrderInfoService {
//
//
//    @Override
//    public String getGameVenueCode() {
//        return VenueCodeConstants.WP_ACELT;
//    }
//
//    @Override
//    public List<Map<String, Object>> getOrderInfo(Map<String, Object> map) {
//        return AceLtOrderParseUtil.getDetailOrderInfo(map);
//    }
//
//    public String getOrderRecordInfo(OrderRecordPO recordPO) {
//        return AceLtOrderParseUtil.getOrderRecordInfo(recordPO);
//    }
//
////    @Override
////    public List<Map<String, Object>> getClientOrderInfo(BetGameOrderInfoVO orderInfoVO, Map<String, Object> parlayMap) {
////        // 替换下注玩法
////        orderInfoVO.setPlayType(orderInfoVO.getGameName());
////        List<Map<String, Object>> result = Lists.newArrayList();
////        Map<String, Object> map = Maps.newHashMap();
////        // 投注内容
////        map.put("betContent", parlayMap.get("numsName"));
////        // 是否追号
////        map.put("chaseNum", "");
////        result.add(map);
////        return result;
////    }
//}

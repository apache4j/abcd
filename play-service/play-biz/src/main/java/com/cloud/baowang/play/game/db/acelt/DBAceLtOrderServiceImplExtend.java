package com.cloud.baowang.play.game.db.acelt;

import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.base.VenueOrderInfoService;
import com.cloud.baowang.play.po.OrderRecordPO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <h2></h2>
 */
@Slf4j
@AllArgsConstructor
@Service(ServiceType.GAME_INFO_THIRD_API_SERVICE + VenuePlatformConstants.DB_ACELT)
public class DBAceLtOrderServiceImplExtend implements VenueOrderInfoService {


    @Override
    public String getGameVenueCode() {
        return VenuePlatformConstants.DB_ACELT;
    }

    @Override
    public List<Map<String, Object>> getOrderInfo(Map<String, Object> map) {
        return DBAceLtOrderParseUtil.getDetailOrderInfo(map);
    }

    public String getOrderRecordInfo(OrderRecordPO recordPO) {
        return DBAceLtOrderParseUtil.getOrderRecordInfo(recordPO);
    }

//    @Override
//    public List<Map<String, Object>> getClientOrderInfo(BetGameOrderInfoVO orderInfoVO, Map<String, Object> parlayMap) {
//        // 替换下注玩法
//        orderInfoVO.setPlayType(orderInfoVO.getGameName());
//        List<Map<String, Object>> result = Lists.newArrayList();
//        Map<String, Object> map = Maps.newHashMap();
//        // 投注内容
//        map.put("betContent", parlayMap.get("numsName"));
//        // 是否追号
//        map.put("chaseNum", "");
//        result.add(map);
//        return result;
//    }
}

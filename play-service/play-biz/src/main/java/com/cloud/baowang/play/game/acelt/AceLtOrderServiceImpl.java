package com.cloud.baowang.play.game.acelt;

import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.acelt.utils.AceLtOrderParseUtil;
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
@Service(ServiceType.GAME_INFO_THIRD_API_SERVICE + VenuePlatformConstants.ACELT)
public class AceLtOrderServiceImpl implements VenueOrderInfoService {


    @Override
    public String getGameVenueCode() {
        return VenuePlatformConstants.ACELT;
    }

    @Override
    public List<Map<String, Object>> getOrderInfo(Map<String, Object> map) {
        return AceLtOrderParseUtil.getDetailOrderInfo(map);
    }

    public String getOrderRecordInfo(OrderRecordPO recordPO) {
        return AceLtOrderParseUtil.getOrderRecordInfo(recordPO);
    }
}

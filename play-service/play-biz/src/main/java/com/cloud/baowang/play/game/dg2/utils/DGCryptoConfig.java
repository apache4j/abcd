package com.cloud.baowang.play.game.dg2.utils;

import com.cloud.baowang.play.api.api.third.VenueUserAccountApi;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DGCryptoConfig {

    private  String agentName;

    private final VenueUserAccountApi venueUserAccountApi;


    public void init() {
        if (StringUtils.isEmpty(agentName)) {
            VenueInfoVO venueInfoVO = venueUserAccountApi.getVenueInfoByVenueCode(VenueEnum.DG2.getVenueCode());
            this.agentName = venueInfoVO.getMerchantNo();
        }

    }
    public String getAgentName() {
        init();
        return agentName;
    }

}

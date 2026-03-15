package com.cloud.baowang.play.wallet.vo.sexy.utils;

import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.api.third.VenueUserAccountApi;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SexyCryptoConfig {

    private String KEY;

    private final VenueUserAccountApi venueUserAccountApi;


    public void init() {
        if (StringUtils.isEmpty(KEY)) {
            VenueInfoVO venueInfoVO = venueUserAccountApi.getVenueInfoByVenueCode(VenueEnum.SEXY.getVenueCode());
            this.KEY = venueInfoVO.getMerchantKey();
        }

    }
    public String getKey() {
        init();
        return KEY;
    }

}

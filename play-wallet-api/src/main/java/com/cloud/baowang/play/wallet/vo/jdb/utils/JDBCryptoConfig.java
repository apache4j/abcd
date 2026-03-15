package com.cloud.baowang.play.wallet.vo.jdb.utils;

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
public class JDBCryptoConfig {

    private  String KEY;
    private String IV;

    private final VenueUserAccountApi venueUserAccountApi;


    public void init() {
//        log.info( "VenueInfoVO : key - "+KEY+ " iv - "+IV );
        if (StringUtils.isEmpty(KEY) || StringUtils.isEmpty(IV)) {
            VenueInfoVO venueInfoVO = venueUserAccountApi.getVenueInfoByVenueCode(VenueEnum.JDB.getVenueCode());
            this.KEY = venueInfoVO.getAesKey();
            this.IV = venueInfoVO.getMerchantKey();
        }

    }
    public String getKey() {
        init();
        return KEY;
    }
    public String getIV() {
        init();
        return IV;
    }
}

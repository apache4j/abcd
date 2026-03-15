package com.cloud.baowang.play.api.vo.db.config;

import com.cloud.baowang.play.api.api.third.VenueUserAccountApi;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DBCryptoConfig {

    private String KEY;
    private String IV;
    private String aesKey;

    private final VenueUserAccountApi venueUserAccountApi;


    public void init(String venueCode, String merchantNo) {
        VenueInfoVO venueInfoVO = venueUserAccountApi.getVenueInfoByMerchantNo(venueCode, merchantNo);
        this.KEY = venueInfoVO.getAesKey();
        this.IV = venueInfoVO.getMerchantKey();
        log.info("VenueInfoVO : key - " + KEY + " iv - " + IV);

    }

    public String getKey(String venueCode, String merchantNo) {
        init(venueCode, merchantNo);
        return KEY;
    }

    public String getIV(String venueCode, String merchantNo) {
        init(venueCode, merchantNo);
        return IV;
    }

    public String getAesKey(String venueCode) {
       if (StringUtils.isBlank(aesKey)) {
           VenueInfoVO venueInfoVO = venueUserAccountApi.getVenueInfoByVenueCode(venueCode);
           this.aesKey = venueInfoVO.getAesKey();
       }
        return aesKey;
    }

    public String getMerchantkey(String venueCode, String merchantNo) {
        VenueInfoVO venueInfoVO = venueUserAccountApi.getVenueInfoByMerchantNo(venueCode, merchantNo);
        return venueInfoVO.getMerchantKey();
    }

}

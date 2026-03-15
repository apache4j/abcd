package com.cloud.baowang.play.api.third;


import com.cloud.baowang.play.api.api.third.VenueUserAccountApi;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.config.VenueUserAccountConfig;
import com.cloud.baowang.play.service.VenueInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class VenueUserAccountService implements VenueUserAccountApi {

    private final VenueUserAccountConfig venueUserAccountConfig;

    private final VenueInfoService venueInfoService;

    @Override
    public String addVenueUserAccountPrefix(String account) {
        return venueUserAccountConfig.addVenueUserAccountPrefix(account);
    }

    @Override
    public String getVenueUserAccount(String account) {
        return venueUserAccountConfig.getVenueUserAccount(account);
    }

    @Override
    public VenueInfoVO getVenueInfoByMerchantNo(String venueCode,String merchantNo) {
        return venueInfoService.getVenueInfoByMerchantNo(venueCode,merchantNo);
    }

    @Override
    public VenueInfoVO getVenueInfoByVenueCode(String venueCode) {
        return venueInfoService.getVenueInfoByVenueCode(venueCode);
    }

}

package com.cloud.baowang.play.api.api.third;

import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.third.betpull.GamePullReqVO;
import com.cloud.baowang.play.api.vo.third.betpull.ManualGamePullReqVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "venueUserAccountApi", value = ApiConstants.NAME)
@Tag(name = "三方用户ID")
public interface VenueUserAccountApi {
    String PREFIX = ApiConstants.PREFIX + "/third-pull-bet/api/";

    @Operation(summary = "增加场馆前缀")
    @PostMapping(PREFIX + "addVenueUserAccountPrefix")
    String addVenueUserAccountPrefix(@RequestParam("userAccount") String account);


    @Operation(summary = "去除场馆前缀")
    @PostMapping(PREFIX + "getVenueUserAccount")
    String getVenueUserAccount(@RequestParam("userAccount") String account);

    @Operation(summary = "获取场馆")
    @PostMapping(PREFIX + "getVenueInfoByMerchantNo")
    VenueInfoVO getVenueInfoByMerchantNo(@RequestParam("venueCode") String venueCode,@RequestParam("merchantNo") String merchantNo);


    @Operation(summary = "获取场馆")
    @PostMapping(PREFIX + "getVenueInfoByVenueCode")
    VenueInfoVO getVenueInfoByVenueCode(@RequestParam("venueCode") String venueCode);

}


package com.cloud.baowang.play.api.third;

import com.cloud.baowang.play.api.api.third.WpACELTGameApi;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.acelt.*;
import com.cloud.baowang.play.api.vo.base.ACELTBaseRes;
import com.cloud.baowang.play.game.acelt.impl.AceltGameBaseServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
public class WpACELTGameApiImpl implements WpACELTGameApi {

    private final AceltGameBaseServiceImpl aceGameService;

    @Override
    public ACELTBaseRes<ACELTGetBalanceRes> queryBalance(ACELTGetBalanceReq aceltGetBalanceReq) {
        return aceGameService.queryBalance(aceltGetBalanceReq, VenuePlatformConstants.WP_ACELT);
    }

    @Override
    public ACELTBaseRes<ACELTAccountCheangeRes> accountChange(ACELTAccountCheangeReq request) {
        return aceGameService.accountChange(request, VenuePlatformConstants.WP_ACELT);
    }

    @Override
    public ACELTBaseRes<ACELTAccountCheangeRes> accountChangeCallBack(ACELTAccountChangeCallBackReq request) {
        return aceGameService.accountChangeCallBack(request, VenuePlatformConstants.WP_ACELT);
    }
}

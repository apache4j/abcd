package com.cloud.baowang.play.api.third;

import com.cloud.baowang.play.api.api.third.SHGameApi;
import com.cloud.baowang.play.api.vo.base.ShBaseRes;
import com.cloud.baowang.play.api.vo.sh.ShAdjustBalanceReq;
import com.cloud.baowang.play.api.vo.sh.ShAdjustBalanceRes;
import com.cloud.baowang.play.api.vo.sh.ShBalanceRes;
import com.cloud.baowang.play.api.vo.sh.ShQueryBalanceReq;
import com.cloud.baowang.play.game.sh.impl.ShGameServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
public class SHGameApiImpl implements SHGameApi {

    private final ShGameServiceImpl shGameService;

    @Override
    public ShBaseRes<ShBalanceRes> queryBalance(ShQueryBalanceReq request) {
        return shGameService.queryBalance(request);
    }

    @Override
    public ShBaseRes<ShAdjustBalanceRes> adjustBalance(ShAdjustBalanceReq request) {
        return shGameService.adjustBalance(request);
    }
}

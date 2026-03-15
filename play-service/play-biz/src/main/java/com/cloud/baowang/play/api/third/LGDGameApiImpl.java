package com.cloud.baowang.play.api.third;

import com.cloud.baowang.play.api.api.third.LGDGameApi;
import com.cloud.baowang.play.api.vo.ldg.LgdResp;
import com.cloud.baowang.play.api.vo.ldg.RequestVO;
import com.cloud.baowang.play.game.lgd.impl.LgdServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
public class LGDGameApiImpl implements LGDGameApi {

    private final LgdServiceImpl lgdService;

    @Override
    public LgdResp oauth(RequestVO request) {
        return lgdService.oauth(request);
    }

    @Override
    public LgdResp checkBalance(RequestVO request) {
        return lgdService.checkBalance(request);
    }

    @Override
    public LgdResp bet(RequestVO request) {
        return lgdService.bet(request);
    }

    @Override
    public LgdResp errorBet(RequestVO request) {
        return lgdService.errorBet(request);
    }
}

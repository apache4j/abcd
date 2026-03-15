package com.cloud.baowang.play.api.third;

import com.cloud.baowang.play.api.api.third.IMGameApi;
import com.cloud.baowang.play.api.vo.im.ImReq;
import com.cloud.baowang.play.api.vo.im.ImResp;
import com.cloud.baowang.play.game.im.impl.ImServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
public class IMGameApiImpl implements IMGameApi {

    private final ImServiceImpl imService;

    @Override
    public ImResp getBalance(ImReq request) {
        return imService.getBalance(request);
    }

    @Override
    public ImResp writeBet(ImReq request) {
        return imService.writeBet(request);
    }
}

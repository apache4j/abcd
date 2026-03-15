package com.cloud.baowang.play.api.third;

import com.cloud.baowang.play.api.api.third.FastSpinGameApi;
import com.cloud.baowang.play.api.vo.fastSpin.req.FSBalanceReq;
import com.cloud.baowang.play.api.vo.fastSpin.req.FSTransferReq;
import com.cloud.baowang.play.game.fastSpin.impl.FastSpinGamServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
public class FastSpinGameApiImpl implements FastSpinGameApi {

    private final FastSpinGamServiceImpl fseGamService;


    @Override
    public Object getBalance(FSBalanceReq vo, String digest) {
        return fseGamService.getBalance(vo, digest);
    }

    @Override
    public Object transfer(FSTransferReq vo, String digest) {
        return fseGamService.transfer(vo, digest);
    }
}

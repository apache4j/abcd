package com.cloud.baowang.play.api.third;

import com.cloud.baowang.play.api.api.third.SpadeGameApi;
import com.cloud.baowang.play.api.vo.spade.req.SpadeBalanceReq;
import com.cloud.baowang.play.api.vo.spade.req.SpadeTransferReq;
import com.cloud.baowang.play.game.spade.impl.SpadeGamServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
public class SpadeGameApiImpl implements SpadeGameApi {

    private final SpadeGamServiceImpl spadeGamService;

    @Override
    public Object getBalance(SpadeBalanceReq vo) {
        return spadeGamService.getBalance(vo);
    }

    @Override
    public Object transfer(SpadeTransferReq vo) {
        return spadeGamService.transfer(vo);
    }
}

package com.cloud.baowang.play.api.third;


import com.cloud.baowang.play.api.api.third.OMGGameApi;
import com.cloud.baowang.play.api.vo.omg.OmgReq;
import com.cloud.baowang.play.api.vo.omg.OmgResp;
import com.cloud.baowang.play.game.omg.OmgGameService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class OMGGameApiImpl implements OMGGameApi {

    private final OmgGameService omgGameService;

    @Override
    public OmgResp verify(OmgReq req) {
        return omgGameService.verify(req);
    }

    @Override
    public OmgResp getBalance(OmgReq req) {
        return omgGameService.getBalance(req);
    }

    @Override
    public OmgResp changeBalance(OmgReq req) {
        return omgGameService.changeBalance(req);
    }
}

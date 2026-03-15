package com.cloud.baowang.play.api.third;

import com.cloud.baowang.play.api.api.third.TFGameApi;
import com.cloud.baowang.play.api.vo.tf.*;
import com.cloud.baowang.play.game.tf.impl.TFGameServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
public class TFGameApiImpl implements TFGameApi {

    TFGameServiceImpl tfGameService;


    @Override
    public TfValidResp validate(TfValidReq req) {
        return tfGameService.validate(req);
    }

    @Override
    public TfWalletResp wallet(String loginName) {
        return tfGameService.wallet(loginName);
    }

    @Override
    public TfTransferResp transfer(TfTransferReq req) {
        return tfGameService.transfer(req);
    }

    @Override
    public TfTransferResp rollback(TfTransferReq req) {
        return tfGameService.rollback(req);
    }
}

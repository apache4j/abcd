package com.cloud.baowang.play.wallet.service;

import com.cloud.baowang.play.api.vo.fastSpin.req.FSBalanceReq;
import com.cloud.baowang.play.api.vo.fastSpin.req.FSTransferReq;
import com.cloud.baowang.play.api.vo.spade.req.SpadeBalanceReq;
import com.cloud.baowang.play.api.vo.spade.req.SpadeTransferReq;

public interface SpadeService {

    Object getBalance(SpadeBalanceReq vo, String digest);

    Object transfer(SpadeTransferReq vo, String digest);

}

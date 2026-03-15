package com.cloud.baowang.play.wallet.service;


import com.cloud.baowang.play.wallet.vo.req.pt2.PT2ActionVO;
import com.cloud.baowang.play.wallet.vo.req.pt2.PT2BaseVO;
import com.cloud.baowang.play.wallet.vo.req.pt2.vo.NotifyBonusEventVO;
import com.cloud.baowang.play.wallet.vo.req.pt2.vo.settle.GameRoundResultVO;
import com.cloud.baowang.play.wallet.vo.req.pt2.vo.settle.TransferFundsVO;
import com.cloud.baowang.play.wallet.vo.res.pt2.PT2BaseRsp;

public interface PT2GameApi {

    PT2BaseRsp authenticate(PT2ActionVO actionVo);

    PT2BaseRsp bet(PT2ActionVO actionVo);

    PT2BaseRsp gameroundresult(GameRoundResultVO actionVo);

    PT2BaseRsp getbalance(PT2BaseVO actionVo);

    PT2BaseRsp logout(PT2BaseVO actionVo);

    PT2BaseRsp transferFunds(TransferFundsVO actionVo);


}

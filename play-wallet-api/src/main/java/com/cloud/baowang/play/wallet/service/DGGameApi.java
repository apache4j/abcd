package com.cloud.baowang.play.wallet.service;


import com.cloud.baowang.play.wallet.vo.req.dg.DGActionVo;
import com.cloud.baowang.play.wallet.vo.res.dg.DGBaseRsp;


public interface DGGameApi {
    DGBaseRsp getBalance(String agentName, DGActionVo actionVo);

    DGBaseRsp transfer(String agentName, DGActionVo actionVo);

    DGBaseRsp inform(String agentName, DGActionVo actionVo);
}

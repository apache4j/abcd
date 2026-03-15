package com.cloud.baowang.play.wallet.service;


import com.cloud.baowang.play.wallet.vo.req.db.acelt.vo.BalanceQueryVO;
import com.cloud.baowang.play.wallet.vo.req.db.acelt.vo.TransferCheckVO;
import com.cloud.baowang.play.wallet.vo.req.db.acelt.vo.TransferRequestVO;
import com.cloud.baowang.play.wallet.vo.req.db.acelt.vo.TransferRspData;
import com.cloud.baowang.play.wallet.vo.res.db.acelt.DBAceltBaseRsp;
import com.cloud.baowang.play.wallet.vo.res.db.evg.DBEVGBaseRsp;

public interface DBAceltGameApi {


    DBAceltBaseRsp getBalance(BalanceQueryVO reqVO);

    DBAceltBaseRsp upateBalance(TransferRequestVO reqVO);

    DBAceltBaseRsp transfer(TransferCheckVO reqVO);

    TransferRspData safetyTransfer(TransferCheckVO reqVO);
}

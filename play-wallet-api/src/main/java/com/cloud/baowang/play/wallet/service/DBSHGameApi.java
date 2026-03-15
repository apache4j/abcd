package com.cloud.baowang.play.wallet.service;


import com.cloud.baowang.play.wallet.vo.req.db.acelt.vo.TransferRspData;
import com.cloud.baowang.play.wallet.vo.req.db.sh.vo.*;
import com.cloud.baowang.play.wallet.vo.res.db.sh.DBSHBaseRsp;

import java.util.List;

public interface DBSHGameApi {


    DBSHBaseRsp<SHOrderRspData> getBalance(SHBalanceQueryVO reqVO);


    DBSHBaseRsp<List<SHOrderRspData>> getBatchBalance(SHBalanceQueryBatchVO reqVO);

    DBSHBaseRsp<BetRspParams> betConfirm(BetRequestVO reqVO);

    DBSHBaseRsp<BetRspParams> betCancel(DBSHRequestVO reqVO);

    DBSHBaseRsp<BetRspParams> gamePayout(DBSHRequestVO reqVO);

    DBSHBaseRsp<BetRspParams> activityPayout(DBSHRequestVO reqVO);

    DBSHBaseRsp<BetRspParams> playerbetting(DBSHRequestVO reqVO);

    DBSHBaseRsp<BetRspParams> activityRebate(DBSHRequestVO reqVO);
}

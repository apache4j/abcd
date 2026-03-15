package com.cloud.baowang.play.wallet.service;


import com.cloud.baowang.play.wallet.vo.req.db.evg.vo.DBEVGBasicInfo;
import com.cloud.baowang.play.wallet.vo.res.db.evg.DBEVGBaseRsp;


public interface DBFishingGameApi {

    DBEVGBaseRsp queryBalance(DBEVGBasicInfo evgBasicInfo, String req);

    DBEVGBaseRsp balanceChange(DBEVGBasicInfo evgBasicInfo, String req);

    DBEVGBaseRsp queryOrderStatus(DBEVGBasicInfo evgBasicInfo, String req);
}

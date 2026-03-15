package com.cloud.baowang.play.api.third;

import com.cloud.baowang.play.api.api.third.DBAceltGameApi;
import com.cloud.baowang.play.api.api.third.DBSHGameApi;
import com.cloud.baowang.play.api.vo.db.acelt.vo.BalanceQueryVO;
import com.cloud.baowang.play.api.vo.db.acelt.vo.TransferCheckVO;
import com.cloud.baowang.play.api.vo.db.acelt.vo.TransferRequestVO;
import com.cloud.baowang.play.api.vo.db.acelt.vo.TransferRspData;
import com.cloud.baowang.play.api.vo.db.rsp.acelt.DBAceltBaseRsp;
import com.cloud.baowang.play.api.vo.db.rsp.sh.DBSHBaseRsp;
import com.cloud.baowang.play.api.vo.db.sh.vo.*;
import com.cloud.baowang.play.game.db.acelt.DBAceltServiceImpl;
import com.cloud.baowang.play.game.db.sh.DBSHServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@Slf4j
@RestController
public class DBAceltGameApiImpl implements DBAceltGameApi {

    private final DBAceltServiceImpl dbAceltService;


    @Override
    public DBAceltBaseRsp getBalance(BalanceQueryVO reqVO) {
        return dbAceltService.getBalance(reqVO);
    }

    @Override
    public DBAceltBaseRsp upateBalance(TransferRequestVO reqVO) {
        return dbAceltService.upateBalance(reqVO);
    }

    @Override
    public DBAceltBaseRsp transfer(TransferCheckVO reqVO) {
        return dbAceltService.transfer(reqVO);
    }

    @Override
    public TransferRspData safetyTransfer(TransferCheckVO reqVO) {
        return dbAceltService.safetyTransfer(reqVO);
    }
}

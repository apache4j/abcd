package com.cloud.baowang.play.api.sba;

import com.cloud.baowang.play.api.api.order.DBPanDaSportServiceApi;
import com.cloud.baowang.play.api.vo.base.DbPanDaBaseRes;
import com.cloud.baowang.play.api.vo.dbPanDaSport.DbPanDaBalanceReq;
import com.cloud.baowang.play.api.vo.dbPanDaSport.DbPanDaConfirmBetReq;
import com.cloud.baowang.play.api.vo.dbPanDaSport.DbPanDaSportBetReqVO;
import com.cloud.baowang.play.api.vo.dbPanDaSport.DbPanDaSportBetResVO;
import com.cloud.baowang.play.game.dbPandaSport.DbPanDaSportServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@AllArgsConstructor
@RestController
@Service
public class DbPanDaSportServiceApiImpl implements DBPanDaSportServiceApi {

    private final DbPanDaSportServiceImpl dbPanDaSportService;

    @Override
    public DbPanDaBaseRes<String> getBalance(DbPanDaBalanceReq req) {
        return dbPanDaSportService.getBalance(req);
    }

    @Override
    public DbPanDaBaseRes<DbPanDaSportBetResVO> transfer(DbPanDaSportBetReqVO req) {
        return dbPanDaSportService.transfer(req);
    }

    public DbPanDaBaseRes<Void> confirmBet(DbPanDaConfirmBetReq req) {
        return dbPanDaSportService.confirmBet(req);
    }

}

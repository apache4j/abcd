package com.cloud.baowang.play.api.third;

import com.cloud.baowang.play.api.api.third.DbDjGameApi;
import com.cloud.baowang.play.api.vo.dbDj.DBBalanceReq;
import com.cloud.baowang.play.api.vo.dbDj.DBBalanceRes;
import com.cloud.baowang.play.api.vo.dbDj.DbDJTransferReq;
import com.cloud.baowang.play.api.vo.dbDj.DbDJTransferRes;
import com.cloud.baowang.play.game.dbDj.DbDJGameServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
public class DbDjGameApiImpl implements DbDjGameApi {


    @Autowired
    private DbDJGameServiceImpl dbDJGameService;


    @Override
    public DBBalanceRes queryBalance(DBBalanceReq req) {
        return dbDJGameService.queryBalance(req);
    }

    @Override
    public DbDJTransferRes transfer(DbDJTransferReq req) {
        return dbDJGameService.transfer(req);
    }
}

package com.cloud.baowang.play.api.third;

import com.cloud.baowang.play.api.api.third.DBChessGameApi;
import com.cloud.baowang.play.api.api.third.DG2GameApi;
import com.cloud.baowang.play.api.vo.db.evg.vo.DBEVGBasicInfo;
import com.cloud.baowang.play.api.vo.db.rsp.evg.DBEVGBaseRsp;
import com.cloud.baowang.play.api.vo.dg2.req.DGActionVo;
import com.cloud.baowang.play.api.vo.dg2.rsp.DGBaseRsp;
import com.cloud.baowang.play.game.db.chess.DBChessServiceImpl;
import com.cloud.baowang.play.game.dg2.DG2ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
public class DG2GameApiImpl implements DG2GameApi {

    private final DG2ServiceImpl dg2Service;


    @Override
    public DGBaseRsp getBalance(String agentName, DGActionVo actionVo) {
        return dg2Service.getBalance(agentName, actionVo);
    }

    @Override
    public DGBaseRsp transfer(String agentName, DGActionVo actionVo) {
        return dg2Service.transfer(agentName, actionVo);
    }

    @Override
    public DGBaseRsp inform(String agentName, DGActionVo actionVo) {
        return dg2Service.inform(agentName, actionVo);
    }
}

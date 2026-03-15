package com.cloud.baowang.play.api.third;

import com.cloud.baowang.play.api.api.third.WinToEvgApi;
import com.cloud.baowang.play.api.vo.winto.req.WintoBaseVO;
import com.cloud.baowang.play.api.vo.winto.req.WintoTransferVO;
import com.cloud.baowang.play.api.vo.winto.rsp.WinToEvgRsp;
import com.cloud.baowang.play.game.winto.WintoEvgServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
public class WinToEvgApiImpl implements WinToEvgApi {

    private WintoEvgServiceImpl wintoEVGService;

    @Override
    public WinToEvgRsp verifySession(WintoBaseVO req) {
        return wintoEVGService.verifySession(req);
    }

    @Override
    public WinToEvgRsp getBalance(WintoBaseVO req) {
        return wintoEVGService.getBalance(req);
    }

    @Override
    public WinToEvgRsp betTransfer(WintoTransferVO req) {
        return wintoEVGService.betTransfer(req);
    }

    @Override
    public WinToEvgRsp cancelBetTransfer(WintoTransferVO req) {
        return null;
    }

    @Override
    public WinToEvgRsp adjustment(WintoTransferVO actionVo) {
        return null;
    }

    @Override
    public WinToEvgRsp manualBetTransfer(WintoTransferVO req) {
        return wintoEVGService.betTransfer(req);
    }
}

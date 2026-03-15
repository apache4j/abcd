package com.cloud.baowang.play.api.third;

import com.cloud.baowang.play.api.api.third.CmdGameApi;
import com.cloud.baowang.play.api.vo.cmd.CmdReq;
import com.cloud.baowang.play.game.cmd.impl.CmdServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
public class CMDGameApiImpl implements CmdGameApi {

    private final CmdServiceImpl cmdService;

    @Override
    public String doAction(CmdReq cmdReq) {
        return cmdService.doAction(cmdReq);
    }

    @Override
    public String token(String token) {
        return cmdService.token(token);
    }
}

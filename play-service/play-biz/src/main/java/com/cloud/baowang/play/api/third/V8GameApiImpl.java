package com.cloud.baowang.play.api.third;

import com.cloud.baowang.play.api.api.third.V8GameApi;
import com.cloud.baowang.play.api.vo.v8.SeamlesswalletResp;
import com.cloud.baowang.play.game.v8.impl.V8GameServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
public class V8GameApiImpl implements V8GameApi {

    private final V8GameServiceImpl v8GameService;

    @Override
    public SeamlesswalletResp seamlessWallet(HttpServletRequest request) {
        return v8GameService.seamlessWallet(request);
    }
}

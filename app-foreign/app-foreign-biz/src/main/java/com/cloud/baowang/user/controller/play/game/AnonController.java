package com.cloud.baowang.user.controller.play.game;


import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.IPUtil;
import com.cloud.baowang.common.core.utils.LanguageUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.third.ThirdApi;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "匿名接口")
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(value = "/anon/api")
public class AnonController {

    private final ThirdApi thirdApi;


    @Operation(summary = "沙巴体育匿名登陆")
    @PostMapping("/sbaAnonLogin")
    public ResponseVO sbaAnonLogin(HttpServletRequest request) {
        long start = System.currentTimeMillis();
        LoginVO vo = new LoginVO();
        vo.setIp(CurrReqUtils.getReqIp());
        vo.setLanguageCode(LanguageUtils.getLanguageFromRequest(request));
        long end = System.currentTimeMillis();
        ResponseVO responseVO = thirdApi.sbaAnonLogin(vo);
        log.info("匿名登陆时间:{}", end - start);
        return responseVO;
    }

}

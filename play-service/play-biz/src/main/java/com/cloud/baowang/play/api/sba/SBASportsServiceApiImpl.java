package com.cloud.baowang.play.api.sba;

import com.cloud.baowang.play.api.api.order.SBASportServiceApi;
import com.cloud.baowang.play.api.enums.SBActionEnum;
import com.cloud.baowang.play.api.vo.sba.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@AllArgsConstructor
@RestController
@Service
public class SBASportsServiceApiImpl implements SBASportServiceApi {

    private final SBASportContext sbaSportContext;

    @Override
    public String toSBAction(SBBaseReq req, String code) {
        return sbaSportContext.toAction(SBActionEnum.nameOfCode(code), req);
    }
}

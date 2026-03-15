package com.cloud.baowang.play.wallet.controller;

import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.play.api.api.third.LGDGameApi;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.ldg.LgdResp;
import com.cloud.baowang.play.api.vo.ldg.RequestVO;
import com.cloud.baowang.play.wallet.annotations.LogExecution;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@LogExecution
@Slf4j
@RestController
@RequestMapping("callback")
@Tag(name = "LGD单一钱包")
public class LGDController {

//    @Resource
//    private LgdService lgdService;

    @Resource
    private LGDGameApi lgdGameApi;

    @Operation(summary = "单一钱包")
    @PostMapping("/lgd")
    public LgdResp request(@RequestBody RequestVO request) {
        String method = request.getMethod();
        if (StringUtils.isEmpty(method)) {
            return LgdResp.err("4004", "method is empty");
        }
        return switch (method) {
            case "oauth":
                yield lgdGameApi.oauth(request);
            case "checkBalance":
                yield lgdGameApi.checkBalance(request);
            case "bet":
                yield lgdGameApi.bet(request);
            case "errorBet":
                LgdResp resp= lgdGameApi.errorBet(request);
                setErrorOrderNoData(resp,request);
                yield resp;
            default:
                yield LgdResp.err("4004", "method not support");
        };
    }


    private void setErrorOrderNoData(LgdResp resp,RequestVO request){
        if (resp.getCODE().equals("4004")
            || (ObjectUtils.isNotEmpty(resp.getData()) &&
                ObjectUtils.isNotEmpty(resp.getData().getProcessStatus())
                && "2".equals(resp.getData().getProcessStatus()))){
            String key= CacheConstants.ERROR_ORDER_NO+ VenuePlatformConstants.LGD+":"+request.getId();
            RedisUtil.setValue(key,request.getId(), 31L, TimeUnit.DAYS);
        }
    }
}

package com.cloud.baowang.play.api.third;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.play.api.api.third.SexyGameApi;
import com.cloud.baowang.play.api.vo.sexy.enums.SexyErrorEnum;
import com.cloud.baowang.play.api.vo.sexy.req.SexyActionVo;
import com.cloud.baowang.play.api.vo.sexy.rsp.SexyBaseRsp;
import com.cloud.baowang.play.game.sexy.SexyServiceImpl;
import com.cloud.baowang.play.game.sexy.utils.SexyCryptoConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
public class SexyGameApiImpl implements SexyGameApi {

    private final SexyServiceImpl sexyService;
    private SexyCryptoConfig sexyCryptoConfig;

    @Override
    public JSONObject action(SexyActionVo actionVo) {
        String key = sexyCryptoConfig.getKey();
//        log.info("actionVo key : "+actionVo.getKey()+" 配置 key : "+key);
        if (!actionVo.getKey().equals(key)) {
            return JSON.parseObject(JSON.toJSONString(SexyBaseRsp.failed(SexyErrorEnum.INVALID_CERTIFICATE)));
        }
        SexyBaseRsp rsp = sexyService.doAction(actionVo);
        return JSON.parseObject(JSON.toJSONString(rsp));
    }
}

package com.cloud.baowang.play.api.third;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.play.api.api.third.JDBGameApi;
import com.cloud.baowang.play.api.enums.jdb.JDBErrorEnum;
import com.cloud.baowang.play.api.vo.jdb.rsp.JDBBaseRsp;
import com.cloud.baowang.play.game.jdb.JDBGameApiImplExtend;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
public class JDBGameApiImpl implements JDBGameApi {

    private final JDBGameApiImplExtend jdbGameApiExtend;

    @Override
    public JSONObject action(String x) {
        if (StringUtils.isBlank(x)) {
            JDBBaseRsp rsp = JDBBaseRsp.builder().status(JDBErrorEnum.ENCRYPTED_DATA_NULL.getCode()).build();
            return JSON.parseObject(JSON.toJSONString(rsp));
        }

        JDBBaseRsp rsp = jdbGameApiExtend.doAction(x);
        return JSON.parseObject(JSON.toJSONString(rsp));
    }
}

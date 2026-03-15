package com.cloud.baowang.play.api.vo.jili.req;

import com.cloud.baowang.play.api.vo.jili.JILIBaseReq;
import lombok.Data;

import java.util.LinkedHashMap;

@Data
public class JILIBalanceReq extends JILIBaseReq {

    String token;
    // 校验字段是否非空
    public boolean isValid() {
        return super.getTraceId() != null && !super.getTraceId().isEmpty() &&
                token != null && !token.isEmpty() &&
                super.getCurrency() != null && !super.getCurrency().isEmpty() &&
                super.getUsername() != null && !super.getUsername().isEmpty();
    }

    public LinkedHashMap<String, Object> getMap(){

        LinkedHashMap<String, Object> map = new LinkedHashMap<>();

        map.put("traceId", this.getTraceId());
        map.put("username", this.getUsername());
        map.put("currency", this.getCurrency());
        map.put("token", this.getToken());

        return map;

    }
}

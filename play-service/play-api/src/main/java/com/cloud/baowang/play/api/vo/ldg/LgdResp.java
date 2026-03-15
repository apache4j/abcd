package com.cloud.baowang.play.api.vo.ldg;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LgdResp {

    // 执行情况
    @JsonProperty("MSG")
    private String MSG;
    // 编码
    @JsonProperty("CODE")
    private String CODE;
    // 数据
    private LgdDataResp data;

    public static LgdResp success(){
        LgdResp lgdResp = new LgdResp();
        lgdResp.setCODE("0000");
        lgdResp.setMSG("success");
        return lgdResp;
    }

    public static LgdResp err(String code, String msg){
        LgdResp lgdResp = new LgdResp();
        lgdResp.setCODE(code);
        lgdResp.setMSG(msg);
        return lgdResp;
    }

}

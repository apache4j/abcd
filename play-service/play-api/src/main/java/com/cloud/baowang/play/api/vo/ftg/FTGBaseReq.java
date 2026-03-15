package com.cloud.baowang.play.api.vo.ftg;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FTGBaseReq {

    /**
     * 身份验证戳记
     * 由运营商提供给FunTa的用户身份验证戳记。
     */
    private String token;

    /**
     * 请求UUID
     * 标准16字节UUID。此参数在FunTa调用运营商API时必带。
     * 运营商收到此参数后必须按照相同的值回传。
     */
    private String request_uuid;

    /**
     * 用户UID
     * 运营商的用户唯一标识符。长度为3到20个字符。
     */
    private String uid;


    /**
     * 身份识别码
     * 由FunTa提供给代理商/运营商的身份编码。
     */
    private String client_id;


}

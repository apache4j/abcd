package com.cloud.baowang.play.api.vo.zf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZfAuthReq {
    /**
     * 请求的识别唯一代码
     */
    private String reqId;

    /**
     * 运营商api access token (最长 800 字符)
     */
    private String token;

    private String platformCode;
}

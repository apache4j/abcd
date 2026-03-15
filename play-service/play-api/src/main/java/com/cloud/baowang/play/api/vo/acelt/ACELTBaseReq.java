package com.cloud.baowang.play.api.vo.acelt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ACELTBaseReq {

    /**
     * 商户号
     */
    private String operatorId;


    /**
     * MD5签名
     */
    private String sign;

    /**
     * 时间戳
     */
    private Long timestamp;
}

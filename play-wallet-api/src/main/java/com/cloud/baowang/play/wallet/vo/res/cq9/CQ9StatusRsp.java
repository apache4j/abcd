package com.cloud.baowang.play.wallet.vo.res.cq9;

import lombok.Data;

/**
 * @author: lavine
 * @creat: 2023/9/11
 */
@Data
public class CQ9StatusRsp {

    /**
     * 狀態碼
     */
    private Integer code;

    /**
     * 狀態訊息
     */
    private String message;

    /**
     * 回應時間
     */
    private String datetime;

    private String traceCode;
}

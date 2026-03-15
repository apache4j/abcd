package com.cloud.baowang.websocket.api.vo;

import lombok.Data;


@Data
public class WSBaseReq {
    /**
     *
     * @see com.cloud.baowang.websocket.api.enums.WSSubscribeEnum
     */
    private String msgTopic;

    /**
     * 每个请求包具体的数据，类型不同结果不同
     */
    private String data;
}

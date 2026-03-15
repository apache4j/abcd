package com.cloud.baowang.websocket.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class WSBaseResp<T> {
    @Schema(title = "ws 发送数据 主题")
    private String msgTopic;
    @Schema(title = "ws 发送数据")
    private T data;
}

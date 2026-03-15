package com.cloud.baowang.play.api.vo.cq9.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * CQ9基础响应结构类
 *
 * @author: lavine
 * @creat: 2023/9/11 11:50
 */
@Data
public class CQ9BaseRsp<T> {
    @JsonInclude(JsonInclude.Include.ALWAYS)
    @Schema(title = "返回数据对象")
    private T data;

    /**
     * 响应消息内容
     */
    private CQ9StatusRsp status;
}


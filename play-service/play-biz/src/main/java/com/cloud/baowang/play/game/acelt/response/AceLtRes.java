package com.cloud.baowang.play.game.acelt.response;

import lombok.Data;

/**
 * <h2></h2>
 *
 */
@Data
public class AceLtRes<T> {
    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息内容
     */
    private String msg;

    /**
     * 返回对象
     */
    private T data;


}

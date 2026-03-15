package com.cloud.baowang.play.game.v8.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum V8ServiceEnums {

    GETRECORDHANDLE("/getRecordHandle", "查询订单"),
    CHANNELHANDLE("/channelHandle", "登录");
    ;

    /**
     * 接口路径
     */
    private String path;

    /**
     * 描述
     */
    private String name;

}

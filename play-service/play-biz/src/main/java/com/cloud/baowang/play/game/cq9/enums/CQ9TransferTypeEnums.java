package com.cloud.baowang.play.game.cq9.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * CQ9平台的枚举类
 *
 * @author: lavine
 * @creat: 2023/8/31 11:14
 */
@Getter
@AllArgsConstructor
public enum CQ9TransferTypeEnums {
    IN("IN", "转入"),

    OUT("OUT", "转出"),
    ;

    /**
     * 编码
     */
    private String code;

    /**
     * 描述
     */
    private String desc;
}

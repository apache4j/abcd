package com.cloud.baowang.play.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeviceEnums {
    PC("PC", "PC端"),

    H5("H5", "手机端");

    /**
     * 编码
     */
    private String code;

    /**
     * 描述
     */
    private String desc;
}

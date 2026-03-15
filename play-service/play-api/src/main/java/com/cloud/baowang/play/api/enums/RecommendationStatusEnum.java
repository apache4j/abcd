package com.cloud.baowang.play.api.enums;

import lombok.Getter;

@Getter
public enum RecommendationStatusEnum {

    NOT_RECOMMENDED("未推荐", 0),  // 未推荐

    RECOMMENDED("推荐", 1);        // 推荐

    private final String name;  // 名称
    private final Integer code;     // 代码

    // 构造函数
    RecommendationStatusEnum(String name, int code) {
        this.name = name;
        this.code = code;
    }

}

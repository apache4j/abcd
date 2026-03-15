package com.cloud.baowang.play.api.enums.venue;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 此处修改或新增 一定要通知调整佣金方案有效流水配置与计算 切记切记!!!!!by ford 2025/03/04
 * 修改此处会影响返水生成逻辑 切记切记!!!!!by ford 2025/10/04
 */
@Getter
public enum VenueTypeEnum {
    SPORTS(1, "体育"),
    SH(2, "视讯"),
    CHESS(3, "棋牌"),
    ELECTRONICS(4, "电子"),
    ACELT(5, "彩票"),
    COCKFIGHTING(6, "斗鸡"),
    ELECTRONIC_SPORTS(7, "电竞"),
    FISHING(8, "捕鱼"),
    MARBLES(9, "娱乐"),
    ;

    private final Integer code;
    private final String name;

    VenueTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static VenueTypeEnum of(Integer code) {
        if (null == code) {
            return null;
        }
        VenueTypeEnum[] types = VenueTypeEnum.values();
        for (VenueTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static String nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        VenueTypeEnum[] types = VenueTypeEnum.values();
        for (VenueTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type.getName();
            }
        }
        return null;
    }

    public static List<VenueTypeEnum> getList() {
        return Arrays.asList(values());
    }
}

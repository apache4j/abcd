package com.cloud.baowang.play.game.sh.enums;

import lombok.Getter;

@Getter
public enum SHGameTypeEnum {
    JI_SU_BAI_CAI(1, "极速百家乐"),
    JING_DIAN_BAI_CAI(2, "经典百家乐"),
    NIU_NIU(3, "牛牛"),
    LONG_HU(4, "龙虎"),
    SAN_GONG(5, "三公"),
    LONG_FENG_ZHA_JIN_HU(6, "龙凤炸金花"),
    YIN_DU_ZHA_JIN_HU(7, "印度炸金花"),
    DE_ZHOU_POKER(8, "德州扑克"),
    ZHU_BO_BAI_CAI(9, "特色百家乐"),
    GONG_MI_BAI_CAI(10, "共咪百家乐"),
    JING_MI_BAI_CAI(11, "竞咪百家乐"),
    ZHA_JIN_HUA(12, "炸金花"),
    JING_WU_BAI_CAI(13, "劲舞百家乐"),
    SE_DI(14, "色碟"),
    LUN_PAN(15, "轮盘"),
    FAN_TAN(16, "番摊"),
    DICE_BAO(17, "骰宝"),
    DIAN_21(18, "21点"),
    AB_ANDAR(19, "安达巴哈"),
    TSLH(20, "特色龙虎"),
    YNYXX(21,"越南鱼虾蟹"),
    SDLH(22,"闪电龙虎");

    private final Integer code;
    private final String name;

    SHGameTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }


}

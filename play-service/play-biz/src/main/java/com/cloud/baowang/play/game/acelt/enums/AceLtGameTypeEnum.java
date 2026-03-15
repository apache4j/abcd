package com.cloud.baowang.play.game.acelt.enums;


import lombok.Getter;


/**
 * 彩种分类
 */
@Getter
public enum AceLtGameTypeEnum {

    // 定义彩种分类
    LUCKY_28("_28", "幸运28"),
    K3("K3", "快三"),
    PK10("PK10", "PK10"),
    SSC("SSC", "时时彩"),
    LHC("LHC", "六合彩"),
    _3D("_3D", "3D"),
    SYXW("SYXW", "11选5"),
    SSQ("SSQ", "双色球"),
    NVN("NVN", "北部越南彩"),
    CVN("CVN", "中部越南彩"),
    SVN("SVN", "南部越南彩"),
    W4D("W4D", "万字4D");
    // 成员变量
    private final String code;  // 彩种代码
    private final String name;  // 彩种名称

    // 构造方法
    AceLtGameTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}

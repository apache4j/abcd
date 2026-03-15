package com.cloud.baowang.play.game.sa;


import lombok.Getter;

@Getter
public enum SAPokdengResultEnum {
    PDR_BankerPoint("PDR_BankerPoint", "庄总点数", Integer.class),
    PDR_BankerPair("PDR_BankerPair", "庄对", Boolean.class),

    PDR_Player1Point("PDR_Player1Point", "闲1 总点数", Integer.class),
    PDR_Player1Result("PDR_Player1Result", "闲1 结果状态", Integer.class),
    PDR_Player1Pair("PDR_Player1Pair", "闲1 对", Boolean.class),

    PDR_Player2Point("PDR_Player2Point", "闲2 总点数", Integer.class),
    PDR_Player2Result("PDR_Player2Result", "闲2 结果状态", Integer.class),
    PDR_Player2Pair("PDR_Player2Pair", "闲2 对", Boolean.class),

    PDR_Player3Point("PDR_Player3Point", "闲3 总点数", Integer.class),
    PDR_Player3Result("PDR_Player3Result", "闲3 结果状态", Integer.class),
    PDR_Player3Pair("PDR_Player3Pair", "闲3 对", Boolean.class),

    PDR_Player4Point("PDR_Player4Point", "闲4 总点数", Integer.class),
    PDR_Player4Result("PDR_Player4Result", "闲4 结果状态", Integer.class),
    PDR_Player4Pair("PDR_Player4Pair", "闲4 对", Boolean.class),

    PDR_Player5Point("PDR_Player5Point", "闲5 总点数", Integer.class),
    PDR_Player5Result("PDR_Player5Result", "闲5 结果状态", Integer.class),
    PDR_Player5Pair("PDR_Player5Pair", "闲5 对", Boolean.class);



    private final String code;
    private final String name;
    private final Class<?> type;

    SAPokdengResultEnum(String code, String name, Class<?> type) {
        this.code = code;
        this.name = name;
        this.type = type;
    }

}

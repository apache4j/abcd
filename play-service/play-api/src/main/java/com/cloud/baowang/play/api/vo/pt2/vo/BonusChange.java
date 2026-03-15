package com.cloud.baowang.play.api.vo.pt2.vo;


import lombok.Data;

@Data
public class BonusChange {
    private String remoteBonusCode;
    private String bonusInstanceCode;

    private FreeSpinChange freeSpinChange;

    private GoldenChipsChange goldenChipsChange;

    private String bonusTemplateId;
}

package com.cloud.baowang.play.game.cmd.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum CmdOddTypeEnum {
        MY("MY", "马来盘", "Malaysian Odds"),
        ID("ID", "印度尼西亚盘", "Indonesian Odds"),
        HK("HK", "香港盘", "Hong Kong Odds"),
        DE("DE", "欧洲盘", "European Odds"),
        US("US", "美国盘", "American Odds");
    ;
    private String code;
    private String descCn;
    private String descEn;

    public static CmdOddTypeEnum of(String code) {
        if (ObjectUtil.isEmpty(code)) {
            return null;
        }
        for (CmdOddTypeEnum obj : CmdOddTypeEnum.values()) {
            if (Objects.equals(obj.getCode(), code)) {
                return obj;
            }
        }
        return null;
    }
}

package com.cloud.baowang.play.game.cmd.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum CmdTypeEnum {
    type0("0","队名"),
    type1("1","联赛名"),
    type2("2","特别投注名"),
    type3("3","体育项目"),
    ;

    ;
    private String code;
    private String desc;

    public static CmdTypeEnum of(String code) {
        if (ObjectUtil.isEmpty(code)) {
            return null;
        }
        for (CmdTypeEnum obj : CmdTypeEnum.values()) {
            if (Objects.equals(obj.getCode(), code)) {
                return obj;
            }
        }
        return null;
    }


}

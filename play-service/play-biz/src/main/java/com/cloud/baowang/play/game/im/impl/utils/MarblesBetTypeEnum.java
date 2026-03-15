package com.cloud.baowang.play.game.im.impl.utils;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.play.game.cmd.enums.CmdBetInfoCNEnum;
import com.cloud.baowang.play.game.cmd.enums.CmdBetInfoEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum MarblesBetTypeEnum {

    SBA_BET_TYPE_1("大于", "greater than"),
    SBA_BET_TYPE_2("小于", "less than");
    private String code;
    private String desc;

    public static String of(String code,String lang) {
        if (ObjectUtil.isEmpty(code)) {
            return null;
        }
        code=code.trim();
        for (MarblesBetTypeEnum obj : MarblesBetTypeEnum.values()) {
            if (Objects.equals(obj.getCode(), code)) {
                if (LanguageEnum.ZH_CN.getLang().equals(lang)){
                    return obj.code;
                }else{
                    return obj.desc;
                }

            }
        }
        return code;
    }
}

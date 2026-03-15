package com.cloud.baowang.play.game.zf.openApi.enums;

import com.cloud.baowang.play.api.enums.ClassifyEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum JILIThreeOrderStatusEnums {

    NOT_SETTLE(0, "未结算",ClassifyEnum.NOT_SETTLE),
    SETTLED(1, "已结算", ClassifyEnum.SETTLED),
    CANCEL(2, "已取消", ClassifyEnum.CANCEL),
    RESETTLED(3, "退款(按取消处理)", ClassifyEnum.CANCEL),

    ABERRANT(17, "异常(未知状态)", ClassifyEnum.NOT_SETTLE);
    ;

    int code;
    String desc;
    ClassifyEnum classifyEnum;

    public static ClassifyEnum getClassifyEnumByCode(Object code) {
        if (code!=null){
            for (JILIThreeOrderStatusEnums status : JILIThreeOrderStatusEnums.values()) {
                if (String.valueOf(status.code).equals(code.toString())) {
                    return status.classifyEnum;
                }
            }
        }
        return null; // 异常
    }
}

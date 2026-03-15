package com.cloud.baowang.common.excel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
@Getter
public enum ExcelTemplateFileNameEnum {
    USER_MANUAL_TEMPLATE("会员账号"),
    AGENT_MANUAL_TEMPLATE("代理账号"),

    USER_AGENT_TEMPLATE("会员或代理或商务账号"),

    USER_FREE_GAME("会员旋转配置"),

    USER_MANUAL_UP("会员人工加额"),
    USER_MANUAL_DOWN("会员人工减额"),
    PLATFORM_MANUAL_UP("会员平台币上分"),
    PLATFORM_MANUAL_DOWN("会员平台币下分"),

    AGENT_MANUAL_UP("代理人工加额"),
    AGENT_MANUAL_DOWN("代理人工减额"),
    ;
    private final String fileName;

    public static ExcelTemplateFileNameEnum getFileNameByCode(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return null;
        }
        ExcelTemplateFileNameEnum[] types = ExcelTemplateFileNameEnum.values();
        for (ExcelTemplateFileNameEnum type : types) {
            if (fileName.equals(type.getFileName())) {
                return type;
            }
        }
        return null;
    }
}

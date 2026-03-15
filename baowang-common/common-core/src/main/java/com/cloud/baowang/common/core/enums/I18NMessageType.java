package com.cloud.baowang.common.core.enums;

public enum I18NMessageType {

    PROMPT("PROMPT"),
    LOOKUP("LOOKUP"),
    EXCEL("EXCEL"),
    BUSINESS("BIZ");

    private final String code;

    I18NMessageType(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

package com.cloud.baowang.system.api.enums.tutorial;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum TutorialGeneralEnum {

    TUTORIAL_CATEGORY(0, "tutorialCategory"),
    TUTORIAL_CLASS(1, "tutorialClass"),
    TUTORIAL_TABS(2, "tutorialTabs"),
    TUTORIAL_CONTENT(3, "tutorialContent"),
    CHANGE_CATALOG(4, "changeCatalog"),
    CHANGE_TYPE(5, "changeType"),


    ;

    private Integer code;

    private String name;

    TutorialGeneralEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static TutorialGeneralEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        TutorialGeneralEnum[] types = TutorialGeneralEnum.values();
        for (TutorialGeneralEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<TutorialGeneralEnum> getList() {
        return Arrays.asList(values());
    }
}

package com.cloud.baowang.play.game.ace.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ACEErrorEnum {


    CODE_0("0", "Request was successfully processed."),
    CODE_1("1", "Authentication failed. Incorrect secure login and secure password combination."),
    CODE_2("2", "Validation failed. Empty mandatory field '<field name>'."),
    CODE_3("3", "Game(s) are not supported: <list of the game id>."),
    CODE_4("4", "Invalid player account."),
    CODE_5("5", "Top up not allowed."),
    CODE_6("6", "Withdraw not allowed."),
    CODE_7("7", "Input length exceeded allowable length."),
    CODE_8("8", "The currency '< currency code >' is incorrect or unsupported."),
    CODE_12("12", "The currency '<currency code>' not allowed for topup."),
    CODE_13("13", "Invalid request format."),
    CODE_14("14", "Player username has been registered."),
    CODE_15("15", "accountID field cannot be empty."),
    CODE_16("16", "Currency field cannot be empty."),
    CODE_17("17", "Nickname field cannot be empty."),


    CODE_99("99", "Unknown Error."),

    ;

    private final String code;
    private final String desc;

    public static ACEErrorEnum fromCode(String code) {
        for (ACEErrorEnum value : ACEErrorEnum.values()) {
            if (value.code.equals(code) ) {
                return value;
            }
        }
        return CODE_99;
    }


}

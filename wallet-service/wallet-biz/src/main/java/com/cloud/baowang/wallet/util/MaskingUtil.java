package com.cloud.baowang.wallet.util;

import com.cloud.baowang.common.core.utils.StringUtil;

public class MaskingUtil {

    public static String maskBankCard(String cardNumber, boolean isTuoMing) {
        if (!isTuoMing){
            return cardNumber;
        }
        if (cardNumber == null || cardNumber.length() < 7) {
            return "";
        }
        return cardNumber.substring(0, 3) + "*".repeat(cardNumber.length() - 7) + cardNumber.substring(cardNumber.length() - 4);
    }

    public static String maskBankName(String cardName) {
        if (cardName == null || cardName.length() < 2) {
            return "";
        }
        return cardName.charAt(0) + "*".repeat(cardName.length() - 2) + cardName.substring(cardName.length() - 1);
    }

    public static String maskSurName(String surName) {
        if (surName==null || surName.isEmpty()) {
            return "";
        }
        return surName.charAt(0) + "*".repeat(surName.length() - 1);
    }


    public static String maskUserAccount(String string, boolean isTuoMing) {
        if (!isTuoMing){
            return string;
        }
        if (string==null || string.length() < 3) {
            return "";
        }
        return string.substring(0, 3) + "*".repeat(string.length());
    }

    public static String maskAddressNo(String string, boolean isTuoMing) {
        if (!isTuoMing){
            return string;
        }
        if (string==null || string.length() < 6) {
            return "";
        }
        return string.substring(0, 3) + "*".repeat(string.length() - 6) + string.substring(string.length() - 3);
    }




}

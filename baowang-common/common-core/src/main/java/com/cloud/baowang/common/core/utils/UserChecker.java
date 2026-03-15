package com.cloud.baowang.common.core.utils;


import com.cloud.baowang.common.core.enums.RegexEnum;

/**
 * 会员验证
 */
public class UserChecker {

    public static boolean checkUserAccount(String userAccount) {
        char firstChar = userAccount.charAt(0);
        if (!Character.isLetter(firstChar)) {
            // 首位字母
            return true;
        }

        int letterAmount = 0;
        for (char c : userAccount.toCharArray()) {
            if (Character.isLetter(c)) {
                letterAmount += 1;
            }
        }
        if (letterAmount < 2) {
            // 最少2个字母
            return true;
        }

        String regex = RegexEnum.ACCOUNT.getRegex();
        boolean userAccountRegex = userAccount.matches(regex);
        // 不规范
        return !userAccountRegex;
    }

    public static boolean checkPassword(String password) {
        String regex = RegexEnum.PASSWORD8.getRegex();
        boolean passwordRegex = password.matches(regex);
        // 不规范
        return !passwordRegex;
    }

    public static boolean checkPhone(String phone) {
        // 长度验证
        //int length = phone.length();
        /*if (length != 11) {
            // 长度不规范
            return true;
        }*/
        String regex = RegexEnum.PHONE_OVER_SEA_ONLY.getRegex();
        boolean phoneRegex = phone.matches(regex);
        // 不规范
        return !phoneRegex;
    }

    public static boolean checkEmail(String email) {
        String regex = RegexEnum.EMAIL.getRegex();
        boolean emailRegex = email.matches(regex);
        // 不规范
        return !emailRegex;
    }

    public static boolean checkCharator32(String email) {
        String regex = RegexEnum.CHARATOR32.getRegex();
        boolean emailRegex = email.matches(regex);
        // 不规范
        return !emailRegex;
    }

    public static boolean checkCode(String code) {
        return code.length() != 6;
    }

    public static boolean checkIp(String ip) {
        String regex = RegexEnum.IP.getRegex();
        boolean ipRegex = ip.matches(regex);
        // 不规范
        return !ipRegex;
    }

    public static boolean checkPayPassword(String payPassword) {
        return payPassword.matches("[0-9]{6}");
    }

    public static boolean checkWithdrawPwd(String password) {
        String regex = RegexEnum.WITHDRAW_PWD.getRegex();
        return password.matches(regex);
    }
}

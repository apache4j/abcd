package com.cloud.baowang.common.core.utils;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

/**
 * @Author : 小智
 * @Date : 16/1/24 10:50 AM
 * 手机号码：
 * 邮箱：
 * 邮箱隐藏中间字符。显示前2个字符+*+@后的字符，
 * 例如：
 * abcd124521@163.com
 * abcd@163.com
 * 银行账号：
 * 保留前4与后4
 * <p>
 * 脱敏权限补充
 * 1.脱敏权限分为脱敏手机号、邮箱、银行卡这三个信息
 * 2.如果配置了脱敏的管理员显示眼睛这个按键，后端如果是明文返回，前端显示眼睛，如果是密文前端不显示眼睛
 * 3.手机号码：
 * 手机号码长度：
 * 长度大于等于8.保留前3后3
 * 长度小于8.保留前2后2.
 * 4.邮箱：
 * 邮箱隐藏中间字符。显示前2个字符+*+@后的字符，
 * 例如：
 * abcd124521@163.com
 * ab********@163.com
 * 5.银行账号：
 * 保留前4与后4，中间字符*隐藏
 * 6.虚拟币地址显示前4后4，中间字符*隐藏
 * 7.电子钱包保留前3与后3，中间字符*隐藏
 * @Version : 1.0
 */
public class SymbolUtil {
    /**
     * 根据手机号码长度进行脱敏
     * 支持区号和手机号码用逗号隔开
     * 手机号码长度：
     * - 长度大于等于8，保留前3后3
     * - 长度介于5到7，保留前2后2
     * - 长度小于5，保留前1后1（或全部字符）
     *
     * @param phone 手机号码（可以包含区号）
     * @return 脱敏后的手机号码
     */
    public static String showPhone(String phone) {
        if (StringUtils.isEmpty(phone) || "null".equalsIgnoreCase(phone)) {
            return ""; // 当输入为空或字符串"null"时返回空字符串
        }

        // 按逗号分割区号和手机号码
        String[] parts = phone.split(",");
        StringBuilder result = new StringBuilder();

        // 添加区号（不脱敏）
        if (parts.length > 0) {
            result.append(parts[0].trim()); // 添加区号
        }

        // 处理手机号码部分
        if (parts.length > 1) {
            String phoneNumber = parts[1].trim();
            if (result.length() > 0) {
                result.append(","); // 添加逗号和空格分隔
            }
            result.append(maskPhoneNumber(phoneNumber));
        } else if (parts.length == 1) {
            // 如果没有区号，则处理整个字符串作为电话号码
            result = new StringBuilder();
            result.append(maskPhoneNumber(parts[0].trim()));
        }

        return result.toString().trim();
    }

    /**
     * 对输入字符串进行脱敏处理，保留首尾字符，中间使用3个星号替代。
     *
     * @param input 需要进行脱敏处理的字符串
     * @return 脱敏后的字符串
     */
    public static String desensitize(String input) {
        // 如果输入为空或长度小于等于2，则不进行处理，直接返回原字符串
        if (input == null || input.length() <= 2) {
            return input;
        }

        // 获取字符串的首字符
        char firstChar = input.charAt(0);
        // 获取字符串的尾字符
        char lastChar = input.charAt(input.length() - 1);

        // 构造脱敏后的字符串，首字符 + "***" + 尾字符
        return firstChar + "***" + lastChar;
    }

    /**
     * 对手机号码进行脱敏处理
     * - 长度大于等于8，保留前3位和后3位
     * - 长度介于5到7，保留前2位和后2位
     * - 长度小于5，保留前1位和后1位
     *
     * @param phoneNumber 手机号码
     * @return 脱敏后的手机号码
     */
    private static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() >= 8) {
            // 长度大于等于8，保留前3位和后3位
            String before = phoneNumber.substring(0, 3);
            String after = phoneNumber.substring(phoneNumber.length() - 3);
            return before + "*".repeat(phoneNumber.length() - 6) + after;
        } else if (phoneNumber.length() >= 5) {
            // 长度介于5到7，保留前2位和后2位
            String before = phoneNumber.substring(0, 2);
            String after = phoneNumber.substring(phoneNumber.length() - 2);
            return before + "*".repeat(phoneNumber.length() - 4) + after;
        } else if (phoneNumber.length() > 2) {
            // 长度小于5但大于2，保留前1位和后1位
            String before = phoneNumber.substring(0, 1);
            String after = phoneNumber.substring(phoneNumber.length() - 1);
            return before + "*".repeat(phoneNumber.length() - 2) + after;
        } else {
            // 长度小于等于2，直接返回
            return phoneNumber;
        }
    }

    /**
     * 邮箱隐藏中间字符，显示前2个字符 + "****" + "@" 后的字符。
     * 例如：
     * abcd124521@163.com => ab****@163.com
     * abcd@163.com => ab****@163.com
     *
     * @param email 待处理的邮箱地址
     * @return 脱敏后的邮箱地址
     */
    public static String showEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return "";
        }
        email = email.trim();
        int atIndex = email.indexOf("@");
        // 如果没有 "@" 或者 "@" 前少于2个字符，直接返回原始邮箱
        if (atIndex == -1 || atIndex < 2) {
            return email;
        }
        // 前2个字符
        String before = email.substring(0, 2);
        // "@"及后面的部分
        String after = email.substring(atIndex);
        // 拼接脱敏后的邮箱
        return before + "****" + after;
    }


    /**
     * 用户账号隐藏中间字符
     *
     * @param userAccount
     * @return
     */
    public static String showUserAccount(String userAccount) {
        if (StringUtils.isEmpty(userAccount)) {
            return "";
        }
        if (userAccount.length() <= 3) {
            return userAccount;
        }
        String after = userAccount.substring(userAccount.length() - 3);
        return "*".repeat(userAccount.length() - 3) + after;
    }

    public static String showUserAccount(String userAccount, boolean isMask) {
        if (!isMask) {
            return userAccount;
        }
        return showUserAccount(userAccount);
    }


    /**
     * 保留前4位和后四位，适用于虚拟货币，
     *
     * @return
     */
    public static String showBankOrVirtualNo(String bankNo) {
        if (StringUtils.isEmpty(bankNo)) {
            return "";
        }
        if (bankNo.length() < 4) {
            return bankNo;
        }
        String before = bankNo.substring(0, 4);
        String after = bankNo.substring(bankNo.length() - 4);
        return before + "********" + after;
    }

    public static String showBankOrVirtualNo(String bankNo, boolean isMask) {
        if (!isMask) {
            return bankNo;
        }
        return showBankOrVirtualNo(bankNo);
    }

    /**
     * 保留后3位，适用于电子钱包账号
     *
     * @param walletNo 电子钱包账号
     * @return 格式化后的账号，前面部分用星号代替，只保留后三位
     */
    public static String showWalletNo(String walletNo) {
        if (StringUtils.isEmpty(walletNo)) {
            return "";
        }
        if (walletNo.length() <= 3) {
            return walletNo;
        }
        String lastThree = walletNo.substring(walletNo.length() - 3);
        return "********" + lastThree;
    }



    /**
     * 保留前1位，姓名
     * 姓名脱敏：统一显示为 姓+**
     *
     * @param name 姓名
     * @return 脱敏后的姓名
     */
    public static String showUserName(String name) {
        if (StringUtils.isBlank(name)) {
            return "";
        }
        return name.charAt(0) + "**";
    }

    public static String showUserName(String name, boolean isMask) {
        if (!isMask) {
            return name;
        }
        return showUserName(name);
    }

    /**
     * 人工和电子钱包账户, 保留后三位
     */
    public static String showLastThree(String stringAccount) {
        if (StringUtils.isEmpty(stringAccount)) {
            return "";
        }
        if (stringAccount.length() <= 3) {
            return stringAccount;
        }
        String lastThree = stringAccount.substring(stringAccount.length() - 3);
        return "*".repeat(stringAccount.length() - 3) + lastThree;
    }

    /**
     * 人工和电子钱包账户, 保留前后2位
     */
    public static String showBankName(String bankName) {
        if (StringUtils.isEmpty(bankName)) {
            return "";
        }
        if (bankName.length() <= 3) {
            return bankName;
        }
        String firstTwo = bankName.substring(0, 1);
        String lastTwo = bankName.substring(bankName.length() - 1);
        return firstTwo + "*".repeat(bankName.length() - 2) + lastTwo;
    }

    public static String showBankName(String name, boolean isMask) {
        if (!isMask) {
            return name;
        }
        return showBankName(name);
    }


    public static void main(String[] args) {

        System.out.println(showBankName("1234"));

        /*System.out.println(showPhone(String.valueOf("null")));
        System.out.println(showPhone("86,15814251112"));//期望86,158*****112
        System.out.println(showPhone("86,26511"));//期望86,1226511
        System.out.println(showPhone("86,1226511"));//期望86,12***11
        System.out.println(showPhone("86,122611"));//期望86,122**11
        System.out.println(showPhone("86,12211"));//期望86,122*11
        System.out.println(showPhone("86,1211"));//期望86,1**1
        System.out.println(showPhone("86,121"));//期望86,1*1
        System.out.println(showPhone("86,11"));//期望86,11
        System.out.println(showPhone("15814243718"));//期望158*****718
        System.out.println(showPhone("1581424718"));//期望158****718
        System.out.println(showPhone("1581718"));//期望15***18
        System.out.println(showPhone("158718"));//期望15**18
        System.out.println(showPhone("15818"));//期望15*18
        System.out.println(showPhone("1518"));//期望1**8
        System.out.println(showPhone("151"));//期望1*1
        System.out.println(showPhone("15"));//期望15*/
    }
}

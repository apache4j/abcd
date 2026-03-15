package com.cloud.baowang.common.core.utils;

import java.nio.charset.Charset;
import java.util.Random;

public class StringUtil {

    /**
     * 生成长度为n的随机字符串
     *
     * @param n
     * @return
     */
    public static String createCharacter(int n) {
        char[] codeSeq = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j',
                'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J',
                'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y', 'Z',  '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        String value = null;

        for (; ; ) {
            Random random = new Random();
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < n; i++) {
                String r = String.valueOf(codeSeq[random.nextInt(codeSeq.length)]);//random.nextInt(10));
                s.append(r);
            }

            return s.toString();
        }
    }

    public static String createNumber(int n) {
        char[] codeSeq = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        String value = null;

        for (; ; ) {
            Random random = new Random();
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < n; i++) {
                String r = String.valueOf(codeSeq[random.nextInt(codeSeq.length)]);//random.nextInt(10));
                s.append(r);
            }

            return s.toString();
        }
    }

    public static String currencyToHex(String currencySymbol) {
        return String.format("%04x", (int) currencySymbol.charAt(0));
    }

    public static String currencyHexToString(String currencyHex) {
        char currencySymbol=(char)Integer.parseInt(currencyHex, 16);
        return Character.toString(currencySymbol);
    }

    public static String getVersion(String userAgent) {
        if (userAgent == null) {
            return "unknown";
        }
        String browserName = "";
        String fullVersion = "";
        String version = "";
        try {
            if (userAgent.contains("Chrome")) {
                browserName = "Google Chrome";
                fullVersion = userAgent.substring(userAgent.indexOf("Chrome") + 7);
                version = fullVersion.split(" ")[0].split("\\.")[0];
            } else if (userAgent.contains("Firefox")) {
                browserName = "Mozilla Firefox";
                fullVersion = userAgent.substring(userAgent.indexOf("Firefox") + 8);
                version = fullVersion.split(" ")[0];
            } else if (userAgent.contains("MSIE")) {
                browserName = "Microsoft Internet Explorer";
                version = userAgent.substring(userAgent.indexOf("MSIE") + 5);
            } else if (userAgent.contains("Edg")) {
                browserName = "Microsoft Edge";
                fullVersion = userAgent.substring(userAgent.indexOf("Edg") + 4);
                version = fullVersion.split(" ")[0].split("\\.")[0];
            } else if (userAgent.contains("Safari")) {
                browserName = "Apple Safari";
                fullVersion = userAgent.substring(userAgent.indexOf("Safari") + 7);
                if (userAgent.contains("Version")) {
                    fullVersion = userAgent.substring(userAgent.indexOf("Version") + 8);
                }
                version = fullVersion.split(" ")[0];
            } else {
                browserName = "unknown";
                version = "0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return browserName+ " " + version;
    }


 /*   public static void main(String[] args) {
        System.err.println(currencyToHex('₵'));
        System.err.println(currencyHexToString("20B5"));
    }*/
}

package com.cloud.baowang.play.game.db.vo;

import java.util.Random;

public class Md5StringUtils {
    public static String mix(String md5Str) {
        String randStr = genRandStr();
//        String randStr = "ShyAQHp3";
        StringBuilder sb = new StringBuilder();

        // 前 2 个
        sb.append(randStr.charAt(0)).append(randStr.charAt(1));
        sb.append(md5Str, 0, 9);

        // 第 9 个字符后插 2 个
        sb.append(randStr.charAt(2)).append(randStr.charAt(3));
        sb.append(md5Str, 9, 17);

        // 第 17 个字符后插 2 个
        sb.append(randStr.charAt(4)).append(randStr.charAt(5));
        sb.append(md5Str.substring(17));

        // 尾部 2 个
        sb.append(randStr.charAt(6)).append(randStr.charAt(7));

        return sb.toString();

    }

    public static String genRandStr() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}

package com.cloud.baowang.user.util;

import com.cloud.baowang.common.core.utils.DateUtils;

import java.util.Date;
import java.util.Random;

/**
 * @author: fangfei
 * @createTime: 2024/03/31 10:20
 * @description:
 */
public class NumberUtil {
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

    public static String createUserId() {
        String id = DateUtils.dateToyyyyMMddHHmmssSSS(new Date()).substring(12);
        return createCharacter(3) + createNumber(2) + id;
    }
}

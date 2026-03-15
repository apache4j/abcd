package com.cloud.baowang.common.core.utils;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class RandomStringUtil {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();
    private static final AtomicLong counter = new AtomicLong(System.currentTimeMillis() % 1000000000);

    public static String generateRandomString(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(randomIndex));
        }
        return stringBuilder.toString();
    }

    public static String getRandomFirstEN(int length){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.substring(0, 51).length())));
        for(int i = 0; i < length - 1; i++){
            int randomIndex = random.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(randomIndex));
        }
        return stringBuilder.toString();
    }


    public static String generateUniqueId() {
        // Increment and get the next value from the atomic counter
        long uniqueNumber = counter.getAndIncrement();

        // Ensure the unique number fits into 10 digits
        uniqueNumber = uniqueNumber % 10000000000L; // Limit to 10 digits

        // Format the number to ensure it is exactly 10 digits (pad with leading zeros if necessary)

        return String.format("%010d", uniqueNumber);
    }
    /**
     * 获取区间的随机整数
     * @param min 最小
     * @param max 最大
     * @return 随机数
     */
    public static Integer getIntervalIntegerRandom(int min,int max){
        Random rand = new Random();
        return rand.nextInt(max - min + 1) + min;
    }

    /**
     * 获取区间的随机小数
     * @param min 最小
     * @param max 最大
     * @return 随机数
     */
    public static double getIntervalDoubleRandom(double min,double max){
        Random rand = new Random();
        double randomNumber = min + (max - min) * rand.nextDouble();

        // 保留两位小数
        return Math.round(randomNumber * 100.0) / 100.0;
    }


    /**
     * 随机生成长度范围的字符串,字母数字拼接
     */
    public static String getCharacters(int minLength,int maxLength){
        Random rand = new Random();
        int length = minLength + rand.nextInt(maxLength - minLength + 1);
        return generateRandomString(length);
    }


    public static void main(String[] args) {
        for (int i=0;i<100;i++){
            System.err.println(getCharacters(5,16));
        }
    }





}

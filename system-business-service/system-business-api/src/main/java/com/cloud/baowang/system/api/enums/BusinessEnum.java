package com.cloud.baowang.system.api.enums;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

@Getter
public enum BusinessEnum {
    INVITE_CODE("7","合营部ID"),
    SKYPE("8", "skype"),
    TELEGRAM("9", "telegram"),
    JOIN_US_OTHER("10", "加入我们"),
    JOIN_US_PC("11", "加入我们"),
    H5_ADDRESS("12", "H5网页访问地址"),

    MINIO_DOMAIN("13","minio-对外访问域名"),
    ;

    private final String type;
    private final String name;

    BusinessEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }



    public static BusinessEnum getOne(String type) {
        if (null == type) {
            return null;
        }
        BusinessEnum[] arr = BusinessEnum.values();
        for (BusinessEnum itemObj : arr) {
            if (itemObj.getType() == type) {
                return itemObj;
            }
        }
        return null;
    }


    public static LinkedList<Map<String, Object>> toList() {
        LinkedList<Map<String, Object>> dataList = new LinkedList<>();
        BusinessEnum[] arr = BusinessEnum.values();
        for (BusinessEnum itemObj : arr) {
            LinkedHashMap<String, Object> itemMap = new LinkedHashMap<>();
            String type = itemObj.getType();
            String name = itemObj.getName();
            itemMap.put("type", type);
            itemMap.put("name", name);
            dataList.add(itemMap);
        }
        return dataList;
    }



    public static Map<String, String> toMap() {
        LinkedHashMap<String, String> itemMap = new LinkedHashMap<>();
        BusinessEnum[] arr = BusinessEnum.values();
        for (BusinessEnum itemObj : arr) {
            String type = itemObj.getType();
            String name = itemObj.getName();
            itemMap.put(type, name);
        }
        return itemMap;
    }



    public static boolean isExist(String type) {
        BusinessEnum one = getOne(type);
        return one!=null;
    }

    public static boolean isNotExist(String type) {
        return !isExist(type);
    }



}

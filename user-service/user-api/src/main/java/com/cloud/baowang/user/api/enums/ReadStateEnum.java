package com.cloud.baowang.user.api.enums;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

@Getter
public enum ReadStateEnum {

    UnRead(0, "未读"),
    Read(1, "已读"),
    ;

    private final Integer type;
    private final String description;

    ReadStateEnum(Integer type, String description) {
        this.type = type;
        this.description = description;
    }



    public static ReadStateEnum getOne(Integer type) {
        if (null == type) {
            return null;
        }
        ReadStateEnum[] arr = ReadStateEnum.values();
        for (ReadStateEnum itemObj : arr) {
            if (itemObj.getType() == type) {
                return itemObj;
            }
        }
        return null;
    }


    public static LinkedList<Map<String, Object>> toList() {
        LinkedList<Map<String, Object>> dataList = new LinkedList<>();
        ReadStateEnum[] arr = ReadStateEnum.values();
        for (ReadStateEnum itemObj : arr) {
            LinkedHashMap<String, Object> itemMap = new LinkedHashMap<>();
            Integer type = itemObj.getType();
            String description = itemObj.getDescription();
            itemMap.put("type", type);
            itemMap.put("description", description);
            dataList.add(itemMap);
        }
        return dataList;
    }



    public static Map<Integer, String> toMap() {
        LinkedHashMap<Integer, String> itemMap = new LinkedHashMap<>();
        ReadStateEnum[] arr = ReadStateEnum.values();
        for (ReadStateEnum itemObj : arr) {
            Integer type = itemObj.getType();
            String description = itemObj.getDescription();
            itemMap.put(type, description);
        }
        return itemMap;
    }



    public static boolean isExist(Integer type) {
        ReadStateEnum one = getOne(type);
        return one!=null;
    }

    public static boolean isNotExist(Integer type) {
        return !isExist(type);
    }



}

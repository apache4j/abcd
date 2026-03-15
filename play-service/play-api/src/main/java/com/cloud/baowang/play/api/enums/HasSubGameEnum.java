package com.cloud.baowang.play.api.enums;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

@Getter
public enum HasSubGameEnum {

    HasSubGame(1, "有子游戏"),
    HasNotSubGame(2, "没有子游戏"),
    ;

    private final Integer type;
    private final String description;

    HasSubGameEnum(Integer type, String description) {
        this.type = type;
        this.description = description;
    }



    public static HasSubGameEnum getOne(Integer type) {
        if (null == type) {
            return null;
        }
        HasSubGameEnum[] arr = HasSubGameEnum.values();
        for (HasSubGameEnum itemObj : arr) {
            if (itemObj.getType() == type) {
                return itemObj;
            }
        }
        return null;
    }


    public static LinkedList<Map<String, Object>> toList() {
        LinkedList<Map<String, Object>> dataList = new LinkedList<>();
        HasSubGameEnum[] arr = HasSubGameEnum.values();
        for (HasSubGameEnum itemObj : arr) {
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
        HasSubGameEnum[] arr = HasSubGameEnum.values();
        for (HasSubGameEnum itemObj : arr) {
            Integer type = itemObj.getType();
            String description = itemObj.getDescription();
            itemMap.put(type, description);
        }
        return itemMap;
    }



    public static boolean isExist(Integer type) {
        HasSubGameEnum one = getOne(type);
        return one!=null;
    }

    public static boolean isNotExist(Integer type) {
        return !isExist(type);
    }



}

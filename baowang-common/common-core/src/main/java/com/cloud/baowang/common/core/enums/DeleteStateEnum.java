package com.cloud.baowang.common.core.enums;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

@Getter
public enum DeleteStateEnum {

    Delete(0, "删除"),
    Normal(1, "正常"),
    ;

    private final Integer type;
    private final String description;

    DeleteStateEnum(Integer type, String description) {
        this.type = type;
        this.description = description;
    }



    public static DeleteStateEnum getOne(Integer type) {
        if (null == type) {
            return null;
        }
        DeleteStateEnum[] arr = DeleteStateEnum.values();
        for (DeleteStateEnum itemObj : arr) {
            if (itemObj.getType() == type) {
                return itemObj;
            }
        }
        return null;
    }


    public static LinkedList<Map<String, Object>> toList() {
        LinkedList<Map<String, Object>> dataList = new LinkedList<>();
        DeleteStateEnum[] arr = DeleteStateEnum.values();
        for (DeleteStateEnum itemObj : arr) {
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
        DeleteStateEnum[] arr = DeleteStateEnum.values();
        for (DeleteStateEnum itemObj : arr) {
            Integer type = itemObj.getType();
            String description = itemObj.getDescription();
            itemMap.put(type, description);
        }
        return itemMap;
    }



    public static boolean isExist(Integer type) {
        DeleteStateEnum one = getOne(type);
        return one!=null;
    }

    public static boolean isNotExist(Integer type) {
        return !isExist(type);
    }



}

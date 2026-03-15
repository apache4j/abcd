package com.cloud.baowang.agent.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 同system_param domain_type
 */
@Getter
@AllArgsConstructor
public enum DomainTypeEnum {

    PC(1, "PC"),
    H5(2, "H5"),
    APP(3, "APP"),
    Exclusive(4, "专属"),
    ;

    private final Integer type;
    private final String description;



    public static DomainTypeEnum getOne(Integer type) {
        if (null == type) {
            return null;
        }
        DomainTypeEnum[] arr = DomainTypeEnum.values();
        for (DomainTypeEnum itemObj : arr) {
            if (itemObj.getType().equals(type)) {
                return itemObj;
            }
        }
        return null;
    }


    public static LinkedList<Map<String, Object>> toList() {
        LinkedList<Map<String, Object>> dataList = new LinkedList<>();
        DomainTypeEnum[] arr = DomainTypeEnum.values();
        for (DomainTypeEnum itemObj : arr) {
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
        DomainTypeEnum[] arr = DomainTypeEnum.values();
        for (DomainTypeEnum itemObj : arr) {
            Integer type = itemObj.getType();
            String description = itemObj.getDescription();
            itemMap.put(type, description);
        }
        return itemMap;
    }


    public static boolean isExist(Integer type) {
        DomainTypeEnum one = getOne(type);
        return one != null;
    }

    public static boolean isNotExist(Integer type) {
        return !isExist(type);
    }


}

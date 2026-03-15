package com.cloud.baowang.agent.api.enums;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

@Getter
public enum ImageTypeEnum {

    Composite(1, "综合推广图"),
    Sport(2, "体育推广图"),
    Immortal(3, "真人推广图"),
    Esport(4, "电竞推广图"),
    Lottery(5, "彩票推广图"),
    Chess(6, "棋牌推广图"),
    Activity(7, "活动推广图"),
    ;

    private final Integer type;
    private final String description;

    ImageTypeEnum(Integer type, String description) {
        this.type = type;
        this.description = description;
    }



    public static ImageTypeEnum getOne(Integer type) {
        if (null == type) {
            return null;
        }
        ImageTypeEnum[] arr = ImageTypeEnum.values();
        for (ImageTypeEnum itemObj : arr) {
            if (itemObj.getType() == type) {
                return itemObj;
            }
        }
        return null;
    }


    public static LinkedList<Map<String, Object>> toList() {
        LinkedList<Map<String, Object>> dataList = new LinkedList<>();
        ImageTypeEnum[] arr = ImageTypeEnum.values();
        for (ImageTypeEnum itemObj : arr) {
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
        ImageTypeEnum[] arr = ImageTypeEnum.values();
        for (ImageTypeEnum itemObj : arr) {
            Integer type = itemObj.getType();
            String description = itemObj.getDescription();
            itemMap.put(type, description);
        }
        return itemMap;
    }



    public static boolean isExist(Integer type) {
        ImageTypeEnum one = getOne(type);
        return one!=null;
    }

    public static boolean isNotExist(Integer type) {
        return !isExist(type);
    }



}

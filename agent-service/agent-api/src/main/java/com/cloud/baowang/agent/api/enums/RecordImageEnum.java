package com.cloud.baowang.agent.api.enums;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * system_param agent_image_change_type
 */
@Getter
public enum RecordImageEnum {
    IMAGE_size(5, "图片尺寸"),
    ImageType(2, "图片类型"),
    OrderNumber(3, "排序"),
    Image(4, "图片"),
    ;

    private final Integer type;
    private final String description;

    RecordImageEnum(Integer type, String description) {
        this.type = type;
        this.description = description;
    }


    public static RecordImageEnum getOne(Integer type) {
        if (null == type) {
            return null;
        }
        RecordImageEnum[] arr = RecordImageEnum.values();
        for (RecordImageEnum itemObj : arr) {
            if (itemObj.getType() == type) {
                return itemObj;
            }
        }
        return null;
    }


    public static LinkedList<Map<String, Object>> toList() {
        LinkedList<Map<String, Object>> dataList = new LinkedList<>();
        RecordImageEnum[] arr = RecordImageEnum.values();
        for (RecordImageEnum itemObj : arr) {
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
        RecordImageEnum[] arr = RecordImageEnum.values();
        for (RecordImageEnum itemObj : arr) {
            Integer type = itemObj.getType();
            String description = itemObj.getDescription();
            itemMap.put(type, description);
        }
        return itemMap;
    }


    public static boolean isExist(Integer type) {
        RecordImageEnum one = getOne(type);
        return one != null;
    }

    public static boolean isNotExist(Integer type) {
        return !isExist(type);
    }


}

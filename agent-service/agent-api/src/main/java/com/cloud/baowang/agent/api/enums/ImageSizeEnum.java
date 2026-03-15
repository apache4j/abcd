package com.cloud.baowang.agent.api.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

@Getter
public enum ImageSizeEnum {

    ImageSize1("750*1334", "750*1334"),
    ImageSize2("1044*507", "1044*507"),
    ImageSize3("1080*1921", "1080*1921"),
    ImageSize4("1210*588", "1210*588"),
    ImageSize5("1080*1920", "1080*1920"),
    ;

    private final String type;
    private final String description;

    ImageSizeEnum(String type, String description) {
        this.type = type;
        this.description = description;
    }



    public static ImageSizeEnum getOne(String type) {
        if (null == type) {
            return null;
        }
        ImageSizeEnum[] arr = ImageSizeEnum.values();
        for (ImageSizeEnum itemObj : arr) {
            if (StringUtils.equals(itemObj.getType(), type)) {
                return itemObj;
            }
        }
        return null;
    }


    public static LinkedList<Map<String, Object>> toList() {
        LinkedList<Map<String, Object>> dataList = new LinkedList<>();
        ImageSizeEnum[] arr = ImageSizeEnum.values();
        for (ImageSizeEnum itemObj : arr) {
            LinkedHashMap<String, Object> itemMap = new LinkedHashMap<>();
            String type = itemObj.getType();
            String description = itemObj.getDescription();
            itemMap.put("type", type);
            itemMap.put("description", description);
            dataList.add(itemMap);
        }
        return dataList;
    }



    public static Map<String, String> toMap() {
        LinkedHashMap<String, String> itemMap = new LinkedHashMap<>();
        ImageSizeEnum[] arr = ImageSizeEnum.values();
        for (ImageSizeEnum itemObj : arr) {
            String type = itemObj.getType();
            String description = itemObj.getDescription();
            itemMap.put(type, description);
        }
        return itemMap;
    }



    public static boolean isExist(String type) {
        ImageSizeEnum one = getOne(type);
        return one!=null;
    }

    public static boolean isNotExist(String type) {
        return !isExist(type);
    }



}

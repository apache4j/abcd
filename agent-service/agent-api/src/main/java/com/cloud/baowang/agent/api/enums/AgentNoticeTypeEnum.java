package com.cloud.baowang.agent.api.enums;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

@Getter
public enum AgentNoticeTypeEnum {

    PublicNotice(1, "公告"),
    AgentNotice(2, "消息"),
    ;

    private final Integer type;
    private final String description;

    AgentNoticeTypeEnum(Integer type, String description) {
        this.type = type;
        this.description = description;
    }



    public static AgentNoticeTypeEnum getOne(Integer type) {
        if (null == type) {
            return null;
        }
        AgentNoticeTypeEnum[] arr = AgentNoticeTypeEnum.values();
        for (AgentNoticeTypeEnum itemObj : arr) {
            if (itemObj.getType() == type) {
                return itemObj;
            }
        }
        return null;
    }


    public static LinkedList<Map<String, Object>> toList() {
        LinkedList<Map<String, Object>> dataList = new LinkedList<>();
        AgentNoticeTypeEnum[] arr = AgentNoticeTypeEnum.values();
        for (AgentNoticeTypeEnum itemObj : arr) {
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
        AgentNoticeTypeEnum[] arr = AgentNoticeTypeEnum.values();
        for (AgentNoticeTypeEnum itemObj : arr) {
            Integer type = itemObj.getType();
            String description = itemObj.getDescription();
            itemMap.put(type, description);
        }
        return itemMap;
    }



    public static boolean isExist(Integer type) {
        AgentNoticeTypeEnum one = getOne(type);
        return one!=null;
    }

    public static boolean isNotExist(Integer type) {
        return !isExist(type);
    }



}

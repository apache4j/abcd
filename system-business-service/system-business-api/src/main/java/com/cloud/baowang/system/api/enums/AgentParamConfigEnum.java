package com.cloud.baowang.system.api.enums;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
//类型: 1=固定值、2=充值金额,3=有效投注
@Getter
public enum AgentParamConfigEnum {


    FixedValue(1, "固定值"),
    DEPOSIT_AMOUNT(2, "充值金额"),
    VALID_BET(3, "有效投注"),
    Percent(4, "百分比"),


    ;

    private final Integer type;
    private final String description;

    AgentParamConfigEnum(Integer type, String description) {
        this.type = type;
        this.description = description;
    }



    public static AgentParamConfigEnum getOne(Integer type) {
        if (null == type) {
            return null;
        }
        AgentParamConfigEnum[] arr = AgentParamConfigEnum.values();
        for (AgentParamConfigEnum itemObj : arr) {
            if (itemObj.getType() == type) {
                return itemObj;
            }
        }
        return null;
    }


    public static LinkedList<Map<String, Object>> toList() {
        LinkedList<Map<String, Object>> dataList = new LinkedList<>();
        AgentParamConfigEnum[] arr = AgentParamConfigEnum.values();
        for (AgentParamConfigEnum itemObj : arr) {
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
        AgentParamConfigEnum[] arr = AgentParamConfigEnum.values();
        for (AgentParamConfigEnum itemObj : arr) {
            Integer type = itemObj.getType();
            String description = itemObj.getDescription();
            itemMap.put(type, description);
        }
        return itemMap;
    }



    public static boolean isExist(Integer type) {
        AgentParamConfigEnum one = getOne(type);
        return one!=null;
    }

    public static boolean isNotExist(Integer type) {
        return !isExist(type);
    }



}

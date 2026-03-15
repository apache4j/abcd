package com.cloud.baowang.wallet.api.enums.wallet;

import lombok.Getter;

/**
 * 地址类型枚举
 * 参考: COLLECT 归集地址 FEE 手续费及出金地址 MAIN 主账户
 */
@Getter
public enum AddressTypeEnum {
    COLLECT("COLLECT","归集地址"),
    FEE("FEE","手续费及出金地址"),
    MAIN("MAIN","主账户"),
    ;


    private String typeCode;

    private String typeName;

    AddressTypeEnum(String typeCode, String typeName) {
        this.typeCode = typeCode;
        this.typeName = typeName;
    }

    public static String parseName(String addressType) {
        for(AddressTypeEnum addressTypeEnum:AddressTypeEnum.values()){
            if(addressTypeEnum.getTypeCode().equalsIgnoreCase(addressType)){
                return addressTypeEnum.getTypeName();
            }
        }
        return "";
    }

    public static AddressTypeEnum parseObj(String addressType) {
        for(AddressTypeEnum addressTypeEnum:AddressTypeEnum.values()){
            if(addressTypeEnum.getTypeCode().equalsIgnoreCase(addressType)){
                return addressTypeEnum;
            }
        }
        return null;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}

package com.cloud.baowang.wallet.api.enums.wallet;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 提现信息收集枚举
 *
 * 关联 system_param中的 withdraw_collect
 *
 */
@Getter
public enum WithDrawCollectEnum {

    BANK_NAME("bankName", "银行名称", Lists.newArrayList(WithdrawTypeEnum.BANK_CARD)),
    BANK_CODE("bankCode", "银行代码", Lists.newArrayList(WithdrawTypeEnum.BANK_CARD)),
    BANK_CARD("bankCard", "银行卡号", Lists.newArrayList(WithdrawTypeEnum.BANK_CARD)),
    SURNAME("surname", "姓名", Lists.newArrayList(WithdrawTypeEnum.BANK_CARD,WithdrawTypeEnum.ELECTRONIC_WALLET,WithdrawTypeEnum.MANUAL_WITHDRAW)),
//    USER_NAME("userName", "名", Lists.newArrayList(WithdrawTypeEnum.BANK_CARD,WithdrawTypeEnum.ELECTRONIC_WALLET,WithdrawTypeEnum.MANUAL_WITHDRAW)),
    USER_EMAIL("userEmail", "邮箱", Lists.newArrayList(WithdrawTypeEnum.BANK_CARD)),
    USER_PHONE("userPhone", "联系电话", Lists.newArrayList(WithdrawTypeEnum.BANK_CARD,WithdrawTypeEnum.ELECTRONIC_WALLET)),
    PROVINCE_NAME("provinceName", "省份", Lists.newArrayList(WithdrawTypeEnum.BANK_CARD)),
    CITY_NAME("cityName", "城市", Lists.newArrayList(WithdrawTypeEnum.BANK_CARD)),
    DETAIL_ADDRESS("detailAddress", "详细地址", Lists.newArrayList(WithdrawTypeEnum.BANK_CARD)),
    USER_ACCOUNT("userAccount", "账户", Lists.newArrayList(WithdrawTypeEnum.ELECTRONIC_WALLET,WithdrawTypeEnum.MANUAL_WITHDRAW)),
    NETWORK_TYPE("networkType", "链网络类型",Lists.newArrayList(WithdrawTypeEnum.CRYPTO_CURRENCY)),
    ADDRESS_NO("addressNo", "收款地址",Lists.newArrayList(WithdrawTypeEnum.CRYPTO_CURRENCY,WithdrawTypeEnum.ELECTRONIC_WALLET)),

    IFSC_CODE("ifscCode","IFSC",Lists.newArrayList(WithdrawTypeEnum.BANK_CARD)),

    ELECTRONIC_WALLET_NAME("electronicWalletName","钱包名称",Lists.newArrayList(WithdrawTypeEnum.ELECTRONIC_WALLET)),

    CPF("cpf","CPF",Lists.newArrayList(WithdrawTypeEnum.BANK_CARD,WithdrawTypeEnum.ELECTRONIC_WALLET,WithdrawTypeEnum.MANUAL_WITHDRAW)),
    ;

    private final String type;
    private final String description;
    private final List<WithdrawTypeEnum> withdrawTypeEnums;

    WithDrawCollectEnum(String type, String description,List<WithdrawTypeEnum> withdrawTypeEnums) {
        this.type = type;
        this.description = description;
        this.withdrawTypeEnums=withdrawTypeEnums;
    }



    public static WithDrawCollectEnum getOne(String type) {
        if (null == type) {
            return null;
        }
        WithDrawCollectEnum[] arr = WithDrawCollectEnum.values();
        for (WithDrawCollectEnum itemObj : arr) {
            if (itemObj.getType() == type) {
                return itemObj;
            }
        }
        return null;
    }

    public static WithDrawCollectEnum of(String type) {
        for (WithDrawCollectEnum withDrawCollectEnum : WithDrawCollectEnum.values()) {
            if (withDrawCollectEnum.type.equals(type)) {
                return withDrawCollectEnum;
            }
        }
        return null;
    }


    public static LinkedList<Map<String, Object>> toList() {
        LinkedList<Map<String, Object>> dataList = new LinkedList<>();
        WithDrawCollectEnum[] arr = WithDrawCollectEnum.values();
        for (WithDrawCollectEnum itemObj : arr) {
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
        WithDrawCollectEnum[] arr = WithDrawCollectEnum.values();
        for (WithDrawCollectEnum itemObj : arr) {
            String type = itemObj.getType();
            String description = itemObj.getDescription();
            itemMap.put(type, description);
        }
        return itemMap;
    }



    public static boolean isExist(String type) {
        WithDrawCollectEnum one = getOne(type);
        return one!=null;
    }

    public static boolean isNotExist(String type) {
        return !isExist(type);
    }


    public static List<WithDrawCollectEnum> parseByWalletTyps(WithdrawTypeEnum walletTypeEnum) {
        List<WithDrawCollectEnum> resultEnums=Lists.newArrayList();
        WithDrawCollectEnum[] arr = WithDrawCollectEnum.values();
        for (WithDrawCollectEnum itemObj : arr) {
            if(itemObj.getWithdrawTypeEnums().contains(walletTypeEnum)){
                resultEnums.add(itemObj);
            }
        }
        return resultEnums;
    }
}

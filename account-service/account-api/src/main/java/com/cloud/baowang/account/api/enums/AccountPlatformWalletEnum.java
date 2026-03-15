package com.cloud.baowang.account.api.enums;

import java.util.Arrays;
import java.util.List;

/**
 * 平台币账变相关枚举
 * @author qiqi
 * system_param 中的  paltform_coin_balance_type
 */
public class AccountPlatformWalletEnum {

    /**
     * 业务类型
     * 1: VIP福利,2:活动优惠,3:勋章奖励,4:平台币兑换
     * CommonConstant.PLATFORM_BUSINESS_COIN_TYPE
     */
    public enum BusinessCoinTypeEnum {
        MEMBER_VIP_BENEFITS("1", "VIP福利"),
        MEMBER_ACTIVITIES("2", "活动优惠"),

        MEDAL_REWARD("3", "勋章奖励"),
        PLATFORM_CONVERSION("4", "平台币兑换"),

        REBATE("5", "返水"),

        OTHER_ADJUSTMENTS("6", "其他调整"),

        ;
        private String code;
        private String name;

        BusinessCoinTypeEnum(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public static BusinessCoinTypeEnum nameOfCode(String code) {
            if (null == code) {
                return null;
            }
            BusinessCoinTypeEnum[] types = BusinessCoinTypeEnum.values();
            for (BusinessCoinTypeEnum type : types) {
                if (code.equals(type.getCode())) {
                    return type;
                }
            }
            return null;
        }

        public static List<BusinessCoinTypeEnum> getList() {
            return Arrays.asList(values());
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /***
     *账变类型
     *
     * CommonConstant.PLATFORM_COIN_TYPE
     *
     */
    public enum CoinTypeEnum {


        MEMBER_VIP_BENEFITS("1", "VIP福利", BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS),


        PROMOTIONS("2", "活动优惠", BusinessCoinTypeEnum.MEMBER_ACTIVITIES),

        MEDAL_REWARD("3", "勋章奖励", BusinessCoinTypeEnum.MEDAL_REWARD),

        PLATFORM_CONVERSION("4", "平台币兑换", BusinessCoinTypeEnum.PLATFORM_CONVERSION),

        REBATE("5", "返水", BusinessCoinTypeEnum.REBATE),

        MEMBER_VIP_BENEFITS_ADD("6", "VIP福利(平台币上分)", BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS),

        MEMBER_VIP_BENEFITS_SUBTRACT("7", "VIP福利(平台币下分)", BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS),

        MEMBER_ACTIVITIES_ADD("8", "活动优惠(平台币上分)", BusinessCoinTypeEnum.MEMBER_ACTIVITIES),

        MEMBER_ACTIVITIES_SUBTRACT("9", "活动优惠(平台币下分)", BusinessCoinTypeEnum.MEMBER_ACTIVITIES),

        OTHER_ADJUSTMENTS_ADD("10", "其他(平台币上分)", BusinessCoinTypeEnum.OTHER_ADJUSTMENTS),

        OTHER_ADJUSTMENTS_SUBTRACT("11", "其他(平台币下分)", BusinessCoinTypeEnum.OTHER_ADJUSTMENTS),
        ;
        private String code;
        private String name;

        private final BusinessCoinTypeEnum businessCoinTypeEnum;

        CoinTypeEnum(String code, String name,BusinessCoinTypeEnum businessCoinTypeEnum) {
            this.code = code;
            this.name = name;
            this.businessCoinTypeEnum = businessCoinTypeEnum;
        }

        public static CoinTypeEnum nameOfCode(String code) {
            if (null == code) {
                return null;
            }
            CoinTypeEnum[] types = CoinTypeEnum.values();
            for (CoinTypeEnum type : types) {
                if (code.equals(type.getCode())) {
                    return type;
                }
            }
            return null;
        }

        public static List<CoinTypeEnum> getList() {
            return Arrays.asList(values());
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }




}

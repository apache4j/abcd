package com.cloud.baowang.account.api.enums;

import java.util.Arrays;
import java.util.List;

/**
 * @author ford
 * @Date 2025-10-14
 */
public class AccountUserCoinEnum {

    /**
     * 业务类型
     */
    public enum BusinessCoinTypeEnum {
        MEMBER_DEPOSIT("1", "会员存款"),

        MEMBER_WITHDRAWAL("2", "会员取款"),
        MEMBER_VIP_BENEFITS("3", "VIP福利"),
        MEMBER_ACTIVITIES("4", "活动优惠"),
        GAME_BET("5", "投注"),
        GAME_PAYOUT("6", "派彩"),

        CANCEL_GAME_PAYOUT("7", "派彩取消"),


        PLATFORM_CONVERSION("8", "平台币兑换"),

        OTHER_ADJUSTMENTS("9", "其他调整"),

        REBATE("10", "返水"),

        RISK_CONTROL_ADJUSTMENT("11", "风控调整"),





       /* GAME_BET_PAYOUT("11","投注并派彩"),

        VALET_RECHARGE("12", "代客充值"),
        MEDAL_REWARD_EXTRACT("14", "宝箱奖励"),

        TASK_REWARD_EXTRACT("15", "会员任务"),

        PLAT_TRANSFER("16", "平台币兑换"),*/

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
     */
    public enum CoinTypeEnum {

        MEMBER_DEPOSIT("1", "会员存款",BusinessCoinTypeEnum.MEMBER_DEPOSIT),

        MEMBER_DEPOSIT_ADMIN("2", "会员存款(后台)",BusinessCoinTypeEnum.MEMBER_DEPOSIT),


        MEMBER_WITHDRAWAL("3", "会员提款",BusinessCoinTypeEnum.MEMBER_WITHDRAWAL),

        MEMBER_WITHDRAWAL_FAIL("4", "提款失败",BusinessCoinTypeEnum.MEMBER_WITHDRAWAL),

        MEMBER_WITHDRAWAL_ADMIN("5", "会员提款(后台)",BusinessCoinTypeEnum.MEMBER_WITHDRAWAL),

        MEMBER_VIP_BENEFITS("6", "VIP福利",BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS),

        MEMBER_VIP_BENEFITS_ADD("7", "VIP福利增加调整",BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS),
        MEMBER_VIP_BENEFITS_SUBTRACT("8", "VIP福利扣除调整",BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS),

        PROMOTIONS("9", "活动优惠",BusinessCoinTypeEnum.MEMBER_ACTIVITIES),

        PROMOTIONS_ADD("10", "活动优惠增加金额",BusinessCoinTypeEnum.MEMBER_ACTIVITIES),
        PROMOTIONS_SUBTRACT("11", "活动优惠扣除金额",BusinessCoinTypeEnum.MEMBER_ACTIVITIES),

        GAME_BET("12", "投注",BusinessCoinTypeEnum.GAME_BET),

        CANCEL_BET("13", "投注取消",BusinessCoinTypeEnum.GAME_BET),
        GAME_PAYOUT("14", "派彩",BusinessCoinTypeEnum.GAME_PAYOUT),

        RECALCULATE_GAME_PAYOUT("15", "重算派彩",BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT),

        CANCEL_GAME_PAYOUT("16", "派彩取消",BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT),

        PLATFORM_CONVERSION("17", "平台币兑换",BusinessCoinTypeEnum.PLATFORM_CONVERSION),

        TRANSFER_FROM_SUPERIOR("18", "代理代存",BusinessCoinTypeEnum.MEMBER_DEPOSIT),

        OTHER_ADD("19", "其他增加调整",BusinessCoinTypeEnum.OTHER_ADJUSTMENTS),

        OTHER_SUBTRACT("20", "其他扣除调整",BusinessCoinTypeEnum.OTHER_ADJUSTMENTS),

        RETURN_BET("21", "投注返还",BusinessCoinTypeEnum.GAME_BET),

        REBATE("22", "返水",BusinessCoinTypeEnum.REBATE),

        REBATE_ADD("23", "返水增加金额",BusinessCoinTypeEnum.REBATE),

        REBATE_SUBTRACT("24","返水扣除金额",BusinessCoinTypeEnum.REBATE),

        RISK_CONTROL_ADJUSTMENT_ADD("25", "风控调整增加金额",BusinessCoinTypeEnum.RISK_CONTROL_ADJUSTMENT),

        RISK_CONTROL_ADJUSTMENT_SUBTRACT("26", "风控调整扣除金额",BusinessCoinTypeEnum.RISK_CONTROL_ADJUSTMENT),

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

    /***
     *客户端类型
     */
    public enum CustomerCoinTypeEnum {
        DEPOSIT("1", "充值"),

        WITHDRAWAL("2", "提款"),

        WITHDRAWAL_RETURN("3", "提款失败"),
        ADD_COIN("4", "加币"),
        SUBTRACT_COIN("5", "减币"),

        OFFLINE_BONUS("6","红利"),
        GAME_BET("7", "投注"),
        GAME_PAYOUT("8", "派彩"),

        REBATE("9", "返水"),
        CANCEL_GAME_PAYOUT("10", "取消派彩"),

        VIP_RIGHTS_INTERESTS_UPGRADE("11","VIP升级奖金"),
        VIP_RIGHTS_INTERESTS_WEEK("12","VIP周返奖"),
        VIP_RIGHTS_INTERESTS_MONTH("13","VIP月返奖"),
        GAME_BET_PAYOUT("14","投注并派彩"),
        PLATFORM_CONVERSION("15", "平台币兑换"),
        ;
        private String code;
        private String name;

        CustomerCoinTypeEnum(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public static CustomerCoinTypeEnum nameOfCode(String code) {
            if (null == code) {
                return null;
            }
            CustomerCoinTypeEnum[] types = CustomerCoinTypeEnum.values();
            for (CustomerCoinTypeEnum type : types) {
                if (code.equals(type.getCode())) {
                    return type;
                }
            }
            return null;
        }

        public static List<CustomerCoinTypeEnum> getList() {
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

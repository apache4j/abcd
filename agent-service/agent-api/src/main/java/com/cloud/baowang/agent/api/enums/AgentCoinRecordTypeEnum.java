package com.cloud.baowang.agent.api.enums;

import java.util.Arrays;
import java.util.List;

/**
 * @author qiqi
 */
public class AgentCoinRecordTypeEnum {


    public enum AgentBusinessCoinTypeEnum {

        OTHER_ADJUSTMENTS("0", "其他调整"),

        AGENT_DEPOSIT("1", "代理存款"),

        AGENT_TRANSFER("2", "代理转账"),

        AGENT_QUOTA_TRANSFER("3", "额度转账"),

        DEPOSIT_OF_SUBORDINATES("4", "代下级存款"),

        AGENT_QUOTA("5","代理额度"),

        AGENT_PROMOTIONS("6", "代理活动"),

        AGENT_WITHDRAWAL("7", "代理取款"),

        AGENT_COMMISSION("8", "代理佣金"),


        ;
        private String code;
        private String name;

        AgentBusinessCoinTypeEnum(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public static AgentBusinessCoinTypeEnum nameOfCode(String code) {
            if (null == code) {
                return null;
            }
            AgentBusinessCoinTypeEnum[] types = AgentBusinessCoinTypeEnum.values();
            for (AgentBusinessCoinTypeEnum type : types) {
                if (code.equals(type.getCode())) {
                    return type;
                }
            }
            return null;
        }

        public static List<AgentBusinessCoinTypeEnum> getList() {
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


    public enum AgentCoinTypeEnum {

        OTHERS_ADD_ADJUSTMENTS("1","其他增加调整"),
        OTHERS_SUBTRACT_ADJUSTMENTS("2","其他扣除调整"),
        //代理存款
        AGENT_DEPOSIT("3", "代理存款"),
        AGENT_ADMIN_DEPOSIT("4", "代理存款(后台)"),

        //代理转账
        TRANSFER_SUBORDINATES("5", "转给下级代理"),
        SUPERIOR_TRANSFER("6", "上级转入"),

        //额度转账
        QUOTA_TRANSFER("7","转入额度钱包"),
        TO_QUOTA_TRANSFER("8","转给额度钱包"),

        //代理额度
        QUOTA_ADD("9", "额度增加调整"),
        QUOTA_SUBTRACT("10", "额度扣除调整"),

        //代理活动
        PROMOTIONS_ADD("11", "活动增加调整"),
        PROMOTIONS_SUBTRACT("12", "活动扣除调整"),

        //代理取款
        AGENT_WITHDRAWAL("13","代理取款"),
        AGENT_WITHDRAWAL_FAIL("14","取款失败"),
        AGENT_WITHDRAWAL_ADMIN("15", "代理取款(后台)"),

        //代理佣金
//        PLATFORM_ADD("16", "平台转入"),
        COMMISSION_ADD("17", "佣金增加调整"),
        COMMISSION_SUBTRACT("18", "佣金扣除调整"),

        TRANSFER_SUBORDINATES_MEMBER("19", "转给下级会员"),

        NEGATIVE_PROFIT_COMMISSION("20", "负盈利佣金"),

        EFFECTIVE_TURNOVER_REBATE("21", "有效流水返点"),

        CAPITATION_FEE("22", "人头费"),
        ;
        private String code;
        private String name;

        AgentCoinTypeEnum(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public static AgentCoinTypeEnum nameOfCode(String code) {
            if (null == code) {
                return null;
            }
            AgentCoinTypeEnum[] types = AgentCoinTypeEnum.values();
            for (AgentCoinTypeEnum type : types) {
                if (code.equals(type.getCode())) {
                    return type;
                }
            }
            return null;
        }

        public static List<AgentCoinTypeEnum> getList() {
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
     *额度钱包客户端账变类型
     */
    public enum AgentCustomerCoinTypeEnum {
        OTHER_ADJUSTMENTS("0", "其他调整"),

        DEPOSIT("1", "存款"),

        AGENT_TRANSFER("2", "代理转账"),

        QUOTA_TRANSFER("3", "额度转账"),

        DEPOSIT_OF_SUBORDINATES("4", "代会员存款"),

        AGENT_QUOTA("5","代理额度"),

        PROMOTIONS("6", "活动优惠"),

        WITHDRAWAL("7", "取款"),

        COMMISSION("8", "佣金"),

        ;
        private String code;
        private String name;

        AgentCustomerCoinTypeEnum(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public static AgentCustomerCoinTypeEnum nameOfCode(String code) {
            if (null == code) {
                return null;
            }
            AgentCustomerCoinTypeEnum[] types = AgentCustomerCoinTypeEnum.values();
            for (AgentCustomerCoinTypeEnum type : types) {
                if (code.equals(type.getCode())) {
                    return type;
                }
            }
            return null;
        }

        public static List<AgentCustomerCoinTypeEnum> getList() {
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


    public enum AgentBalanceTypeEnum {


        INCOME("1", "收入"),
        EXPENSES("2", "支出"),
        FREEZE("3", "冻结"),
        UN_FREEZE("4", "解冻"),
        ;

        private String code;
        private String name;

        AgentBalanceTypeEnum(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public static AgentBalanceTypeEnum nameOfCode(String code) {
            if (null == code) {
                return null;
            }
            AgentBalanceTypeEnum[] types = AgentBalanceTypeEnum.values();
            for (AgentBalanceTypeEnum type : types) {
                if (code.equals(type.getCode())) {
                    return type;
                }
            }
            return null;
        }

        public static List<AgentBalanceTypeEnum> getList() {
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


    /**
     * 代理钱包类型
     */
    public enum AgentWalletTypeEnum {
        COMMISSION_WALLET("1", "佣金钱包"),
        QUOTA_WALLET("2", "额度钱包"),
        ;
        private String code;
        private String name;

        AgentWalletTypeEnum(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public static AgentWalletTypeEnum nameOfCode(String code) {
            if (null == code) {
                return null;
            }
            AgentWalletTypeEnum[] types = AgentWalletTypeEnum.values();
            for (AgentWalletTypeEnum type : types) {
                if (code.equals(type.getCode())) {
                    return type;
                }
            }
            return null;
        }

        public static List<AgentWalletTypeEnum> getList() {
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

    /**
     * 代理代存类型
     */
    public enum AgentDepositSubordinatesTypeEnum {
        COMMISSION_DEPOSIT_SUBORDINATES("1", "佣金代存"),
        QUOTA_DEPOSIT_SUBORDINATES("2", "额度代存"),
        ;
        private String code;
        private String name;

        AgentDepositSubordinatesTypeEnum(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public static AgentDepositSubordinatesTypeEnum nameOfCode(String code) {
            if (null == code) {
                return null;
            }
            AgentDepositSubordinatesTypeEnum[] types = AgentDepositSubordinatesTypeEnum.values();
            for (AgentDepositSubordinatesTypeEnum type : types) {
                if (code.equals(type.getCode())) {
                    return type;
                }
            }
            return null;
        }

        public static List<AgentDepositSubordinatesTypeEnum> getList() {
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


    /**
     * 代理充值类型
     */
    public enum AgentDepositTypeEnum {
        COMMISSION_DEPOSIT("1", "佣金充值"),
        QUOTA_DEPOSIT("2", "额度充值"),
        ;
        private String code;
        private String name;

        AgentDepositTypeEnum(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public static AgentDepositTypeEnum nameOfCode(String code) {
            if (null == code) {
                return null;
            }
            AgentDepositTypeEnum[] types = AgentDepositTypeEnum.values();
            for (AgentDepositTypeEnum type : types) {
                if (code.equals(type.getCode())) {
                    return type;
                }
            }
            return null;
        }

        public static List<AgentDepositTypeEnum> getList() {
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

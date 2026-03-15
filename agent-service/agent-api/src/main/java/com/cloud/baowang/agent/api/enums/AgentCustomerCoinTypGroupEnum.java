package com.cloud.baowang.agent.api.enums;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 代理客户端各类型整合
 */
public class AgentCustomerCoinTypGroupEnum {

    /***
     *代理PC HT账变明细 账变类型
     */
    public enum AgentCustomCoinTypeEnum {
        AGENT_DEPOSIT("1", "存款"),

        AGENT_WITHDRAWAL("2", "取款"),
        AGENT_TRANSFER("3", "转账"),

        PROMOTIONS_ADD("4", "佣金"),
        OTHERS("5", "其他"),
        ;
        private String code;
        private String name;

        AgentCustomCoinTypeEnum(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public static AgentCustomCoinTypeEnum nameOfCode(String code) {
            if (null == code) {
                return null;
            }
            AgentCustomCoinTypeEnum[] types = AgentCustomCoinTypeEnum.values();
            for (AgentCustomCoinTypeEnum type : types) {
                if (code.equals(type.getCode())) {
                    return type;
                }
            }
            return null;
        }

        public static List<AgentCustomCoinTypeEnum> getList() {
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
     *账变类型按客户端分组
     */
    public enum AgentCoinTypeEnum {
        //存款
        AGENT_DEPOSIT("3", "代理存款", AgentCustomCoinTypeEnum.AGENT_DEPOSIT),
        AGENT_ADMIN_DEPOSIT("4", "代理后台存款", AgentCustomCoinTypeEnum.AGENT_DEPOSIT),


        //其他
        OTHERS_ADD_ADJUSTMENTS("1","其他增加调整", AgentCustomCoinTypeEnum.OTHERS),
        OTHERS_SUBTRACT_ADJUSTMENTS("2","其他扣除调整", AgentCustomCoinTypeEnum.OTHERS),
        QUOTA_ADD("9", "额度增加调整", AgentCustomCoinTypeEnum.OTHERS),
        QUOTA_SUBTRACT("10", "额度扣除调整", AgentCustomCoinTypeEnum.OTHERS),
        PROMOTIONS_ADD("11", "活动增加调整", AgentCustomCoinTypeEnum.OTHERS),
        PROMOTIONS_SUBTRACT("12", "活动扣除调整", AgentCustomCoinTypeEnum.OTHERS),


        //转账

        QUOTA_TRANSFER("7","转入额度钱包",AgentCustomCoinTypeEnum.AGENT_TRANSFER),
        TO_QUOTA_TRANSFER("8","转给额度钱包",AgentCustomCoinTypeEnum.AGENT_TRANSFER),
        TRANSFER_SUBORDINATES_AGENT("5", "转给下级代理", AgentCustomCoinTypeEnum.AGENT_TRANSFER),
        SUPERIOR_TRANSFER("6", "上级转入", AgentCustomCoinTypeEnum.AGENT_TRANSFER),
        TRANSFER_SUBORDINATES("19", "转给下级会员", AgentCustomCoinTypeEnum.AGENT_TRANSFER),

        //取款
        AGENT_WITHDRAWAL("13","代理取款", AgentCustomCoinTypeEnum.AGENT_WITHDRAWAL),
        AGENT_WITHDRAWAL_FAIL("14", "取款失败", AgentCustomCoinTypeEnum.AGENT_WITHDRAWAL),
        AGENT_WITHDRAWAL_ADMIN("15", "代理取款(后台)",AgentCustomCoinTypeEnum.AGENT_WITHDRAWAL),

        //佣金
//        PLATFORM_ADD("16", "平台转入",AgentCustomCoinTypeEnum.PROMOTIONS_ADD),
        COMMISSION_ADD("17", "佣金增加调整", AgentCustomCoinTypeEnum.PROMOTIONS_ADD),
        COMMISSION_SUBTRACT("18", "佣金扣除调整", AgentCustomCoinTypeEnum.PROMOTIONS_ADD),
        NEGATIVE_PROFIT_COMMISSION("20", "负盈利佣金",AgentCustomCoinTypeEnum.PROMOTIONS_ADD),
        EFFECTIVE_TURNOVER_REBATE("21", "有效流水返点",AgentCustomCoinTypeEnum.PROMOTIONS_ADD),
        CAPITATION_FEE("22", "人头费",AgentCustomCoinTypeEnum.PROMOTIONS_ADD),

        ;
        private String code;
        private String name;

        private AgentCustomCoinTypeEnum agentCustomCoinTypeEnum;

        AgentCoinTypeEnum(String code, String name, AgentCustomCoinTypeEnum agentCustomCoinTypeEnum) {
            this.code = code;
            this.name = name;
            this.agentCustomCoinTypeEnum = agentCustomCoinTypeEnum;
        }

        public static List<String> getCodesByAgentCustomCoinTypeEnum(AgentCustomCoinTypeEnum customCoinTypeEnum) {
            List<String> codes = Lists.newArrayList();
            for (AgentCoinTypeEnum coinTypeEnum : AgentCoinTypeEnum.values()) {
                if (coinTypeEnum.getAgentCustomCoinTypeEnum() == customCoinTypeEnum) {
                    codes.add(coinTypeEnum.getCode());
                }
            }
            return codes;
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

        public static List<String> getAllCodes() {
            List<String> codes = new ArrayList<>();
            for (AgentCoinTypeEnum quotaCoinTypeEnum : AgentCoinTypeEnum.values()) {
                codes.add(quotaCoinTypeEnum.getCode());
            }
            return codes;
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

        public AgentCustomCoinTypeEnum getAgentCustomCoinTypeEnum() {
            return agentCustomCoinTypeEnum;
        }

        public void setAgentCustomCoinTypeEnum(AgentCustomCoinTypeEnum agentCustomCoinTypeEnum) {
            this.agentCustomCoinTypeEnum = agentCustomCoinTypeEnum;
        }
    }



}

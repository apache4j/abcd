package com.cloud.baowang.system.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.cloud.baowang.common.core.vo.base.CodeNameVO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 风险黑名单类型
 */
public enum RiskBlackTypeEnum {

    RISK_REG_IP("1", "注册IP黑名单",true),
    RISK_LOGIN_IP("2", "登录IP黑名单",true),
    RISK_REG_DEVICE("3", "注册设备黑名单",true),
    RISK_LOGIN_DEVICE("4", "登录设备黑名单",true),
    RISK_BANK_ACCOUNT("5", "银行账户黑名单",false),
    RISK_ELECTRONIC_WALLET("6", "电子钱包黑名单",false),
    RISK_VIRTUAL_ACCOUNT("7", "虚拟账户黑名单",false),
    ;

    private String code;
    private String name;
    private boolean needKickOut;

    RiskBlackTypeEnum(String code, String name,boolean needKickOut) {
        this.code = code;
        this.name = name;
        this.needKickOut=needKickOut;
    }

    public static RiskBlackTypeEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        RiskBlackTypeEnum[] types = RiskBlackTypeEnum.values();
        for (RiskBlackTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<RiskBlackTypeEnum> getList() {
        return Arrays.asList(values());
    }


    public static List<CodeValueVO> getMapList() {
        List<CodeValueVO> list = new ArrayList<>(4);
        for (RiskBlackTypeEnum blackTypeEnum :RiskBlackTypeEnum.values()) {
            CodeValueVO codeNameVO = CodeValueVO.builder()
                    .code(blackTypeEnum.getCode())
                    .value(blackTypeEnum.getName()).build();
            list.add(codeNameVO);
        }
        return list;
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

    public boolean isNeedKickOut() {
        return needKickOut;
    }
}

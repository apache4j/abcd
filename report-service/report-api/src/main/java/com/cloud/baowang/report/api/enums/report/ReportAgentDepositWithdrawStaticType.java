package com.cloud.baowang.report.api.enums.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: ford
 * @Date: 2024-11-11
 * 统计类型 transfer:转账 subord:代存
 */
@Getter
@AllArgsConstructor
public enum ReportAgentDepositWithdrawStaticType {

    TRANSFER("transfer", "转账"),
    SUBORD("subord", "代存");


    private final String type;
    private final String desc;



    public static ReportRecalculateEnum of(String type) {
        if (null == type) {
            return null;
        }
        ReportRecalculateEnum[] types = ReportRecalculateEnum.values();
        for (ReportRecalculateEnum reportRecalculateEnum : types) {
            if (type.equals(reportRecalculateEnum.getType())) {
                return reportRecalculateEnum;
            }
        }
        return null;
    }
}

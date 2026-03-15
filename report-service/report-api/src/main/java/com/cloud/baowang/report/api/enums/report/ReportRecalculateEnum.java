package com.cloud.baowang.report.api.enums.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportRecalculateEnum {
    //重算模式 1:按天重算,只用传开始时间，以开始时间当天时间计算，默认 2:按月重算，只用传开始时间，以开始时间月份重算 3:按代理账号与时间范围重算 4.按会员账号与时间范围重算
    DAY(1, "按天重算,默认,只用传开始时间，以开始时间当天开始与结束时间计算"),
    MONTH(2, "按月重算，只用传开始时间，以开始时间月份重算"),
    AGENT(3, "按代理账号与时间范围重算"),
    USER(4, "按会员账号与时间范围重算");

    private final Integer type;
    private final String desc;

    public static ReportRecalculateEnum of(Integer type) {
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

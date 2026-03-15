package com.cloud.baowang.wallet.api.vo.report.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserInfoStatementVO {
    private String userAccount;
    private String userId;
    /**
     * 存款还是取款
     */
    private String type;
    private Long startTime;
    private Long endTime;


    /**
     * 1加、2减
     * {@link com.cloud.baowang.common.core.enums.manualDowmUp.ManualAdjustWayEnum}
     */
    private Integer adjustWay;


    /**
     * 调整类型 各种业务
     * 会员人工加额类型枚举
     * {@link com.cloud.baowang.common.core.enums.manualDowmUp.ManualAdjustTypeEnum}
     * 会员人工减额类型枚举
     * {@link com.cloud.baowang.common.core.enums.manualDowmUp.ManualDownAdjustTypeEnum}
     */
    private Integer adjustType;

    /**
     * 站点
     */
    private String siteCode;
}

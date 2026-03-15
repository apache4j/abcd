package com.cloud.baowang.wallet.api.vo.userCoinRecord;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author : 小智
 * @Date : 2024/10/12 17:24
 * @Version : 1.0
 */
@Schema(title = "会员奖励记录VO对象")
@Data
@I18nClass
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAwardRecordVO implements Serializable {

    private String awardType;

    private String userId;

    private String siteCode;

    private BigDecimal awardAmount;

    private Integer vipGradeCode;

    private Integer vipRankCode;

    private Integer receiveType;

    private Integer receiveStatus;

    private String userAccount;

    private String currency;

    private String accountType;

    private String agentId;

    /* 发放开始时间 */
    private long recordStartTime;
    /* 发放结束时间 */
    private long recordEndTime;

    private String agentAccount;

    private Long expiredTime;
}

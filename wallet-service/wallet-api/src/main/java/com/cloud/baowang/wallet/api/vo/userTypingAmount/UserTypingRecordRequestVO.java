package com.cloud.baowang.wallet.api.vo.userTypingAmount;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author qiqi
 */
@Data
@Schema(title ="会员流水变动记录请求对象")
public class UserTypingRecordRequestVO extends PageVO {


    @Schema(description="流水变更开始时间")
    private Long recordStartTime;

    @Schema(description="流水变更结束时间")
    private Long recordEndTime;

    @Schema(description="账号类型-数据字典code:account_type")
    private String accountType;

    @Schema(description="会员账号")
    private String userAccount;

    @Schema(description="订单号")
    private String orderNo;


    @Schema(description="币种")
    private String currency;






    @Schema(description="代理账号")
    private String agentAccount;

    @Schema(description="账号状态 字典类型code:account_status")
    private String accountStatus;

    @Schema(description="风控级别")
    private String riskLevelId;

    @Schema(description="VIP段位最小值")
    private String minVipRank;

    @Schema(description="VIP段位最大值")
    private String maxVipRank;

    @Schema(description="VIP等级最小值")
    private String minVipGradeCode;

    @Schema(description="VIP等级最大值")
    private String maxVipGradeCode;



    @Schema(description="增减类型 字典类型code:typing_balance_type")
    private String adjustWay;


    @Schema(description="流水类型 字典类型code:typing_adjust_type")
    private String adjustType;

    @Schema(description="是否导出 true 是 false 否")
    private Boolean exportFlag = false;

    private String siteCode;
}

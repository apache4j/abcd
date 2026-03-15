package com.cloud.baowang.report.po;


import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@FieldNameConstants
@TableName("report_user_recharge_withdraw")
@Schema(title = "会员充值取款累计")
public class ReportUserRechargeWithdrawPO extends BasePO {

    /**
     * 站点编码
     */
    private String siteCode;

    /**
     * 会员ID
     */
    private String userId;
    /**
     * 账号类型 1-测试 2-正式
     */
    private String accountType;

    /**
     * 会员账号
     */
    private String userAccount;

    /**
     * 代理id
     */
    private String agentId;
    /**
     * 代理账号
     */
    private String agentAccount;

    /**
     * 日期小时维度
     */
    private Long dayHourMillis;
    /**
     * 站点日期 当天起始时间戳
     */
    private Long dayMillis;

    /**
     * 站点日期 当天起始时间字符串
     */
    private String dayStr;

    /**
     * 币种金额
     */
    private String currency;

    /**
     * 存取款方式ID
     */
    private String depositWithdrawWayId;

    /**
     * 存取款金额
     */
    private BigDecimal amount;


    /**
     * 手续费
     */
    private BigDecimal feeAmount;

    /**
     * 方式手续费
     */
    private BigDecimal wayFeeAmount;


    /**
     * 结算手续费
     */
    private BigDecimal settlementFeeAmount;

    /**
     * 类型1存款 2取款
     */
    private String type;

    /**
     * 存取款次数
     */
    private Integer nums;

    /**
     * 大额存取款金额
     */
    private BigDecimal largeAmount;

    /**
     * 大额存取款次数
     */
    private Integer largeNums;

    /**
     * 代理代存次数
     */
    private Integer depositSubordinatesNums;

    /**
     * 代理代存金额
     */
    private BigDecimal depositSubordinatesAmount;

    /**
     * 备注
     */
    private String remark;

}

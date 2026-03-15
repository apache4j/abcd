package com.cloud.baowang.report.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

/**
 * 会员存款累计-MQ消息体
 *
 * @author qiqi
 */
@Data
@Accessors(chain = true)
@FieldNameConstants
@TableName("report_user_recharge_withdraw_mq_message")
public class ReportUserRechargeWithdrawMqMessagePO extends BasePO {

    /**
     * 消息类型
     */
    private Integer type;

    /**
     * 消息体
     */
    private String jsonStr;
}

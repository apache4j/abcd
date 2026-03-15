package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

/**
 * 会员打码量-MQ消息体
 *
 * @author qiqi
 */
@Data
@Accessors(chain = true)
@FieldNameConstants
@TableName("user_typing_amount_mq_message")
public class UserTypingAmountMqMessagePO extends BasePO {

    /**
     * 消息类型
     */
    private Integer type;

    /**
     * 消息体
     */
    private String jsonStr;
}

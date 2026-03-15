package com.cloud.baowang.common.kafka.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(title = "会员打码量请求对象")
public class UserTypingAmountRequestVO {

    @Schema(description = "站点编码")
    private String siteCode;

    @Schema(description = "会员ID 必填")
    private String userId;

    @Schema(description = "会员账号")
    private String userAccount;


    @Schema(description = "打码量")
    private BigDecimal typingAmount;

    /**
     * 类型 增加/减少
     * 对应枚举 TypingAmountEnum
     */
    @Schema(description = "1增加 2减少")
    private String  type;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(title = "币种")
    private String currencyCode;
    /**
     * 调整类型
     * 对应枚举 {@link com.cloud.baowang.common.core.enums.wallet.TypingAmountAdjustTypeEnum}
     */
    @Schema(title = "调整类型")
    private String adjustType;

    @Schema(title = "备注-非必要")
    private String remark;
    /**
     * 账号类型 1测试 2正式
     */
    private Integer accountType;


    /**
     * 合并计算对象集合
     */
    private List<UserTypingAmountRequestVO> typingList;

    /**
     * 仅活动
     */
    private Boolean onlyActivity;

    /**
     * 是否清除
     */
    private Boolean isClear;

    /**
     * 活动流水
     */
    private BigDecimal activityTypingAmount;

    /**
     * mq消息id
     */
    private String msgId;

}

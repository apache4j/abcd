package com.cloud.baowang.activity.api.vo.free;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 活动免费旋转基础实体
 */
@Data
public class SiteActivityFreeGameRecordConfigVO  implements Serializable {

    @Schema(description = "会员id")
    private String userId;

    @Schema(description = "会员账号")
    private String userAccount;


    @Schema(description = "获取来源订单号 唯一值 做防重处理; 活动订单号|注单订单号")
    private String orderNo;

    @Schema(description = "订单时间")
    private Long orderTime;

    /**
     * 旋转次数变化类型
     * {@link com.cloud.baowang.play.api.enums.FreeGameChangeTypeEnum}
     */
    @Schema(description = "旋转次数变化类型")
    private Integer type;

    @Schema(description = "活动id")
    private String activityId;

    @Schema(description = "活动编号")
    private String activityNo;

    @Schema(description = "活动模板")
    private String activityTemplate;

    @Schema(description = "获取来源|活动名称")
    private String activityTemplateName;

    @Schema(description = "原次数")
    private Integer beforeNum;

    @Schema(description = "赠送次数")
    private Integer acquireNum;

    @Schema(description = "变更后次数")
    private Integer afterNum;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "平台code")
    private String venueCode;

    @Schema(description = "ip")
    private String ip;


    @Schema(description = "备注")
    private String remark;


    @Schema(description = "可领取开始时间")
    private Long receiveStartTime;

    @Schema(description = "可领取结束时间")
    private Long receiveEndTime;

    @Schema(title = "限注金额")
    private BigDecimal betLimitAmount;

    @Schema(description = "领取状态（已过期）")
    private Integer receiveStatus;

    @Schema(description = "旋转次数余额")
    private Integer balance;

    @Schema(title = "时效-秒")
    private Long timeLimit;
}

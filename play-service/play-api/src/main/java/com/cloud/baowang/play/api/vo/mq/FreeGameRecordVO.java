package com.cloud.baowang.play.api.vo.mq;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "免费游戏类")
@Data
public class FreeGameRecordVO {

    @Schema(description = "会员id")
    private String userId;


    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "站点代码")
    private String siteCode;

    /**
     * FreeGameChangeTypeEnum
     */
    @Schema(description = "类型")
    private Integer type;


    @Schema(description = "获取来源订单号 唯一值 做防重处理")
    private String orderNo;

    /**
     * 活动模板
     */
    @Schema(description = "活动模板")
    private String activityTemplate;

    private String activityId;

    @Schema(description = "活动编号")
    private String activityNo;

    @Schema(description = "获取来源|活动名称")
    private String activityTemplateName;

    @Schema(description = "赠送次数")
    private Integer acquireNum;

    /**
     * 币种
     */
    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "ip")
    private String ip;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 备注
     */
    @Schema(description = "操作者")
    private String operator;


    @Schema(description = "平台code")
    private String venueCode;
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
    private Integer timeLimit;


    /**
     * 场馆
     */
    @Schema(title = "场馆")
    private String gameId;

}

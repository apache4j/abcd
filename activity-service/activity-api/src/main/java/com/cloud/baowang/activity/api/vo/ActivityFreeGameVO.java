package com.cloud.baowang.activity.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "免费游戏类")
@Data
public class ActivityFreeGameVO {

    @Schema(description = "会员id")
    private String userId;


    @Schema(description = "站点代码")
    private String siteCode;


    @Schema(description = "获取来源订单号 唯一值 做防重处理")
    private String orderNo;

    /**
     * 活动模板
     */
    @Schema(description = "活动模板")
    private String activityTemplate;

    private String activityId;

    private String  activityNo;

    @Schema(description = "获取来源|活动名称")
    private String activityTemplateName;

    @Schema(description = "赠送次数")
    private Integer acquireNum;

    /**
     * 币种
     */
    @Schema(description = "币种")
    private String currencyCode;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    // 添加三个
    /**
     * 1.洗码倍数
     */
    private BigDecimal washRatio;
    /**
     * 游戏场馆
     */
    private String venueCode;

    /**
     * 2.游戏code
     */
    private String accessParameters;
    /**
     * 3.限注金额
     */
    private BigDecimal betLimitAmount;

    @Schema(title = "盘口模式:0:国际盘 1:大陆盘")
    private int handicapMode;

}

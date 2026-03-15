package com.cloud.baowang.activity.api.vo.free;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @className: FreeGameRecordReqVO
 * @author: wade
 * @description: 免费旋转
 * @date: 7/6/25 10:36
 */
@Schema(description = "免费游戏类，请求入参")
@Data
public class FreeGameRecordReqVO {

    @Schema(description = "会员id")
    private String userId;


    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "站点代码")
    private String siteCode;

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

    @Schema(description = "平台code")
    private String venueCode;
}

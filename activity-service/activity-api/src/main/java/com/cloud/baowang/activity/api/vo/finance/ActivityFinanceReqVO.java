package com.cloud.baowang.activity.api.vo.finance;

import com.cloud.baowang.common.core.vo.base.SitePageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/10/26 09:50
 * @Version: V1.0
 **/
@Data
@Schema(description = "会员活动记录请求参数")
public class ActivityFinanceReqVO extends SitePageVO {


    @Schema(description = "派发开始时间")
    private Long sendStartTime;
    @Schema(description = "派发结束时间")
    private Long sendEndTime;


    @Schema(description = "领取开始时间")
    private Long receiveStartTime;
    @Schema(description = "领取结束时间")
    private Long receiveEndTime;


    @Schema(description = "过期开始时间")
    private Long overStartTime;
    @Schema(description = "过期结束时间")
    private Long overEndTime;

    @Schema(description = "会员账户")
    private String userAccount;
    //todo 待定
    @Schema(description = "福利类型 公共参数: activity_template_reward ")
    private String activityRewardType;
    @Schema(description = "领取方式 公共参数:activity_distribution_type")
    private String receiveWay;

    @Schema(description = "领取状态 公共参数:activity_receive_status")
    private String receiveStatus;

    @Schema(description = "活动模版 公共参数:activity_template")
    private String activityTemplate;

    @Schema(description = "活动模版列表",hidden = true)
    private List<String> activityTemplates;

    @Schema(description = "活动No")
    private String activityNo;

    @Schema(description = "活动名称")
    private String activityName;


    @Schema(description = "订单号")
    private String activityOrderNo;

    @Schema(description = "账号类型 公共参数:account_type")
    private String accountType;

    private Integer handicapMode;

}

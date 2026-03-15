package com.cloud.baowang.user.api.vo.vip;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "VIP奖励发放记录请求对象")
public class SiteVipAwardRecordReqVo  extends PageVO {
    @Schema(description = "siteCode",hidden = true)
    private String siteCode;
    @Schema(title = "领取开始时间")
    private Long receiveTimeStart;

    @Schema(title = "领取结束时间")
    private Long receiveTimeEnd;

    @Schema(title = "发放开始时间")
    private Long createdTimeStart;

    @Schema(title = "发放结束时间")
    private Long createdTimeEnd;

    @Schema(title = "过期开始时间")
    private Long expiredTimeStart;

    @Schema(title = "过期结束时间")
    private Long expiredTimeEnd;

    @Schema(title = "领取方式 公共参数: vip_receive_type")
    private String receiveType;

    @Schema(title = "领取状态 公共参数:activity_receive_status")
    private String receiveStatus;

    @Schema(title = "订单号")
    private String orderId;

    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "账号类型 公共参数:account_type")
    private String accountType;

    @Schema(title = "奖励类型 award_type")
    private String awardType;

}

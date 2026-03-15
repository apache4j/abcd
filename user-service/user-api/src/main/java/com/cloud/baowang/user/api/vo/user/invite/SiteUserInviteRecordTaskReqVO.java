package com.cloud.baowang.user.api.vo.user.invite;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/11/23 22:03
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "邀请记录请求VO Task")
public class SiteUserInviteRecordTaskReqVO  {
    @Schema(description = "siteCode")
    private String siteCode;
    @Schema(description ="注册开始时间/首存开始时间")
    private Long startTime;
    @Schema(description ="注册结束时间/首存开始时间")
    private Long endTime;
    @Schema(description = "邀请人userId")
    private String userId;
    @Schema(description = "邀请人userId")
    private String userAccount;
    @Schema(description = "首存最小限制金额")
    private BigDecimal targetAmount;
    @Schema(description = "邀请码")
    private String inviteCode;
}

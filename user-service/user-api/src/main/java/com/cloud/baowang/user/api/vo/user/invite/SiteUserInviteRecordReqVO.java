package com.cloud.baowang.user.api.vo.user.invite;

import com.cloud.baowang.common.core.vo.base.BaseVO;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/11/23 22:03
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "邀请记录请求VO")
public class SiteUserInviteRecordReqVO extends PageVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    @Schema(description ="注册开始时间")
    private Long startTime;
    @Schema(description ="注册结束时间")
    private Long endTime;
    @Schema(description = "邀请人账号")
    private String userAccount;
    @Schema(description = "被邀请会员账号")
    private String targetAccount;
    @Schema(description = "邀请码")
    private String inviteCode;
}

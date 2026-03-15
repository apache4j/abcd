package com.cloud.baowang.system.api.vo.site.rebate.user;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


@Data
@Schema(description = "不返水配置返回vo")
@NoArgsConstructor
@AllArgsConstructor
public class UserRebateAuditQueryVO extends PageVO  {

    private String id;

    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "统计时间-开始")
    private Long startTime;

    @Schema(description = "统计时间-结束")
    private Long endTime;

    @Schema(description = "锁单状态 0未锁 1已锁")
    private Integer lockStatus;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户id")
    private String userId;

    @Schema(description = "用户账号")
    private String userAccount;

    @Schema(description = "会员段位code")
    private List<String> vipRankCode;

    @Schema(description = "审核员")
    private String auditAccount;

    @Schema(description = "订单状态 （1-待审核 2-审核中，3-已派发，4-审核拒绝）")
    private Integer auditStatus;

    @Schema(description = "订单状态 （1-申请时间 2-审核时间）")
    private String timeType;


    @Schema(description = "领取时间")
    private Long receiveTime;


    @Schema(description = "币种")
    private String currencyCode;
    @Schema(description = "时区 UTC+8",hidden = true)
    private String timeZone;





}

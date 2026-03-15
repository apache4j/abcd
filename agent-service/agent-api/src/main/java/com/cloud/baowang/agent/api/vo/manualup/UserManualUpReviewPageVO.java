package com.cloud.baowang.agent.api.vo.manualup;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 会员人工加额审核-列表 Request
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Schema(title = "会员人工加额审核-列表 Request")
public class UserManualUpReviewPageVO extends PageVO {

    @Schema(title = "页签标记 1待一审 2待二审")
    @NotNull(message = "页签标记不能为空")
    private Integer review;

    @Schema(title = "申请时间-开始")
    private Long applyTimeStart;

    @Schema(title = "申请时间-结束")
    private Long applyTimeEnd;

    @Schema(title = "订单号")
    private String orderNo;

    @Schema(title = "锁单状态 0未锁 1已锁")
    private Integer lockStatus;

    @Schema(title = "审核员")
    private String locker;

    @Schema(description = "会员ID")
    private String userId;

    @Schema(description = "会员注册信息")
    private String userAccount;
}

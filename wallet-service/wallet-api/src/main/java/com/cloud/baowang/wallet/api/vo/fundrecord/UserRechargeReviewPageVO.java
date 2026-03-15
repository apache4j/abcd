package com.cloud.baowang.wallet.api.vo.fundrecord;

import com.cloud.baowang.common.core.vo.base.PageVO;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * 会员充值人工确认审核-列表 Request
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Schema(title = "会员充值审核-列表 Request")
public class UserRechargeReviewPageVO extends PageVO {

    @Schema(title = "页签标记 1待一审 2待入款")
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

    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "会员注册信息")
    private String userRegister;

    @Schema(title = "存款人姓名")
    private String userName;

    // -------------------------------------------------------------------
    @Schema(title = "数据脱敏 true需要脱敏 false不需要脱敏")
    private Boolean dataDesensitization;
}

package com.cloud.baowang.wallet.api.vo.fundadjust;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 会员人工加额记录 Request
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Schema(title = "会员人工加额记录 Request")
public class UserManualUpRecordPageVO extends PageVO {

    @Schema(description = "siteCode")
    private String siteCode;

    @Schema(description = "userId")
    private String userId;
    @Schema(description = "orderStatus")
    private Integer orderStatus;

    @Schema(title = "申请时间-开始")
    private Long applyStartTime;

    @Schema(title = "申请时间-结束")
    private Long applyEndTime;

    @Schema(title = "订单号")
    private String orderNo;

    @Schema(title = "会员注册信息")
    private String userAccount;

    @Schema(title = "审核状态 system_param review_status code值")
    private Integer auditStatus;
    // 调整类型:3.其他调整,4.会员提款(后台),5.会员VIP优惠,6.会员活动',
    @Schema(title = "调整类型")
    private Integer adjustType;

    @Schema(title = "调整金额-最小值")
    private String adjustAmountMin;

    @Schema(title = "调整金额-最大值")
    private String adjustAmountMax;

    @Schema(title = "是否导出 true 是 false 否")
    private Boolean exportFlag = false;
}

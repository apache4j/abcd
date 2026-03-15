package com.cloud.baowang.wallet.api.vo.fundrecord;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 会员充值审核记录-列表 Request
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Schema(title = "会员充值审核记录-列表 Request")
public class GetUserRechargeRecordPageVO extends PageVO {

    @Schema(title = "申请时间-开始")
    private Long applyTimeStart;

    @Schema(title = "申请时间-结束")
    private Long applyTimeEnd;

    @Schema(title = "订单号")
    private String orderNo;

    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "会员注册信息")
    private String userRegister;

    @Schema(title = "存款人姓名")
    private String userName;

    @Schema(title = "订单状态")
    private Integer orderStatus;
}

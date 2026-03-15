package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 审核信息
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Accessors(chain = true)
@Schema(title = "审核信息")
public class WithdrawReviewInfoVO {

    @Schema(title = "审核人")
    private String auditUser;

    @Schema(title = "审核时间")
    private Long auditTime;

    @Schema(title = "审核信息")
    private String auditInfo;
    @Schema(title = "当前审核节点是第几步")
    private Integer num;
}

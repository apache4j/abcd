package com.cloud.baowang.agent.api.vo.manualup;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 代理加额审核记录-列表 Request
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Schema(title = "代理加额审核记录-列表 Request")
public class AgentGetRecordPageVO extends PageVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    @Schema(title = "申请时间-开始")
    private Long applyTimeStart;

    @Schema(title = "申请时间-结束")
    private Long applyTimeEnd;

    @Schema(title = "审核时间-开始")
    private Long auditTimeStart;
    @Schema(title = "审核时间-结束")
    private Long auditTimeEnd;

    @Schema(title = "订单号")
    private String orderNo;

    @Schema(title = "代理账号")
    private String agentAccount;

    @Schema(title = "钱包类型")
    private Integer walletType;

    @Schema(title = "申请类型")
    private Integer adjustType;

    @Schema(title = "订单状态")
    private Integer orderStatus;
    @Schema(description = "审核人")
    private String oneReviewer;
    @Schema(description = "单据操作", hidden = true)
    private Integer reviewOperation;
}

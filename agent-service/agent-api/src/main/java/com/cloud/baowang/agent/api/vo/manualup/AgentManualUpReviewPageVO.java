package com.cloud.baowang.agent.api.vo.manualup;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 代理人工加额审核-列表 Request
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Schema(title = "代理人工加额审核-列表 Request")
public class AgentManualUpReviewPageVO extends PageVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    @Schema(description = "adminName", hidden = true)
    private String adminName;
    /**
     * {@link com.cloud.baowang.common.core.enums.ReviewOperationEnum}
     */
    @Schema(title = "1.一审审核,2.结单查看")
    private Integer reviewOperation;

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

    @Schema(title = "调整类型")
    private Integer adjustType;

    @Schema(title = "调整金额最小值")
    private BigDecimal adjustAmtMin;

    @Schema(title = "调整金额最大值")
    private Integer adjustAmtMax;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "钱包类型")
    private Integer walletType;
}

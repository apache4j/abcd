package com.cloud.baowang.report.api.vo.userwinlose;


import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 会员盈亏 Request
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Schema( title = "会员盈亏 Request")
public class UserWinLosePageVO extends PageVO {

    @Schema(title =   "开始日期")
    @NotNull(message = "开始日期不能为空")
    private Long startDay;

    @Schema(title =   "结束日期")
    @NotNull(message = "结束日期不能为空")
    private Long endDay;

    @Schema(title =   "会员账号")
    private String userAccount;

    @Schema(title =   "上级代理账号")
    private String superAgentAccount;

    @Schema(title =   "账号类型")
    private Integer accountType;

    @Schema(title =   "注单量-最小值")
    private String betNumMin;
    @Schema(title =   "注单量-最大值")
    private String betNumMax;

    @Schema(title =   "投注金额-最小值")
    private String betAmountMin;
    @Schema(title =   "投注金额-最大值")
    private String betAmountMax;

    @Schema(title =   "投注盈亏-最小值")
    private String betWinLoseMin;
    @Schema(title =   "投注盈亏-最大值")
    private String betWinLoseMax;

    @Schema(title =   "净盈亏-最小值")
    private String profitAndLossMin;
    @Schema(title =   "净盈亏-最大值")
    private String profitAndLossMax;

    @Schema(title =   "代理归属")
    private Integer agentAttribution;

    @Schema(title  = "站点Code")
    private String siteCode;

    @Schema(title  = "主货币")
    private String mainCurrency;



    @Schema(description = "是否转换为平台币")
    private Boolean convertPlatCurrency = Boolean.FALSE;

    @Schema(description = "时区",hidden = true)
    private String timeZone ;
}

package com.cloud.baowang.report.api.vo;


import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Schema(title = "会员报表查询请求Request VO")
public class ReportUserInfoStatementPageVO extends PageVO {
    @Schema(title = "站点code")
    private String siteCode;
    @Schema(title = "统计日期-开始")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Long statisticalDateStart;

    @Schema(title = "统计日期-结束")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Long statisticalDateEnd;

    @Schema(title = "注册时间-开始")
    private Long registerTimeStart;

    @Schema(title = "注册时间-结束")
    private Long registerTimeEnd;

    @NotBlank
    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "上级代理")
    private String superAgentAccount;

    @Schema(title = "账号类型 1测试 2正式 3商务 4置换")
    private String accountType;

    @Schema(title = "注单量最小值")
    private String betMin;

    @Schema(title = "注单量最大值")
    private String betMax;

    @Schema(title = "投注金额最小值")
    private String betAmountMin;

    @Schema(title = "投注金额最大值")
    private String betAmountMax;

    @Schema(title = "有效投注最小值")
    private String activeBetMin;

    @Schema(title = "有效投注最大值")
    private String activeBetMax;

    @Schema(title = "投注盈亏最小值")
    private String bettingProfitLossMin;

    @Schema(title = "投注盈亏最大值")
    private String bettingProfitLossMax;

    @Schema(title = "总存款最小值")
    private String totalDepositMin;

    @Schema(title = "总存款最大值")
    private String totalDepositMax;

    @Schema(title = "总取款最小值")
    private String totalWithdrawalMin;

    @Schema(title = "总取款最大值")
    private String totalWithdrawalMax;

    @Schema(title = "代理归属")
    private Integer agentAttribution;

    @Schema(title = "排序规则：asc，desc")
    private String orderType;
    @Schema(title = "排序字段")
    private String orderField;

    @Schema(title = "是否导出 true 是 false 否")
    private Boolean exportFlag = false;


    @Schema(title = "平台币code")
    private String platCurrencyCode;


    @Schema(title = "当前站点时区", hidden = true)
    private String timeZone;

    @Schema(title = "主货币")
    private String mainCurrency;

    @Schema(description = "是否转换为平台币")
    private Boolean convertPlatCurrency = Boolean.FALSE;


    @Schema(description = "首存时间开始")
    private Long firstDepositTimeStart;

    @Schema(description = "首存时间结束")
    private Long firstDepositTimeEnd;


    @Schema(title = "存款次数最小")
    private Integer numberDepositMin;

    @Schema(title = "存款次数最大")
    private Integer numberDepositMax;


    @Schema(title = "取款次数最小")
    private Integer numberWithdrawalMin;

    @Schema(title = "取款次数最大")
    private Integer numberWithdrawalMax;

}

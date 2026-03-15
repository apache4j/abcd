package com.cloud.baowang.report.api.vo.userwinlose;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author: kimi
 */
@Data
@I18nClass
@Schema(title = "会员盈亏 返回Response")
public class UserWinLoseResponseVO {
    @Schema(description = "siteCode")
    private String siteCode;

    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "会员账号")
    private String userId;

    @Schema(title = "姓名")
    private String userName;

    @Schema(title = "账号类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private Integer accountType;

    @Schema(title = "账号类型")
    private String accountTypeText;


    @Schema(title = "上级代理")
    private String superAgentAccount;

    @Schema(title = "代理归属")
    private Integer agentAttribution;

    @Schema(title = "代理归属-Name")
    private String agentAttributionName;


    @Schema(title = "VIP段位")
    private Integer vipRankCode;

    @I18nField
    @Schema(description = "vip段位名称")
    private String vipRankCodeName;


    @Schema(title = "vip等级")
    private Integer vipGradeCode;

    @Schema(title = "vip等级名称")
    private String vipGradeCodeName;


    @Schema(description = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_STATUS)
    private String accountStatus;
    @Schema(description = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定 - Text")
    private String accountStatusText;
    /*@Schema(title =   "账号状态-Name")
    private List<CodeValueVO> accountStatusName;*/


    /*@Schema(title =   "账号状态 - 用于导出")
    private String accountStatusExport;*/

    @Schema(title = "会员标签")
    private String userLabelId;

    @Schema(title = "会员标签-Name")
    private String userLabelIdName;

    @Schema(title = "会员标签-Name")
    private List<GetUserLabelByIdsResVO> userLabelIdNames;

    @Schema(title = "风控层级")
    private String riskLevelId;

    @Schema(title = "风控层级-Name")
    private String riskLevelIdName;


    @Schema(title = "注单量")
    private Integer betNum;


    @Schema(title = "投注金额")
    private BigDecimal betAmount;


    @Schema(title = "有效投注")
    private BigDecimal validBetAmount;


    @Schema(title = "流水纠正")
    private BigDecimal runWaterCorrect;


    @Schema(title = "投注盈亏")
    private BigDecimal betWinLose;


    @Schema(title = "返水金额")
    private BigDecimal rebateAmount;


    @Schema(title = "优惠金额")
    private BigDecimal activityAmount = BigDecimal.ZERO;

    @Schema(title = "已经使用优惠金额")
    private BigDecimal alreadyUseAmount = BigDecimal.ZERO;

    @Schema(title = "vip优惠金额")
    private BigDecimal vipAmount = BigDecimal.ZERO;


    @Schema(title = "调整金额(其他调整)")
    private BigDecimal adjustAmount = BigDecimal.ZERO;


    @Schema(title = "补单其他调整")
    private BigDecimal repairOrderOtherAdjust = BigDecimal.ZERO;


    @Schema(title = "净盈亏")
    private BigDecimal profitAndLoss = BigDecimal.ZERO;


    @Schema(title = "主货币")
    private String mainCurrency;

    @Schema(title = "主货币")
    private String platCurrencyCode;


    @Schema(description = "同步时间")
    private Long dayHourMillis;

    @Schema(description = "是否转换为平台币")
    private Boolean convertPlatCurrency = Boolean.FALSE;

    @Schema(title = "调整金额(其他调整)-平台币")
    private BigDecimal platAdjustAmount = BigDecimal.ZERO;
    @Schema(title = "打赏金额")
    private BigDecimal tipsAmount = BigDecimal.ZERO;

    @Schema(title = "封控金额-主货币")
    private BigDecimal riskAmount = BigDecimal.ZERO;

}

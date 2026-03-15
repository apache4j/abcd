package com.cloud.baowang.report.api.vo;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.report.api.vo.userwinlose.GetUserLabelByIdsResVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(title = "会员报表返回 VO")
@I18nClass
public class ReportUserInfoStatementVO {

    @Schema(title = "会员Id")
    private String userId;

    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "姓名")
    private String userName;

    @Schema(title = "账号类型 1测试 2正式 ")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private Integer accountType;

    @Schema(title = "账号类型 name")
    private String accountTypeText;

    @Schema(title = "主币种")
    private String mainCurrency;

    @Schema(title = "站点名称")
    private String siteCodeName;

    @Schema(title = "VIP段位")
    private Integer vipRankCode;

    @I18nField
    @Schema(description = "vip段位名称")
    private String vipRankCodeName;


    @Schema(title = "vip等级")
    private Integer vipGradeCode;

    @Schema(title = "vip等级名称")
    private String vipGradeCodeName;

    @Schema(title = "上级代理id")
    private String superAgentId;

    @Schema(title = "上级代理账号")
    private String superAgentAccount;

    @Schema(title = "代理归属")
    private Integer agentAttribution;

    @Schema(title = "代理归属-Name")
    private String agentAttributionName;


    @Schema(title = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_STATUS)
    private String accountStatus;

    @Schema(title = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定 Name")
    private String accountStatusText;

    @Schema(title = "会员标签 id")
    private String userLabelId;

    @Schema(title = "会员标签 id")
    private List<GetUserLabelByIdsResVO> userLabelIds;

    @Schema(title = "会员标签 name")
    private String userLabelName;

    @Schema(title = "风控层级 id")
    private String riskLevelId;

    @Schema(title = "风控层级 name")
    private String riskLevelName;

    @Schema(title = "注册时间")
    private Long registerTime;

    @Schema(title = "注册时间 name")
    private String registerTimeStr;

    @Schema(title = "首存金额")
    private BigDecimal firstDepositAmount;

    public BigDecimal getFirstDepositAmount() {
        if (ObjectUtil.isEmpty(firstDepositAmount)) {
            firstDepositAmount = BigDecimal.valueOf(0);
        }
        return firstDepositAmount;
    }

    @Schema(title = "首存时间")
    private Long firstDepositTime;

    @Schema(title = "总存款")
    private BigDecimal totalDeposit = BigDecimal.ZERO;

    @Schema(title = "存款次数")
    private Integer numberDeposit;

    public Integer getNumberDeposit() {
        if (ObjectUtil.isEmpty(numberDeposit)) {
            numberDeposit = 0;
        }
        return numberDeposit;
    }

    @Schema(title = "上级转入")
    private BigDecimal advancedTransfer = BigDecimal.ZERO;

    public BigDecimal getAdvancedTransfer() {
        if (ObjectUtil.isEmpty(advancedTransfer)) {
            advancedTransfer = BigDecimal.valueOf(0);
        }
        return advancedTransfer;
    }

    @Schema(title = "上级转入次数")
    private Integer numberTransfer = 0;

    @Schema(title = "大额存款金额")
    private BigDecimal amountLargeDeposits = BigDecimal.ZERO;

    @Schema(title = "大额取款次数")
    private Integer numberLargeDeposits = 0;


    /*@Schema(title = "转中心钱包次数")
    private Integer centralWallet = 0;*/

    /*@Schema(title = "转回次数")
    private Integer numberReversals = 0;*/

    @Schema(title = "总取款")
    private BigDecimal totalWithdrawal = BigDecimal.ZERO;

    @Schema(title = "取款次数")
    private Integer numberWithdrawal = 0;

    @Schema(title = "大额取款次数")
    private Integer numberLargeWithdrawal = 0;

    @Schema(title = "大额存款总额")
    private BigDecimal amountLargeWithdrawal = BigDecimal.ZERO;

    @Schema(title = "存取差")
    private BigDecimal poorAccess = BigDecimal.ZERO;

    @Schema(title = "净盈利=净盈利=会员输赢+其他调整+平台币转化金额")
    private BigDecimal totalPreference = BigDecimal.ZERO;

    @Schema(title = "总返水")
    private BigDecimal rebateAmount = BigDecimal.ZERO;

    @Schema(title = "其他调整")
    private BigDecimal otherAdjustments = BigDecimal.ZERO;

    @Schema(title = "注单量")
    private Integer placeOrderQuantity = 0;

    @Schema(title = "投注金额")
    private BigDecimal betAmount = BigDecimal.ZERO;

    @Schema(title = "有效投注金额")
    private BigDecimal activeBet = BigDecimal.ZERO;
    @Schema(title = "优惠金额")
    private BigDecimal activityAmount = BigDecimal.ZERO;
    @Schema(title = "vip金额")
    private BigDecimal vipAmount = BigDecimal.ZERO;
    @Schema(title = "已经使用优惠")
    private BigDecimal alreadyUseAmount = BigDecimal.ZERO;

    /**
     * 投注盈亏就是会员输赢，相对于场馆
     */
    @Schema(title = "投注盈亏就是会员输赢")
    private BigDecimal bettingProfitLoss = BigDecimal.ZERO;

    @Schema(title = "转代次数")
    private Integer transAgentTime;

    @Schema(description = "中心钱包余额")
    private BigDecimal centerAmount_$_totalPlatAmount;

    public BigDecimal getCenterAmount_$_totalPlatAmount() {
        return centerAmount;
    }

    @Schema(title = "中心钱包余额主货币")
    private BigDecimal centerAmount = BigDecimal.ZERO;



    /*@Schema(title = "中心钱包主货币余额")
    private BigDecimal totalMainAmount = BigDecimal.ZERO;*/

    @Schema(title = "中心钱包平台币余额")
    private BigDecimal totalPlatAmount = BigDecimal.ZERO;


    public String getRegisterTimeStr() {
        if (ObjectUtils.isEmpty(this.registerTime)) {
            return StringUtils.EMPTY;
        }
        return TimeZoneUtils.formatTimestampToTimeZone(this.registerTime, CurrReqUtils.getTimezone());
    }

    @Schema(title = "平台币code")
    private String platCurrencyCode;


    @Schema(title = "siteCode")
    private String siteCode;

    @Schema(description = "是否转换为平台币")
    private Boolean convertPlatCurrency = Boolean.FALSE;

    @Schema(title = "registerIp 注册ip")
    private String registerIp;

    @Schema(title = "registerIp 注册ip")
    @ExcelProperty("注册IP归属地")
    private String ipAddress;


    @Schema(title = "registry 注册终端")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REGISTRY)
    private String registry;

    @Schema(title = "registry 注册终端")
    private String registryText;


    @Schema(title = "最后登录时间")
    private Long lastLoginTime;

    @Schema(title = "最后登录时间")
    private String lastLoginTimeStr;

    @Schema(title = "调整金额(其他调整)-平台币")
    private BigDecimal platAdjustAmount = BigDecimal.ZERO;
    @Schema(title = "打赏金额")
    private BigDecimal tipsAmount = BigDecimal.ZERO;

    @Schema(title = "封控金额-主货币")
    private BigDecimal riskAmount = BigDecimal.ZERO;


    public String getLastLoginTimeStr() {
        if (ObjectUtils.isEmpty(this.lastLoginTime)) {
            return StringUtils.EMPTY;
        }
        return TimeZoneUtils.formatTimestampToTimeZone(this.lastLoginTime, CurrReqUtils.getTimezone());
    }


}

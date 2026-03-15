package com.cloud.baowang.user.api.vo.UserDetails;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: kimi
 */
@Data
@Schema(title = "会员详情 ResponseVO")
@I18nClass
public class SelectUserDetailResponseVO {

    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "主货币")
    private String mainCurrency;

    @Schema(title = "vip当前等级")
    private Integer vipGradeCode;
    @Schema(title = "vip当前等级 - Name")
    private String vipGradeCodeName;

    @Schema(title = "是否活跃 0非活跃 1活跃")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ACTIVE_TYPE)
    private Integer isActive;

    @Schema(title = "是否活跃 0非活跃 1活跃")
    private String isActiveText;
    @Schema(title = "是否活跃 0非活跃 1活跃")
    private String isActiveName;

    @Schema(title = "注册时间")
    private Long registerTime;

    @Schema(title = "最后登录时间")
    private Long lastLoginTime;

    @Schema(title = "存款")
    private BigDecimal allDepositAmount = BigDecimal.ZERO;

    @Schema(title = "取款")
    private BigDecimal allWithdrawAmount = BigDecimal.ZERO;

    @Schema(title = "红利")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Schema(title = "有效投注")
    private BigDecimal validBetAmount = BigDecimal.ZERO;

    @Schema(title = "投注金额")
    private BigDecimal betAmount = BigDecimal.ZERO;

    @Schema(title = "总输赢")
    private BigDecimal betWinLose = BigDecimal.ZERO;

    @Schema(title = "净输赢")
    private BigDecimal profitAndLoss = BigDecimal.ZERO;

    @Schema(title = "会员标签")
    private List<String> userLabels;

    @Schema(title = "账号备注")
    private String accountRemark;
    @Schema(title = "邮箱")
    private String email;
    @Schema(title = "区号")
    private String areaCode;
    @Schema(title = "手机号码")
    private String phone;

    @Schema(title = "平台币名称")
    private String platCurrencyCode;


    @Schema(title = "平台币标志")
    private String platCurrencySymbol;

    @Schema(title = "vip福利")
    private BigDecimal vipAmount = BigDecimal.ZERO;

    @Schema(title = "活动优惠")
    private BigDecimal activityAmount = BigDecimal.ZERO;
    @Schema(title = "已经使用优惠")
    private BigDecimal alreadyUseAmount = BigDecimal.ZERO;

    @Schema(title = "返水金额")
    private BigDecimal rebateAmount = BigDecimal.ZERO;


    @Schema(title = "账号备注")
    private String acountRemark;


}

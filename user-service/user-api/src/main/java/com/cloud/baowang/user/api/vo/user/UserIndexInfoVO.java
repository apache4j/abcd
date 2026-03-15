package com.cloud.baowang.user.api.vo.user;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.AppBigDecimalJsonSerializer;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
public class UserIndexInfoVO {

    private String userId;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "vip当前等级段位")
    private Integer vipRank;
    @Schema(description = "当前段位图片，当是华人盘是vip等级图片")
    private String vipIconImage;
    @Schema(description = "当前段位颜色")
    private String rankColor;

    @Schema(description = "下一个段位图片")
    private String nextVipIconImage;
    @Schema(description = "下一个段位颜色")
    private String nextRankColor;

    @Schema(description = "vip下一个等级段位")
    private Integer nextVipRank;

    @Schema(description = "会员ID")
    private String userAccount;

    @Schema(description = "vip当前等级")
    private Integer vipGradeCode;

    @Schema(description = "vip当前等级名称")
    private String vipGradeName;

    @Schema(description = "vip下一等级")
    private Integer vipGradeUp;

    @Schema(description = "vip下一等级名称")
    private String vipGradeUpName;

    @Schema(description = "用户头像CODE")
    private String avatarCode;

    @Schema(description = "用户头像图片相对路径")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String avatar;

    @Schema(description = "用户头像图片完整路径")
    private String avatarFileUrl;

    @Schema(description = "总余额")
    @JsonSerialize(using = AppBigDecimalJsonSerializer.class)
    private BigDecimal totalBalance;

    @Schema(description = "平台币可用余额")
    @JsonSerialize(using = AppBigDecimalJsonSerializer.class)
    private BigDecimal platAvailableAmount;

    @Schema(description = "平台币可用余额是否大于0，大于0返回1，否则返回0，如果曾经兑换过，也返回1")
    private Integer platAmountFlag;

    public Integer getPlatAmountFlag() {
        // 曾经兑换过，也返回1
        if (platAmountFlag != null && platAmountFlag > 0) {
            return 1;
        }
        if (platAvailableAmount == null || platAvailableAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        return 1;
    }

    @Schema(description = "当前经验-cn")
    private BigDecimal currentExperience;

    @Schema(description = "当前等级经验")
    private BigDecimal currentVipExp = BigDecimal.ZERO;

    @Schema(description = "剩余经验（流水）-cn")
    private BigDecimal leftExperience;

    @Schema(description = "下一等级经验-cn")
    private BigDecimal nextExperience;

    @Schema(description = "保级流水金额")
    private BigDecimal finishRelegationAmount;

    @Schema(description = "保级总流水金额")
    private BigDecimal gradeRelegationAmount;

    @Schema(description = "剩余保级流水金额 -cn")
    private BigDecimal leftRelegationAmount = BigDecimal.ZERO;


    @Schema(description = "保级/降级 天数-cn")
    private Integer relegationDays;

    @Schema(description = "还剩多少天降级-cn")
    private Integer leftRelegationDays = 0;

    @Schema(description = "主货币")
    private String mainCurrency;

    @Schema(description = "主货币符号")
    private String currencySymbol;

    @Schema(description = "主货币名称")
    @I18nField()
    private String mainCurrencyName;


    @Schema(description = "主货币标识图片")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String currencyIcon;
    @Schema(description = "主货币标识图片全路径")
    private String currencyIconFileUrl;

    @Schema(description = "消息中心-未读消息数量")
    private int unReadNoticeNums;

    @Schema(description = "是否充提限制 1是 0否")
    private Integer rechargeWithdrawLimit;

    @Schema(description = "是否限制取款 1是 0否")
    private Integer withdrawLimit;


    @Schema(description = "福利中心未领取标识")
    private int welfareNotClaimNums;


    @Schema(description = "意见反馈未读标识")
    private int unReadFeedbackNums;

    @Schema(description = "站点平台币币种信息")
    private String platCurrencyCode;
    @Schema(description = "站点平台币币种信息")
    private String platCurrencyName;
    @Schema(description = "站点平台币币种符号")
    private String platCurrencySymbol;

    @Schema(description = "站点平台币币种图标")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String platCurrencyIcon;

    @Schema(description = "主货币标识图片全路径")
    private String platCurrencyIconFileUrl;

    @Schema(description = "新手指引步骤，如果没有则为0")
    private Integer step;
    @Schema(description = "任务领取状态 0-可领取 1-已领取 2-奖励已过期")
    private Integer receiveStatus;


    @Schema(description = "加入天数")
    private Long joinDays;

    @Schema(description = " handicap模式 0-国际盘 1- 中国盘")
    private Integer handicapMode;

    @Schema(description = "区号")
    private String areaCode;

    @Schema(description = "手机号码")
    private String phone;

}

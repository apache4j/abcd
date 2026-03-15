package com.cloud.baowang.play.api.vo.AbnormalOrder;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@I18nClass
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "异常注单返回对象")
public class OrderAbnormalRecordPageRespVO implements Serializable {
    @Schema(description = "id", title = "id")
    private String id;
    @Schema(description = "注单号", title = "注单号")
    private String orderId;
    @Schema(description = "注单号", title = "注单号")
    private String thirdOrderId;
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_CODE)
    private String venueCode;
    @Schema(description = "游戏场馆")
    private String venueCodeText;
    @Schema(description = "游戏类别")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_TYPE)
    private Integer venueType;
    @Schema(description = "游戏类别-文本")
    private String venueTypeText;
    @Schema(description = "局号/赛事id")
    private String gameNo;
    @Schema(description = "异常类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ABNORMAL_TYPE)
    private Integer abnormalType;
    @Schema(description = "异常类型名称")
    private String abnormalTypeText;
    @Schema(description = "重算状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.RESETTLE_STATUS)
    private Integer reSettleStatus;
    @Schema(description = "重算状态名称")
    private String reSettleStatusText;
    @Schema(description = "重算结果")
    private String reSettleResult;
    @Schema(description = "会员账号")
    private String userAccount;
    @Schema(description = "账号类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private Integer accountType;
    @Schema(description = "账号类型名字")
    private String accountTypeText;
    @Schema(description = "VIP段位")
    private Integer vipRank;
    @Schema(description = "VIP段位文本")
    @I18nField
    private String vipRankText;
    @Schema(description = "VIP等级")
    private Integer vipGradeCode;
    @Schema(description = "VIP等级-文本")
    private String vipGradeText;
    @Schema(description = "国内盘-VIP等级-文本")
    private String zhVipGradeText;

    @Schema(description = "上级代理ID")
    private String agentId;
    @Schema(description = "上级代理账号")
    private String agentAcct;
    @Schema(description = "游戏账号")
    private String casinoUserName;
    @Schema(description = "注单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ORDER_CLASSIFY)
    private Integer orderClassify;
    @Schema(description = "注单状态名称")
    private String orderClassifyText;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "投注额")
    private BigDecimal betAmount;
    @Schema(description = "输赢金额")
    private BigDecimal winLossAmount;
    @Schema(description = "投注时间")
    private Long betTime;
    @Schema(description = "结算时间")
    private Long settleTime;
    @Schema(description = "变更次数")
    private Integer changeCount;
    @Schema(description = "最近变更时间")
    private Long changeTime;
    @Schema(description = "备注")
    private String remark;

    @Schema(description = "游戏场馆/游戏类别 导出使用")
    private String venueCodeText_$_venueTypeText;

    public String getVenueCodeText_$_venueTypeText() {
        return venueCodeText;
    }
}
